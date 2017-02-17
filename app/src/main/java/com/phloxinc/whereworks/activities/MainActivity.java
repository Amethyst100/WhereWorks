package com.phloxinc.whereworks.activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.phloxinc.whereworks.ActivityController;
import com.phloxinc.whereworks.CircleTransform;
import com.phloxinc.whereworks.R;
import com.phloxinc.whereworks.adapters.pager.PagerAdapter;
import com.phloxinc.whereworks.bo.Member;
import com.phloxinc.whereworks.constant.Constants;
import com.phloxinc.whereworks.process.Process;
import com.phloxinc.whereworks.controller.MainController;
import com.phloxinc.whereworks.fragments.DashboardFragment;
import com.phloxinc.whereworks.fragments.ManagementFragment;
import com.phloxinc.whereworks.prefs.Prefs;
import com.phloxinc.whereworks.process.ProcessRequest;
import com.phloxinc.whereworks.utils.Utils;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabSelectedListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMenuTabSelectedListener, ViewPager.OnPageChangeListener, NavigationView.OnNavigationItemSelectedListener, ProcessRequest.RequestListener<Object> {

    private BottomBar bottomBar;
    public ViewPager pager;
    private PagerAdapter adapter;
    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;

    public static final int PAGE_DASHBOARD = 0;
    public static final int PAGE_MAPS = 1;
    public static final int PAGE_MANAGEMENT = 2;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initActionBar();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_menu);
        bottomBar = BottomBar.attach(this, savedInstanceState);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, null, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                bottomBar.show();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                bottomBar.hide();
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        bottomBar.setItemsFromMenu(R.menu.menu_bottom_bar, this);

        String[] tabs = getResources().getStringArray(R.array.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new PagerAdapter(getSupportFragmentManager(), tabs);
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(this);
        pager.setCurrentItem(PAGE_MAPS);

        if (Utils.IsInternetAvailable(this)) {
            new ProcessRequest<>(Process.MEMBER_DETAIL, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        setProfileInfo();
    }

    private void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityController.getInstance().setCurrentActivity(this);
        new ProcessRequest<>(Process.MEMBER_INVITATION_LIST, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        if (Prefs.isExists("Nav")) {
            if (Prefs.getString("Nav", "").contains("Maps")) {
                Prefs.putString("Nav", "");
                Log.e("Main Activity", "Navigate to map");
                pager.setCurrentItem(PAGE_MAPS);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                    mDrawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.END);
                }
                return true;

            case R.id.action_requests:
                startActivity(new Intent(this, InvitationsActivity.class));
                return true;

            case R.id.action_messages:
                startActivity(new Intent(this, ChatListActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMenuItemSelected(@IdRes int itemId) {
        switch (itemId) {
            case R.id.dashboard_item:
                pager.setCurrentItem(PAGE_DASHBOARD);
                break;

            case R.id.maps_item:
                pager.setCurrentItem(PAGE_MAPS);
                break;

            case R.id.management_item:
                pager.setCurrentItem(PAGE_MANAGEMENT);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        bottomBar.selectTabAtPosition(position, true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_profile:
                startActivity(new Intent(this, SelfProfileActivity.class));
                return false;

            case R.id.nav_timeline:
                pager.setCurrentItem(PAGE_DASHBOARD);
                if (adapter.getDashboardFragment() != null) {
                    adapter.getDashboardFragment().setCurrentItem(DashboardFragment.PAGE_TIMELINE);
                }
                if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                    mDrawerLayout.closeDrawer(GravityCompat.END);
                }
                return true;

            case R.id.nav_contacts:
                pager.setCurrentItem(PAGE_MANAGEMENT);
                if (adapter.getManagementFragment() != null) {
                    adapter.getManagementFragment().setCurrentItem(ManagementFragment.PAGE_CONTACTS);
                }
                if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                    mDrawerLayout.closeDrawer(GravityCompat.END);
                }
                return true;

            case R.id.nav_teams:
                pager.setCurrentItem(PAGE_MANAGEMENT);
                if (adapter.getManagementFragment() != null) {
                    adapter.getManagementFragment().setCurrentItem(ManagementFragment.PAGE_TEAMS);
                }
                if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                    mDrawerLayout.closeDrawer(GravityCompat.END);
                }
                return true;

            case R.id.nav_logout:
                MainController.getInstance().clearData();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return false;
        }
        return false;
    }

    private void setProfileInfo() {
        ImageView profileImage = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        TextView profileName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.name);
        TextView profileEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.email);
        TextView profileNumber = (TextView) navigationView.getHeaderView(0).findViewById(R.id.number);
        String name = Prefs.getString("userName", "");
        String email = Prefs.getString("userEmail", "");
        String number = Prefs.getString("userNumber", "");
        String photo = Prefs.getString("userPhoto", "");
        TextDrawable drawable;
        if (!name.isEmpty()) {
            drawable = Utils.getTextDrawable(String.valueOf(name.charAt(0)), name);
        } else {
            drawable = Utils.getTextDrawable("A");
        }
        profileImage.setImageDrawable(drawable);
        Picasso.with(this).load(Constants.MEMBER_IMAGE_URL + photo)
                .transform(new CircleTransform())
                .placeholder(drawable)
                .into(profileImage);
        profileName.setText(name);
        profileEmail.setText(email);
        profileNumber.setText(number);
    }

    @Override
    public void onSuccess(String process, Object result) {
        switch (process) {
            case Process.MEMBER_DETAIL:
                setProfileInfo();
                break;

            case Process.MEMBER_INVITATION_LIST:
                List<Member> memberList = (List<Member>) result;
//                MenuItem item = menu.findItem(R.id.action_requests);
                if (!memberList.isEmpty()) {
//                    item.setIcon(R.drawable.ic_person_notify_24dp);
                    showNotification(memberList.get(0).getFullName(), "You have new contact request");
                }
//                else {
//                    item.setIcon(R.drawable.ic_person_white_24dp);
//                }
                break;
        }
    }

    @Override
    public void onFailure(String process) {

    }

    private void showNotification(String title, String message) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message);

        Intent resultIntent = new Intent(this, InvitationsActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
//        mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        mBuilder.setAutoCancel(true);

        int mNotificationId = 2;
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
