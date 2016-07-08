package net.bloople.manga;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private Book book;
    private int currentPage;
    private RelativeLayout holder;
    private RequestListener requestListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        Intent intent = getIntent();
        int key = intent.getIntExtra("key", 0);

        book = MangaApplication.books.get(key);
        currentPage = 0;

        holder = (RelativeLayout)findViewById(R.id.image_view_holder);

        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPage == book.pages() - 1) return;
                currentPage++;
                showCurrentPage();
            }
        });

        requestListener = new RequestListener<Uri, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, Uri model, Target<GlideDrawable> target,
                                       boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target,
                                           boolean isFromMemoryCache, boolean isFirstResource) {
                for(int i = 0; i < (holder.getChildCount() - 1); i++) holder.removeViewAt(0);
                return false;
            }
        };

        showCurrentPage();
    }

    private void showCurrentPage() {
        ImageView imageView = (ImageView)LayoutInflater.from(this)
                .inflate(R.layout.reading_image_view, holder, false);

        holder.addView(imageView);

        Glide.with(this).load(book.pageUrl(currentPage))
                .diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate().listener(requestListener).into(imageView);

        if ((currentPage + 1) < book.pages()) {
            Glide.with(this).load(book.pageUrl(currentPage + 1)).diskCacheStrategy(DiskCacheStrategy.SOURCE).preload();
        }
    }
}
