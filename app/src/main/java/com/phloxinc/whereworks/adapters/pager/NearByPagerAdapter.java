package com.phloxinc.whereworks.adapters.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.phloxinc.whereworks.fragments.managment.ContactsFragment;
import com.phloxinc.whereworks.fragments.managment.TeamsFragment;

public class NearByPagerAdapter extends FragmentStatePagerAdapter {

    private final int tabCount;

    public NearByPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ContactsFragment();
            case 1:
                return new TeamsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
