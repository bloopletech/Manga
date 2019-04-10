package net.bloople.manga;

import android.os.SystemClock;
import android.view.View;

abstract class ThrottledOnClickListener implements View.OnClickListener {
    static final long THRESHOLD_MILLIS = 1000L;
    private long lastClickMillis;

    @Override public void onClick(View v) {
        long now = SystemClock.elapsedRealtime();
        if(now - lastClickMillis > THRESHOLD_MILLIS) onThrottledClick(v);
        lastClickMillis = now;
    }

    abstract void onThrottledClick(View v);

    static View.OnClickListener wrap(final View.OnClickListener clickListener) {
        return new ThrottledOnClickListener() {
            @Override
            void onThrottledClick(View v) {
                clickListener.onClick(v);
            }
        };
    }
}