package me.gr.ofoeye.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.PointF
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_eye.view.*
import me.gr.ofoeye.R
import me.gr.ofoeye.util.dp2px
import java.lang.ref.WeakReference

/**
 * Created by gr on 2017/7/18.
 */
class EyeView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val sensorListener = EyeSensorEventListener()
    private val handle = EyeHandle(this)

    private var translationYCount = dp2px(195f)
    private var rotationAngle = 180f
    private var isGone = false

    private var downX = 0f
    private var downY = 0f

    init {
        View.inflate(context, R.layout.view_eye, this)
        setBackgroundResource(R.drawable.bg_eyeview)
        arrow.setOnClickListener { startAnimation() }
        isClickable = true
    }

    fun registerSensorListener() {
        if (!isGone) sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_UI)
    }

    fun unregisterSensorListener() {
        sensorManager.unregisterListener(sensorListener)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.rawX
                downY = ev.rawY
            }
            MotionEvent.ACTION_UP -> {
                val distanceX = Math.abs(ev.rawX - downX)
                val distanceY = Math.abs(ev.rawY - downY)
                if (distanceY > distanceX && distanceY > dp2px(24f)) {
                    if (isGone) {
                        if (ev.rawY - downY < 0) startAnimation()
                    } else {
                        if (ev.rawY - downY > 0) startAnimation()
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun startAnimation() {
        when {
            arrow.rotation == 180f -> {
                translationYCount = 0f
                rotationAngle = 0f
            }
            arrow.rotation == 0f -> {
                translationYCount = dp2px(195f)
                rotationAngle = 180f
            }
            else -> return
        }

        val translationAnimator = ObjectAnimator.ofFloat(this, "translationY", translationYCount)
        val rotationAnimator = ObjectAnimator.ofFloat(arrow, "rotation", rotationAngle)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(translationAnimator, rotationAnimator)
        animatorSet.duration = 300
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                if (arrow.rotation == 180f) {
                    isGone = true
                    unregisterSensorListener()
                } else {
                    isGone = false
                    registerSensorListener()
                }
            }
        })
        animatorSet.start()
    }

    private inner class EyeSensorEventListener : SensorEventListener {
        private var x = 0f
        private var y = 0f
        private val space = dp2px(9f)

        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                x -= event.values[0] * 8
                y += event.values[1] * 8
                if (x > space) x = space
                else if (x < -space) x = -space
                if (y > space) y = space
                else if (y < -space) y = -space

                val point = PointF(x, y)
                val message = handle.obtainMessage()
                message.obj = point
                handle.sendMessage(message)
            }
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }
    }

    private class EyeHandle(eyeView: EyeView) : Handler() {
        private val eyeViewReference=WeakReference<EyeView>(eyeView)

        override fun handleMessage(msg: Message) {
            val eyeView= eyeViewReference.get() ?: return
            val point = msg.obj as PointF
            with(eyeView.left_eye) {
                rotation = point.x
                translationX = point.x
                translationY = point.y
            }
            with(eyeView.right_eye) {
                rotation = point.x
                translationX = point.x
                translationY = point.y
            }
        }
    }
}