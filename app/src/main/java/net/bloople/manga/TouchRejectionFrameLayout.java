package net.bloople.manga;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TouchRejectionFrameLayout extends FrameLayout {
    private int touchRejectionOffset;

    public TouchRejectionFrameLayout(@NonNull Context context) {
        super(context);
    }

    public TouchRejectionFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchRejectionFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TouchRejectionFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TouchRejectionFrameLayout, 0, 0);

        try {
            touchRejectionOffset = a.getDimensionPixelSize(R.styleable.TouchRejectionFrameLayout_touchRejectionOffset,
                0);
        }
        finally {
            a.recycle();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return shouldRejectEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent (MotionEvent ev) {
        return shouldRejectEvent(ev);
    }

    private boolean shouldRejectEvent(MotionEvent ev) {
        return ev.getY() < touchRejectionOffset || ev.getY() > (getHeight() - touchRejectionOffset);
    }
}
