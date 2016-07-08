package net.bloople.manga;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

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
        currentPage = 1;

        imageView = (ImageView)findViewById(R.id.image_view);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage++;
                showCurrentPage();
            }
        });

        showCurrentPage();
    }

    private void showCurrentPage() {
        Uri uri = MangaApplication.root().buildUpon().appendEncodedPath(book.getUrl())
                    .appendEncodedPath(book.getPageUrls().get(currentPage - 1)).build();

        Glide.with(this).load(uri.toString()).into(imageView);
    }
}
