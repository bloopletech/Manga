package net.bloople.manga;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class ReadingActivity extends AppCompatActivity {
    private Book book;
    private int currentPage;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        Intent intent = getIntent();
        int key = intent.getIntExtra("key", 0);

        book = MangaApplication.books.get(key);
        currentPage = 0;

        imageView = (ImageView)findViewById(R.id.image_view);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPage == book.pages() - 1) return;
                currentPage++;
                showCurrentPage();
            }
        });

        showCurrentPage();
    }

    private void showCurrentPage() {
        Glide.with(this).load(book.pageUrl(currentPage)).dontAnimate().into(imageView);

        if ((currentPage + 1) < book.pages()) {
            Glide.with(this).load(book.pageUrl(currentPage + 1)).diskCacheStrategy(DiskCacheStrategy.SOURCE).preload();
        }
    }
}
