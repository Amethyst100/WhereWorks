package com.phloxinc.whereworks.adapters.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.phloxinc.whereworks.fragments.MapsFragment;
import com.phloxinc.whereworks.fragments.ManagementFragment;
import com.phloxinc.whereworks.fragments.DashboardFragment;

public class PagerAdapter extends FragmentPagerAdapter {

    private final String[] tabs;
    private DashboardFragment dashboardFragment;
    private MapsFragment mapsFragment;
    private ManagementFragment managementFragment;

    public PagerAdapter(FragmentManager fm, String[] tabs) {
        super(fm);
        this.tabs = tabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                dashboardFragment = new DashboardFragment();
                return dashboardFragment;
            case 1:
                mapsFragment = new MapsFragment();
                return mapsFragment;
            case 2:
                managementFragment = new ManagementFragment();
                return managementFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabs.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }

    public DashboardFragment getDashboardFragment() {
        return dashboardFragment;
    }

    public MapsFragment getMapsFragment() {
        return mapsFragment;
    }

    public ManagementFragment getManagementFragment() {
        return managementFragment;
    }
}
