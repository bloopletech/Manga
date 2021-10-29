package net.bloople.manga;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

class BookPagerAdapter extends FragmentStateAdapter {
    private Book book;

    BookPagerAdapter(FragmentActivity fa, Book book) {
        super(fa);
        this.book = book;
    }

    @NonNull
    @Override
    public Fragment createFragment(int i) {
        return PageFragment.newInstance(book.pageUrl(i));
    }

    @Override
    public int getItemCount() {
        return book.getPages();
    }
}