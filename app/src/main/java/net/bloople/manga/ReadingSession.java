package net.bloople.manga;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

class ReadingSession {
    private Context context;
    private ViewPager pager;
    private Book book;

    ReadingSession(Context context, AppCompatActivity activity, ViewPager pager, Book book) {
        this.context = context;
        this.pager = pager;
        this.book = book;

        PagesPagerAdapter pagesPagerAdapter = new PagesPagerAdapter(activity.getSupportFragmentManager(), this);
        pager.setAdapter(pagesPagerAdapter);

        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                bookmark(position);
            }
        });
    }

    int page() {
        return pager.getCurrentItem();
    }

    void page(int page) {
        pager.setCurrentItem(page);
    }

    void go(int change) {
        pager.setCurrentItem(pager.getCurrentItem() + change);
    }

    void bookmark(int page) {
        BookMetadata bookMetadata = BookMetadata.findOrCreateByBookId(context, book.id());
        bookMetadata.lastReadPosition(page);
        bookMetadata.save(context);
    }

    void resume() {
        BookMetadata bookMetadata = BookMetadata.findOrCreateByBookId(context, book.id());
        page(bookMetadata.lastReadPosition());
    }

    void finish() {
        if(page() == book.pages - 1)  bookmark(0);
    }

    int count() {
        return book.pages;
    }

    String url(int page) {
        return book.pageUrl(page);
    }
}
