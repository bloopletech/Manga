package net.bloople.manga

import android.content.Context
import android.widget.FrameLayout
import android.util.AttributeSet
import android.view.MotionEvent

class TouchRejectionFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val touchRejectionOffset: Int

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.TouchRejectionFrameLayout, 0, 0)

        touchRejectionOffset = try {
            a.getDimensionPixelSize(
                R.styleable.TouchRejectionFrameLayout_touchRejectionOffset,
                0
            )
        }
        finally {
            a.recycle()
        }
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return shouldRejectEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return shouldRejectEvent(ev)
    }

    private fun shouldRejectEvent(ev: MotionEvent): Boolean {
        return ev.y < touchRejectionOffset || ev.y > height - touchRejectionOffset
    }
}