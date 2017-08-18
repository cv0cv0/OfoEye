package me.gr.ofoeye.util

import android.util.TypedValue
import android.view.View

/**
 * Created by gr on 2017/8/18.
 */
fun View.dp2px(dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
}