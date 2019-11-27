package net.bloople.manga;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

class PagesPagerAdapter extends FragmentStatePagerAdapter {
    private ReadingSession session;

    PagesPagerAdapter(FragmentManager fm, ReadingSession session) {
        super(fm);
        this.session = session;
    }

    @Override
    public Fragment getItem(int i) {
        return PageFragment.newInstance(session.url(i));
    }

    @Override
    public int getCount() {
        return session.count();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "OBJECT " + (position + 1);
    }
}
