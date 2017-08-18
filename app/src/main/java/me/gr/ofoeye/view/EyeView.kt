package me.gr.ofoeye.view

import android.content.Context
import android.graphics.Point
import android.graphics.PointF
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.view_eye.view.*
import me.gr.ofoeye.R
import me.gr.ofoeye.util.dp2px

/**
 * Created by gr on 2017/7/18.
 */
class EyeView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val sensorListener = EyeSensorEventListener()
    private val handle=EyeHandle()

    init {
        View.inflate(context, R.layout.view_eye, this)
        setBackgroundResource(R.drawable.bg_eyeview)
    }

    fun registerSensorListener() {
        sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_UI)
    }

    fun unregisterSensorListener() {
        sensorManager.unregisterListener(sensorListener)
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

                val point=PointF(x,y)
                val message=handle.obtainMessage()
                message.obj=point
                handle.sendMessage(message)
            }
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }
    }

    private inner class EyeHandle: Handler() {
        override fun handleMessage(msg: Message) {
            val point=msg.obj as PointF
            with(left_eye) {
                rotation = point.x
                translationX = point.x
                translationY = point.y
            }
            with(right_eye) {
                rotation = point.x
                translationX = point.x
                translationY = point.y
            }
        }
    }
}