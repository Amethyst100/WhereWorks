package com.phloxinc.whereworks.adapters.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.phloxinc.whereworks.fragments.dashboard.NotificationFragment;
import com.phloxinc.whereworks.fragments.dashboard.TimelineFragment;

public class HomePagerAdapter extends FragmentStatePagerAdapter {

    private final int tabCount;

    public HomePagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TimelineFragment();
            case 1:
                return new NotificationFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
