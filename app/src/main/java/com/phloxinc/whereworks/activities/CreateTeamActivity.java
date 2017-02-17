package com.phloxinc.whereworks.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.phloxinc.whereworks.ActivityController;
import com.phloxinc.whereworks.R;
import com.phloxinc.whereworks.bo.Team;
import com.phloxinc.whereworks.process.Process;
import com.phloxinc.whereworks.process.ProcessRequest;
import com.phloxinc.whereworks.utils.Utils;

public class CreateTeamActivity extends AppCompatActivity implements View.OnClickListener, ProcessRequest.RequestListener<Object> {

    private EditText fieldName;
    private EditText fieldDesc;
    private ProgressDialog dialog;
    private Team team;
    private int parentId = 0;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);

        initToolbar();

        fieldName = (EditText) findViewById(R.id.name_field);
        fieldDesc = (EditText) findViewById(R.id.desc_field);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_done_white_24dp);
        fab.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("teamid")) {
                int teamId = extras.getInt("teamid");
                team = Team.load(teamId);

                if (team != null) {
                    fieldName.setText(team.getName());
                    fieldDesc.setText(team.getDescription());
                }
            } else if (extras.containsKey("parentid")) {
                parentId = extras.getInt("parentid");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityController.getInstance().setCurrentActivity(this);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        if (team != null) {
            collapsingToolbar.setTitle("Edit Team");
        } else {
            collapsingToolbar.setTitle("Create Team");
        }
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab) {
            String name = fieldName.getText().toString();
            String desc = fieldDesc.getText().toString();

            if (!name.isEmpty()) {
                if (Utils.IsInternetAvailable(view.getContext())) {
                    dialog = new ProgressDialog(CreateTeamActivity.this);
                    if (team != null) {
                        new ProcessRequest<>(Process.TEAM_UPDATE, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(team.getTeamId()), name, desc, String.valueOf(team.getParentId()));
                        dialog.setMessage("Updating Team");
                    } else {
                        new ProcessRequest<>(Process.TEAM_ADD, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, name, desc, String.valueOf(parentId));
                        dialog.setMessage("Creating Team");
                    }
                    fab.setOnClickListener(null);
                    fab.animate().scaleX(0).scaleY(0).setDuration(250).start();
                    dialog.show();
                } else {
                    Utils.showDialog(this, "No internet");
                }
            } else {
                Utils.showDialog(this, "Team Name Required");
            }
        }
    }

    @Override
    public void onSuccess(String process, Object result) {
        switch (process) {
            case Process.TEAM_ADD:
            case Process.TEAM_UPDATE:
                dialog.dismiss();
                new ProcessRequest<>(Process.TEAM_LIST, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                dialog.setMessage("Updating Teams");
                dialog.show();
                break;

            case Process.TEAM_LIST:
                fab.animate().scaleX(1).scaleY(1).setDuration(500).start();
                dialog.dismiss();
                finish();
                break;
        }
    }

    @Override
    public void onFailure(String process) {
        dialog.dismiss();
        switch (process) {
            case Process.TEAM_ADD:
                fab.setOnClickListener(this);
                fab.animate().scaleX(1).scaleY(1).setDuration(500).start();
                Utils.showDialog(this, "Failed to create team");
                break;

            case Process.TEAM_UPDATE:
                fab.setOnClickListener(this);
                fab.animate().scaleX(1).scaleY(1).setDuration(500).start();
                Utils.showDialog(this, "Failed to update team");
                break;
        }
    }
}
