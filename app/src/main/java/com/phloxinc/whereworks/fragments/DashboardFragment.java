package com.phloxinc.whereworks.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.phloxinc.whereworks.R;
import com.phloxinc.whereworks.adapters.pager.HomePagerAdapter;

public class DashboardFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    private ViewPager viewPager;

    public static final int PAGE_TIMELINE = 0;
    public static final int PAGE_NOTIFICATION = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_timeline, container, false);

        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Timeline"));
        tabLayout.addTab(tabLayout.newTab().setText("Notifications"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) rootView.findViewById(R.id.pager);
        HomePagerAdapter adapter = new HomePagerAdapter(getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(this);

        return rootView;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public void setCurrentItem(int position) {
        if (viewPager != null) {
            viewPager.setCurrentItem(position);
        }
    }
}
