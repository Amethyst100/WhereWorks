package com.phloxinc.whereworks.activities;

import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.phloxinc.whereworks.ActivityController;
import com.phloxinc.whereworks.R;
import com.phloxinc.whereworks.adapters.TeamMembersAdapter;
import com.phloxinc.whereworks.bo.Member;
import com.phloxinc.whereworks.bo.Team;

import java.util.ArrayList;
import java.util.List;

public class TeamMembersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int teamId = extras.getInt("teamid");
            Team team = Team.load(teamId);

            List<Member> memberList = new ArrayList<>();
            try {
                memberList = Member.allNonPending();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            TeamMembersAdapter adapter = new TeamMembersAdapter(this, team, memberList);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityController.getInstance().setCurrentActivity(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
