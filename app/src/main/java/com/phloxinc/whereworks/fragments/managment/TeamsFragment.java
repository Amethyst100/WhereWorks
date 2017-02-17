package com.phloxinc.whereworks.fragments.managment;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.phloxinc.whereworks.R;
import com.phloxinc.whereworks.activities.CreateTeamActivity;
import com.phloxinc.whereworks.adapters.TeamAdapter;
import com.phloxinc.whereworks.bo.Team;
import com.phloxinc.whereworks.process.Process;
import com.phloxinc.whereworks.process.ProcessRequest;
import com.phloxinc.whereworks.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class TeamsFragment extends Fragment implements View.OnClickListener, ProcessRequest.RequestListener<List<Team>>, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
//    private ExpandableRecyclerAdapter adapter;
    private TeamAdapter adapter;
    private SwipeRefreshLayout swipeContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_list, container, false);

        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setColorSchemeResources(Utils.getColorScheme());

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_group_add_black_24dp);
        fab.setOnClickListener(this);
        fab.setVisibility(View.VISIBLE);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        List<Team> teamList = new ArrayList<>();
        try {
            teamList = Team.allParentTeams();
            if (!teamList.isEmpty()) {
                Team team = new Team();
                team.setTeamId(-1);
                teamList.add(team);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

//        adapter = new ExpandableTeamAdapter(teamList);
        adapter = new TeamAdapter(teamList, 0);
        recyclerView.setAdapter(adapter);

        if (Utils.IsInternetAvailable(getContext())) {
            swipeContainer.setRefreshing(true);
            new ProcessRequest<>(Process.TEAM_LIST, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab) {
            startActivity(new Intent(view.getContext(), CreateTeamActivity.class));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onSuccess(String process, List<Team> teamList) {
        if (teamList != null) {
//            adapter.getParentItemList().clear();
            if (!teamList.isEmpty()) {
                Team team = new Team();
                team.setTeamId(-1);
                teamList.add(team);
            }
//            adapter.getParentItemList().addAll(teamList);
//            adapter.notifyDataSetChanged();
            adapter.setTeamList(teamList);
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
            new ProcessRequest<>(Process.TEAM_LIST, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }
}
