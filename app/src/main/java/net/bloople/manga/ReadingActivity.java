package net.bloople.manga;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

public class ReadingActivity extends AppCompatActivity {
    private ReadingSession session;
    private ViewPager2 pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reading);

        pager = findViewById(R.id.pager);

        ImageView prev10 = findViewById(R.id.prev_10);
        prev10.setOnClickListener(ThrottledOnClickListener.wrap(v -> session.go(-10)));

        ImageView next10 = findViewById(R.id.next_10);
        next10.setOnClickListener(ThrottledOnClickListener.wrap(v -> session.go(10)));

        final Intent intent = getIntent();
        long libraryId = intent.getLongExtra("libraryId", -1);

        LibraryService.ensureLibrary(this, libraryId, library -> {
            if(library == null) return;
            long bookId = intent.getLongExtra("_id", -1);

            session = new ReadingSession(getApplicationContext(), library, library.books().get(bookId));
            session.bind(this, pager);

            if(intent.getBooleanExtra("resume", false)) session.resume();

            if(savedInstanceState != null) {
                int pageFromBundle = savedInstanceState.getInt("page", -1);
                if(pageFromBundle != -1) session.page(pageFromBundle);
            }

            session.start();
        });
    }

    @Override
    public void onRestart() {
        super.onRestart();
        session.start();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("page", session.page());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        session.finish();
        super.onStop();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfiguration) {
        super.onConfigurationChanged(newConfiguration);

        int currentItem = pager.getCurrentItem();
        pager.setAdapter(pager.getAdapter());
        pager.setCurrentItem(currentItem, false);
    }
}
