package net.bloople.manga;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Space;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class ReadingActivity extends Activity {
    public static final String MAX_IMAGE_DIMENSION = "1500";
    private ReadingSession session;
    private FrameLayout holder;
    private RequestListener<GlideUrl, GlideDrawable> requestListener;
    private ScrollView scroller;
    private boolean loadingImage = false;
    private long lastBackPressMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reading);

        holder = findViewById(R.id.image_view_holder);
        holder.setOnClickListener(ThrottledOnClickListener.wrap(v -> navigateAndShow(1)));

        scroller = findViewById(R.id.scroller);

        final FrameLayout layout = findViewById(R.id.layout);
        final Space scroller_fill = findViewById(R.id.scroller_fill);

        scroller_fill.post(() -> scroller_fill.setMinimumHeight(layout.getHeight()));

        ImageView prev10 = findViewById(R.id.prev_10);
        prev10.setOnClickListener(ThrottledOnClickListener.wrap(v -> navigateAndShow(-10)));

        ImageView next10 = findViewById(R.id.next_10);
        next10.setOnClickListener(ThrottledOnClickListener.wrap(v -> navigateAndShow(10)));

        requestListener = new LoadedRequestListener();

        final Intent intent = getIntent();
        long libraryRootId = intent.getLongExtra("libraryRootId", -1);

        LibraryService.ensureLibrary(this, libraryRootId, library -> {
            if(library == null) return;
            long bookId = intent.getLongExtra("_id", -1);
            session = new ReadingSession(getApplicationContext(), library.books().get(bookId));

            if(intent.getBooleanExtra("resume", false)) session.resume();

            if(savedInstanceState != null) {
                int pageFromBundle = savedInstanceState.getInt("page", -1);
                if(pageFromBundle != -1) session.page(pageFromBundle);
            }

            showCurrentPage();
        });
    }

    @Override
    public void onBackPressed() {
        long now = SystemClock.elapsedRealtime();
        if(now - lastBackPressMillis > ThrottledOnClickListener.THRESHOLD_MILLIS) onThrottledBackPress();
        lastBackPressMillis = now;
    }

    private void onThrottledBackPress() {
        if(session == null || session.isBeginning()) finish();
        else navigateAndShow(-1);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            event.startTracking();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("page", session.page());
        super.onSaveInstanceState(outState);
    }

    private void navigateAndShow(int change) {
        if(loadingImage) return;
        if(session == null) return;
        session.go(change);
        showCurrentPage();
    }

    private void showCurrentPage() {
        loadingImage = true;

        cacheNextPage();

        ImageView imageView = (ImageView)LayoutInflater.from(this)
                .inflate(R.layout.reading_image_view, holder, false);

        holder.addView(imageView);

        Glide
                .with(this)
                .load(urlWithContentHint(session.url()))
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .listener(requestListener)
                .into(imageView);

        session.bookmark();
    }

    private void cacheNextPage() {
        Glide
                .with(this)
                .load(urlWithContentHint(session.url(session.nextPage())))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .preload();
    }

    private GlideUrl urlWithContentHint(String uri) {
        return new GlideUrl(uri, new LazyHeaders.Builder()
                .addHeader("Width", MAX_IMAGE_DIMENSION)
                .build());
    }

    private class LoadedRequestListener implements RequestListener<GlideUrl, GlideDrawable> {
        @Override
        public boolean onException(Exception e, GlideUrl model, Target<GlideDrawable> target,
        boolean isFirstResource) {
            loadingImage = false;
            if(e != null) e.printStackTrace();
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, GlideUrl model, Target<GlideDrawable> target,
        boolean isFromMemoryCache, boolean isFirstResource) {
            scroller.scrollTo(0, 0);

            for(int i = 1; i < (holder.getChildCount() - 1); i++) holder.removeViewAt(1);

            loadingImage = false;

            return false;
        }
    }
}
