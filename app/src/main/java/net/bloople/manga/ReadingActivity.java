package net.bloople.manga;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class ReadingActivity extends AppCompatActivity {
    private ReadingSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reading);

        final ViewPager pager = findViewById(R.id.pager);

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
            session.bind(getSupportFragmentManager(), pager);
            session.start();

            if(intent.getBooleanExtra("resume", false)) session.resume();

            if(savedInstanceState != null) {
                int pageFromBundle = savedInstanceState.getInt("page", -1);
                if(pageFromBundle != -1) session.page(pageFromBundle);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishSession();
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishSession();
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

    private void finishSession() {
        if(session != null) session.finish();
        finish();
    }
}
