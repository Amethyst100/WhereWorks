package com.phloxinc.whereworks.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.phloxinc.whereworks.ActivityController;
import com.phloxinc.whereworks.CircleTransform;
import com.phloxinc.whereworks.R;
import com.phloxinc.whereworks.adapters.ContactAdapter;
import com.phloxinc.whereworks.bo.Member;
import com.phloxinc.whereworks.bo.Team;
import com.phloxinc.whereworks.constant.Constants;
import com.phloxinc.whereworks.process.Process;
import com.phloxinc.whereworks.prefs.Prefs;
import com.phloxinc.whereworks.process.ProcessRequest;
import com.phloxinc.whereworks.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TeamActivity extends AppCompatActivity implements View.OnClickListener, ProcessRequest.RequestListener<Object> {

    private Team team;
    private ProgressDialog dialog;
    private ContactAdapter<Member> adapter;
    private int teamId = 0;
    private ImageView teamImage;
    private TextView teamName;
    private TextView teamDesc;
    private RecyclerView recyclerView;
    private AppCompatButton deleteButton;
    private AppCompatButton liveTrackButton;
    private AppCompatButton leaveButton;
    private ImageView addMembersIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        teamImage = (ImageView) findViewById(R.id.profile_image);
        teamName = (TextView) findViewById(R.id.name);
        teamDesc = (TextView) findViewById(R.id.desc);
        liveTrackButton = (AppCompatButton) findViewById(R.id.live_track);
        deleteButton = (AppCompatButton) findViewById(R.id.delete);
        leaveButton = (AppCompatButton) findViewById(R.id.leave);
        AppCompatButton chatButton = (AppCompatButton) findViewById(R.id.chat);
        addMembersIcon = (ImageView) findViewById(R.id.add_members_icon);
        liveTrackButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        leaveButton.setOnClickListener(this);
        chatButton.setOnClickListener(this);
        addMembersIcon.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            teamId = extras.getInt("teamid");
            team = Team.load(teamId);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityController.getInstance().setCurrentActivity(this);

        if (teamId != 0) {
            team = Team.load(teamId);

            if (team != null) {
                teamName.setText(team.getName());
                teamDesc.setText(team.getDescription());

                Picasso.with(this).load(Constants.TEAM_IMAGE_URL + team.getTeamPhoto())
                        .transform(new CircleTransform())
                        .placeholder(Utils.getTextDrawable(String.valueOf(team.getName().charAt(0)), String.valueOf(team.getTeamId())))
                        .into(teamImage);

                if (team.getHeadId() != Integer.parseInt(Prefs.getString("userId", "0"))) {
                    deleteButton.setVisibility(View.GONE);
                    liveTrackButton.setVisibility(View.GONE);
                    addMembersIcon.setVisibility(View.GONE);
                    leaveButton.setVisibility(View.VISIBLE);
                } else {
                    deleteButton.setVisibility(View.VISIBLE);
                    liveTrackButton.setVisibility(View.VISIBLE);
                    addMembersIcon.setVisibility(View.VISIBLE);
                    leaveButton.setVisibility(View.GONE);
                }

                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(layoutManager);
                List<Member> memberList = team.getMembers();
                if (team.getHeadId() != Integer.parseInt(Prefs.getString("userId", "0"))) {
                    Member head = Member.load(team.getHeadId());
                    if (head != null) {
                        memberList.add(0, head);
                    }
                }
                adapter = new ContactAdapter<>(memberList, team, false);
                recyclerView.setAdapter(adapter);

                if (Utils.IsInternetAvailable(this)) {
                    new ProcessRequest<>(Process.TEAM_MEMBER_LIST, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(team.getTeamId()));
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_team, menu);
        MenuItem editItem = menu.findItem(R.id.action_edit);
        MenuItem addSubTeamItem = menu.findItem(R.id.action_add_sub_team);

        if (team.getHeadId() != Integer.parseInt(Prefs.getString("userId", "0"))) {
            editItem.setVisible(false);
            addSubTeamItem.setVisible(false);
        } else {
            editItem.setVisible(true);
            addSubTeamItem.setVisible(true);

//            if (team.getParentId() != 0) {
//                addSubTeamItem.setVisible(false);
//            } else {
//                addSubTeamItem.setVisible(true);
//            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_edit:
                startActivity(new Intent(this, CreateTeamActivity.class).putExtra("teamid", team.getTeamId()));
                return true;

            case R.id.action_add_sub_team:
                startActivity(new Intent(this, CreateTeamActivity.class).putExtra("parentid", team.getTeamId()));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.live_track:
                if (Utils.IsInternetAvailable(this)) {
                    new ProcessRequest<>(Process.MEMBER_LIVE_LOCATION_LIST, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(team.getTeamId()));
                    dialog = new ProgressDialog(this);
                    dialog.setMessage("Live Tracking");
                    dialog.show();
                } else {
                    Utils.showDialog(this, "No internet");
                }
                break;

            case R.id.delete:
                if (Utils.IsInternetAvailable(this)) {
                    new ProcessRequest<>(Process.TEAM_DELETE, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(team.getTeamId()));
                    dialog = new ProgressDialog(this);
                    dialog.setMessage("Deleting Team");
                    dialog.show();
                } else {
                    Utils.showDialog(this, "No internet");
                }
                break;

            case R.id.leave:
                if (Utils.IsInternetAvailable(this)) {
                    dialog = new ProgressDialog(this);
                    dialog.setMessage("Leaving team");
                    dialog.show();
                    new ProcessRequest<>(Process.MEMBER_TEAM_LEAVE, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(team.getTeamId()), String.valueOf(team.getHeadId()));
                } else {
                    Utils.showDialog(this, "No internet");
                }
                break;

            case R.id.chat:
                view.getContext().startActivity(new Intent(view.getContext(), ChatActivity.class).putExtra("teamid", team.getTeamId()));
                break;

            case R.id.add_members_icon:
                view.getContext().startActivity(new Intent(view.getContext(), TeamMembersActivity.class).putExtra("teamid", team.getTeamId()));
                break;
        }
    }

    @Override
    public void onSuccess(String process, Object result) {
        switch (process) {
            case Process.MEMBER_LIVE_LOCATION_LIST:
                dialog.dismiss();
                Prefs.putString("Nav", "Maps");
                Prefs.putString("LiveTrack", String.valueOf(team.getTeamId()));
                Prefs.putString("TrackType", "Team");
                finish();
                break;

            case Process.MEMBER_TEAM_LEAVE: // leave team
                dialog.dismiss();
                FirebaseMessaging.getInstance().unsubscribeFromTopic("T" + team.getTeamId());
                team.delete();
                finish();
                break;

            case Process.TEAM_DELETE:
                dialog.dismiss();
                finish();
                break;

            case Process.TEAM_MEMBER_LIST:
                List<Member> memberList = team.getMembers();
                if (team.getHeadId() != Integer.parseInt(Prefs.getString("userId", "0"))) {
                    Member head = Member.load(team.getHeadId());
                    if (head != null) {
                        memberList.add(0, head);
                    }
                }
                adapter.setFeeds(memberList, team);
                break;
        }
    }

    @Override
    public void onFailure(String process) {
        switch (process) {
            case Process.MEMBER_LIVE_LOCATION_LIST:
                if (dialog != null) {
                    dialog.dismiss();
                }
                Prefs.putString("LiveTrack", "");
                break;

            case Process.MEMBER_TEAM_LEAVE: // leave team
                if (dialog != null) {
                    dialog.dismiss();
                }
                Utils.showDialog(this, "Failed to leave team");
                break;

            case Process.TEAM_DELETE:
                if (dialog != null) {
                    dialog.dismiss();
                }
                Utils.showDialog(this, "Failed to delete team");
                break;

            case Process.TEAM_MEMBER_LIST:
                break;
        }

    }
}
