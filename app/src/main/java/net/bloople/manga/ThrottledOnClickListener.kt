package net.bloople.manga

import android.os.SystemClock
import android.view.View

abstract class ThrottledOnClickListener : View.OnClickListener {
    private var lastClickMillis: Long = 0

    override fun onClick(v: View) {
        val now = SystemClock.elapsedRealtime()
        if(now - lastClickMillis > THRESHOLD_MILLIS) onThrottledClick(v)
        lastClickMillis = now
    }

    abstract fun onThrottledClick(v: View?)

    companion object {
        const val THRESHOLD_MILLIS = 1000L

        @JvmStatic
        fun wrap(clickListener: View.OnClickListener): View.OnClickListener {
            return object : ThrottledOnClickListener() {
                override fun onThrottledClick(v: View?) {
                    clickListener.onClick(v)
                }
            }
        }
    }
}