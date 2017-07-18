package net.bloople.manga;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class ReadingActivity extends Activity {
    public static final int MAX_IMAGE_SIZE = 2048;

    private Book book;
    private BookMetadata bookMetadata;
    private int currentPage;
    private RelativeLayout holder;
    private RequestListener<Uri, GlideDrawable> requestListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reading);

        Intent intent = getIntent();
        long bookId = intent.getLongExtra("_id", -1);
        book = MangaApplication.allBooks.get(bookId);
        bookMetadata = BookMetadata.findOrCreateByBookId(getApplicationContext(), bookId);

        boolean resume = intent.getBooleanExtra("resume", false);

        View.OnClickListener nextListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!changePage(1)) return;
                showCurrentPage();
                cacheNextPage();
            }
        };

        holder = (RelativeLayout)findViewById(R.id.image_view_holder);
        holder.setOnClickListener(nextListener);

        ImageView prev10 = (ImageView)findViewById(R.id.prev_10);
        prev10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePage(-10);
                showCurrentPage();
            }
        });

        ImageView next10 = (ImageView)findViewById(R.id.next_10);
        next10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePage(10);
                showCurrentPage();
                cacheNextPage();
            }
        });

        requestListener = new LoadedRequestListener();

        int lastReadPosition = resume ? bookMetadata.lastReadPosition() : 0;
        int newPage = savedInstanceState != null ? savedInstanceState.getInt("currentPage") : lastReadPosition;
        if(changePage(newPage)) {
            showCurrentPage();
            cacheNextPage();
        }
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

    private void showCurrentPage() {
        ImageView imageView = (ImageView)LayoutInflater.from(this)
                .inflate(R.layout.reading_image_view, holder, false);

        holder.addView(imageView);

        Glide
                .with(this)
                .load(book.pageUrl(currentPage))
                .override(MAX_IMAGE_SIZE, MAX_IMAGE_SIZE)
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
                .load(book.pageUrl(currentPage + 1))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .preload();
    }

    private boolean changePage(int change) {
        currentPage += change;

        if(currentPage < 0) {
            currentPage = 0;
            return false;
        }

        if(currentPage > (book.pages() - 1)) {
            currentPage = book.pages() - 1;
            return false;
        }

        return true;
    }

    private class LoadedRequestListener implements RequestListener<Uri, GlideDrawable> {
        @Override
        public boolean onException(Exception e, Uri model, Target<GlideDrawable> target,
        boolean isFirstResource) {
            if(e != null) e.printStackTrace();
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target,
        boolean isFromMemoryCache, boolean isFirstResource) {
            for(int i = 0; i < (holder.getChildCount() - 1); i++) holder.removeViewAt(0);
            return false;
        }
    }
}
