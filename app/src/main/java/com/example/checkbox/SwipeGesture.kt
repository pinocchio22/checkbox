package com.example.checkbox

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import java.lang.Exception

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-10-25
 * @desc
 */
class SwipeGesture(v : View) : GestureDetector.OnGestureListener {
    private val SWIPE_THRESHOLD = 100
    private val SWIPE_VELOCITY_THRESHOLD = 100
    val month_left_button = v.findViewById<AppCompatImageButton>(R.id.cal_month_left)
    val month_right_button = v.findViewById<AppCompatImageButton>(R.id.cal_month_right)
    val v = v

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        var result = false
        try {
            val diffY = e2!!.y - e1!!.y
            val diffX = e2.x - e1.x
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight()
                    } else {
                        onSwipeLeft()
                    }
                }
                result = true
            }
            result = true
        } catch (exception : Exception) {
            exception.printStackTrace()
        }
        return result
    }

    fun onSwipeRight() {
        val month_left_button = v.findViewById<AppCompatImageButton>(R.id.cal_month_left)
        month_left_button.performClick()
    }

    fun onSwipeLeft() {
        val month_right_button = v.findViewById<AppCompatImageButton>(R.id.cal_month_right)
        month_right_button.performClick()
    }
}







