package net.bloople.manga;

import android.content.Context;
import android.os.AsyncTask;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

class ReadingSession {
    public static final int CACHE_PAGES_LIMIT = 5;
    private Context context;
    private ViewPager pager;
    private Book book;

    ReadingSession(Context context, Book book) {
        this.context = context;
        this.book = book;
    }

    void bind(FragmentManager fm, ViewPager pager) {
        this.pager = pager;

        pager.setAdapter(new BookPagerAdapter(fm));

        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                bookmark(position);
                PageFragment.cacheUrls(context, cacheNextUrls(position));
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

    private ArrayList<String> cacheNextUrls(int currentPage) {
        ArrayList<String> cacheUrls = new ArrayList<>();
        for(int i = currentPage + 1, j = 0; i < book.pages && j < CACHE_PAGES_LIMIT; i++, j++) cacheUrls.add(book.pageUrl(i));
        return cacheUrls;
    }

    void resume() {
        BookMetadata bookMetadata = BookMetadata.findOrCreateByBookId(context, book.id());
        page(bookMetadata.lastReadPosition());
    }

    void finish() {
        if(page() == book.pages - 1)  bookmark(0);
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
