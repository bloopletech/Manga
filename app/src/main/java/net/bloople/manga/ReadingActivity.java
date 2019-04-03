package net.bloople.manga;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
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

public class ReadingActivity extends Activity implements BooksLoadedListener {
    public static final String MAX_IMAGE_DIMENSION = "1500";
    private Book book;
    private BookMetadata bookMetadata;
    private int pageFromBundle = -1;
    private int currentPage;
    private FrameLayout holder;
    private RequestListener<GlideUrl, GlideDrawable> requestListener;
    private ScrollView scroller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) pageFromBundle = savedInstanceState.getInt("currentPage");

        setContentView(R.layout.activity_reading);

        holder = (FrameLayout)findViewById(R.id.image_view_holder);
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(book == null) return;
                if(!changePage(1)) return;
                showCurrentPage();
                cacheNextPage();
            }
        });

        scroller = (ScrollView)findViewById(R.id.scroller);

        final FrameLayout layout = (FrameLayout)findViewById(R.id.layout);
        final Space scroller_fill = (Space)findViewById(R.id.scroller_fill);

        scroller_fill.post(new Runnable() {
            @Override
            public void run() {
                scroller_fill.setMinimumHeight(layout.getHeight());
            }
        });

        ImageView prev10 = (ImageView)findViewById(R.id.prev_10);
        prev10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(book == null) return;
                changePage(-10);
                showCurrentPage();
            }
        });

        ImageView next10 = (ImageView)findViewById(R.id.next_10);
        next10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(book == null) return;
                changePage(10);
                showCurrentPage();
                cacheNextPage();
            }
        });

        requestListener = new LoadedRequestListener();

        Intent intent = getIntent();
        String root = intent.getStringExtra("root");
        Mango.ensureCurrent(Uri.parse(root),this);
    }

    @Override
    public void onBackPressed() {
        if(changePage(-1)) showCurrentPage();
        else finish();
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
        super.onSaveInstanceState(outState);
        outState.putInt("currentPage", currentPage);
    }

    public void onBooksLoaded() {
        Intent intent = getIntent();
        long bookId = intent.getLongExtra("_id", -1);
        book = Mango.current.books().get(bookId);
        bookMetadata = BookMetadata.findOrCreateByBookId(getApplicationContext(), bookId);

        int newPage = 0;
        if(pageFromBundle != -1) {
            newPage = pageFromBundle;
        }
        else if(intent.getBooleanExtra("resume", false)) {
            int lastReadPosition = bookMetadata.lastReadPosition();
            if(lastReadPosition < lastPage()) newPage = lastReadPosition;
        }

        if(changePage(newPage)) {
            showCurrentPage();
            cacheNextPage();
        }
    }

    private void showCurrentPage() {
        ImageView imageView = (ImageView)LayoutInflater.from(this)
                .inflate(R.layout.reading_image_view, holder, false);

        holder.addView(imageView);

        Glide
                .with(this)
                .load(urlWithContentHint(book.pageUrl(currentPage)))
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .listener(requestListener)
                .into(imageView);

        bookMetadata.lastReadPosition(currentPage);
        bookMetadata.save(getApplicationContext());
    }

    private void cacheNextPage() {
        if ((currentPage + 1) >= book.pages()) return;

        Glide
                .with(this)
                .load(urlWithContentHint(book.pageUrl(currentPage + 1)))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .preload();
    }

    private boolean changePage(int change) {
        currentPage += change;

        if(currentPage < 0) {
            currentPage = 0;
            return false;
        }

        if(currentPage > lastPage()) {
            currentPage = lastPage();
            return false;
        }

        return true;
    }

    private int lastPage() {
        return book.pages() - 1;
    }

    private GlideUrl urlWithContentHint(Uri uri) {
        return new GlideUrl(uri.toString(), new LazyHeaders.Builder()
                .addHeader("Width", MAX_IMAGE_DIMENSION)
                .build());
    }

    private class LoadedRequestListener implements RequestListener<GlideUrl, GlideDrawable> {
        @Override
        public boolean onException(Exception e, GlideUrl model, Target<GlideDrawable> target,
        boolean isFirstResource) {
            if(e != null) e.printStackTrace();
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, GlideUrl model, Target<GlideDrawable> target,
        boolean isFromMemoryCache, boolean isFirstResource) {
            scroller.scrollTo(0, 0);

            for(int i = 1; i < (holder.getChildCount() - 1); i++) holder.removeViewAt(1);

            return false;
        }
    }
}
