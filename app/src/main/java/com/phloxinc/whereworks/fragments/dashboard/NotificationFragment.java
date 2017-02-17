package com.phloxinc.whereworks.fragments.dashboard;

import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.phloxinc.whereworks.R;
import com.phloxinc.whereworks.adapters.NotificationAdapter;
import com.phloxinc.whereworks.bo.Notification;
import com.phloxinc.whereworks.process.Process;
import com.phloxinc.whereworks.process.ProcessRequest;
import com.phloxinc.whereworks.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment implements ProcessRequest.RequestListener<List<Notification>>, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private SwipeRefreshLayout swipeContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_list, container, false);

        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setColorSchemeResources(Utils.getColorScheme());

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        List<Notification> notificationList = new ArrayList<>();
        try {
            notificationList = Notification.all();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        adapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(adapter);

        if (Utils.IsInternetAvailable(getContext())) {
            swipeContainer.setRefreshing(true);
            new ProcessRequest<>(Process.MEMBER_NOTIFICATION_LIST, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void onSuccess(String process, List<Notification> notificationList) {
        if (notificationList != null) {
            adapter.setFeeds(notificationList);
        }
        swipeContainer.setRefreshing(false);
    }

    @Override
    public void onFailure(String process) {
        swipeContainer.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        if (Utils.IsInternetAvailable(getContext())) {
            new ProcessRequest<>(Process.MEMBER_NOTIFICATION_LIST, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }
}
