package net.bloople.manga;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import net.bloople.manga.audit.BooksAuditor;

class ReadingSession {
    private static final int CACHE_PAGES_LIMIT = 5;
    private Context context;
    private ViewPager pager;
    private Library library;
    private Book book;
    private BooksAuditor auditor;

    ReadingSession(Context context, Library library, Book book) {
        this.context = context;
        this.library = library;
        this.book = book;
        auditor = new BooksAuditor(context);
    }

    void start() {
        BookMetadata metadata = BookMetadata.findOrCreateByBookId(context, book.id());
        metadata.lastOpenedAt(System.currentTimeMillis());
        metadata.save(context);

        auditor.opened(library, book);
    }

    void bind(FragmentManager fm, ViewPager pager) {
        this.pager = pager;

        pager.setAdapter(new BookPagerAdapter(fm));

        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
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
        pager.setCurrentItem(page);
    }

    void go(int change) {
        pager.setCurrentItem(pager.getCurrentItem() + change);
    }

    private void bookmark(int page) {
        BookMetadata bookMetadata = BookMetadata.findOrCreateByBookId(context, book.id());
        bookMetadata.lastReadPosition(page);
        bookMetadata.save(context);
        auditor.bookmarked(library, book, page);
    }

    void resume() {
        BookMetadata bookMetadata = BookMetadata.findOrCreateByBookId(context, book.id());
        page(bookMetadata.lastReadPosition());
        auditor.resumed(library, book, bookMetadata.lastReadPosition());
    }

    void finish() {
        if(page() == book.pages - 1) bookmark(0);
        auditor.closed(library, book);
    }

    class BookPagerAdapter extends FragmentStatePagerAdapter {
        BookPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return PageFragment.newInstance(book.pageUrl(i));
        }

        @Override
        public int getCount() {
            return book.pages;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }
}
