package net.bloople.manga;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import net.bloople.manga.audit.BooksAuditor;

class ReadingSession {
    private static final int CACHE_PAGES_LIMIT = 5;
    private Context context;
    private ViewPager2 pager;
    private Library library;
    private Book book;
    private BookMetadata metadata;
    private BooksAuditor auditor;

    ReadingSession(Context context, Library library, Book book) {
        this.context = context;
        this.library = library;
        this.book = book;
        metadata = BookMetadata.findOrCreateByBookId(context, book.getId());
        auditor = new BooksAuditor(context);
    }

    void start() {
        metadata.lastOpenedAt(System.currentTimeMillis());
        metadata.openedCount(metadata.openedCount() + 1);
        metadata.save(context);

        auditor.opened(library, book, page());
    }

    @SuppressLint("WrongConstant")
    void bind(FragmentActivity fa, ViewPager2 pager) {
        this.pager = pager;

        pager.setAdapter(new BookPagerAdapter(fa, book));

        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                bookmark(position);
            }
        });

        pager.setOffscreenPageLimit(CACHE_PAGES_LIMIT);
    }

    int page() {
        return pager.getCurrentItem();
    }

    void page(int page) {
        pager.setCurrentItem(page, false);
    }

    void go(int change) {
        pager.setCurrentItem(pager.getCurrentItem() + change, false);
    }

    private void bookmark(int page) {
        metadata.lastReadPosition(page);
        metadata.save(context);
    }

    void resume() {
        page(metadata.lastReadPosition());
    }

    void finish() {
        if(page() == book.getPages() - 1) bookmark(0);
        auditor.closed(library, book, page());
    }
}
