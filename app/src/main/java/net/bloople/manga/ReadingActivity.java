package net.bloople.manga;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
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

public class ReadingActivity extends AppCompatActivity {
    public static final int MAX_IMAGE_SIZE = 2048;

    private Book book;
    private int currentPage;
    private RelativeLayout holder;
    private RequestListener<Uri, GlideDrawable> requestListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reading);

        Intent intent = getIntent();
        int key = intent.getIntExtra("key", 0);
        book = MangaApplication.books.get(key);

        if(savedInstanceState != null) currentPage = savedInstanceState.getInt("currentPage");
        else currentPage = 0;

        holder = (RelativeLayout)findViewById(R.id.image_view_holder);

        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPage == book.pages() - 1) return;
                currentPage++;
                showCurrentPage();
                cacheNextPage();
            }
        });

        requestListener = new LoadedRequestListener();

        showCurrentPage();
        cacheNextPage();
    }

    @Override
    public void onBackPressed() {
        currentPage--;
        if(currentPage == -1) finish();
        else showCurrentPage();
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
    }

    private void cacheNextPage() {
        if ((currentPage + 1) >= book.pages()) return;

        Glide
                .with(this)
                .load(book.pageUrl(currentPage + 1))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .preload();
    }

    private class LoadedRequestListener implements RequestListener<Uri, GlideDrawable> {
        @Override
        public boolean onException(Exception e, Uri model, Target<GlideDrawable> target,
        boolean isFirstResource) {
            e.printStackTrace();
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
