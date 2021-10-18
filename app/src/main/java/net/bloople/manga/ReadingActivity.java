package net.bloople.manga;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
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

            session = new ReadingSession(getApplicationContext(), library, library.getBooks().get(bookId));
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables "sticky immersive" mode
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}
