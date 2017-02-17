package com.phloxinc.whereworks.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.phloxinc.whereworks.ActivityController;
import com.phloxinc.whereworks.CircleTransform;
import com.phloxinc.whereworks.R;
import com.phloxinc.whereworks.bo.Chat;
import com.phloxinc.whereworks.bo.Member;
import com.phloxinc.whereworks.bo.MemberTeamMap;
import com.phloxinc.whereworks.constant.Constants;
import com.phloxinc.whereworks.process.Process;
import com.phloxinc.whereworks.prefs.Prefs;
import com.phloxinc.whereworks.process.ProcessRequest;
import com.phloxinc.whereworks.utils.Utils;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MemberInfoActivity extends AppCompatActivity implements View.OnClickListener, ProcessRequest.RequestListener<Object> {

    private Member member;
    private ProgressDialog dialog;
    private ProgressDialog rDialog;
    private int memberId;
    private ImageView memberImage;
    private TextView memberName;
    private TextView memberEmail;
    private AppCompatButton trackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        memberImage = (ImageView) findViewById(R.id.profile_image);
        memberName = (TextView) findViewById(R.id.name);
        memberEmail = (TextView) findViewById(R.id.email);

        trackButton = (AppCompatButton) findViewById(R.id.track);
        AppCompatButton deleteButton = (AppCompatButton) findViewById(R.id.delete);
        AppCompatButton chatButton = (AppCompatButton) findViewById(R.id.chat);

        trackButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        chatButton.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            memberId = extras.getInt("memberid");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityController.getInstance().setCurrentActivity(this);

        if (memberId != 0) {
            member = Member.load(memberId);

            if (member != null) {
                memberName.setText(member.getFullName());
                memberEmail.setText(member.getEmail());
                if (!member.getFullName().isEmpty()) {
                    memberImage.setImageDrawable(Utils.getTextDrawable(String.valueOf(member.getFullName().charAt(0)), String.valueOf(member.getMemberId())));
                } else {
                    memberImage.setImageDrawable(Utils.getTextDrawable("A"));
                }

                Picasso.with(this).load(Constants.MEMBER_IMAGE_URL + member.getMemberPhoto())
                        .transform(new CircleTransform())
                        .placeholder(Utils.getTextDrawable(String.valueOf(member.getFullName().charAt(0)), String.valueOf(member.getMemberId())))
                        .into(memberImage);

                if (member.getStatus() != null) {
                    if (member.getStatus().contains("2")) {
                        trackButton.setVisibility(View.GONE);
                    } else {
                        trackButton.setVisibility(View.VISIBLE);
                    }
                } else {
                    trackButton.setVisibility(View.GONE);
                }
            }
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
        switch (view.getId()) {
            case R.id.chat:
                startActivity(new Intent(this, ChatActivity.class).putExtra("memberid", member.getMemberId()));
                break;

            case R.id.track:
                Calendar calendar = Calendar.getInstance();
                String isoDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault()).format(calendar.getTime());
                new ProcessRequest<>(Process.MEMBER_OLD_LOCATION_LIST, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "0", String.valueOf(member.getMemberId()), isoDate, "01:00:00", "23:59:00");
                rDialog = new ProgressDialog(this);
                rDialog.setMessage("Retrieving location data");
                rDialog.show();
                break;

            case R.id.delete:
                if (Utils.IsInternetAvailable(this)) {
                    new ProcessRequest<>(Process.MEMBER_REMOVE_CONTACT, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(0), String.valueOf(member.getMemberId()));
                    dialog = new ProgressDialog(this);
                    dialog.setMessage("Removing Contact");
                    dialog.show();
                } else {
                    Utils.showDialog(this, "No internet");
                }
                break;
        }
    }

    @Override
    public void onSuccess(String process, Object result) {
        switch (process) {
            case Process.MEMBER_REMOVE_CONTACT:
                dialog.dismiss();
                Chat chat = Chat.loadByMemberId(member.getMemberId());
                List<MemberTeamMap> memberTeamMaps = MemberTeamMap.getMapsById(member.getMemberId());
                for (MemberTeamMap memberTeamMap : memberTeamMaps) {
                    memberTeamMap.delete();
                }
                if (chat != null) {
                    chat.delete();
                }
                member.delete();
                finish();
                break;

            case Process.MEMBER_OLD_LOCATION_LIST:
                rDialog.dismiss();
                Prefs.putString("Nav", "Maps");
                Prefs.putString("LiveTrack", String.valueOf(member.getMemberId()));
                Prefs.putString("TrackType", "Member");
                finish();
                break;
        }
    }

    @Override
    public void onFailure(String process) {
        switch (process) {
            case Process.MEMBER_REMOVE_CONTACT:
                dialog.dismiss();
                Utils.showDialog(this, "Failed to delete member");
                break;

            case Process.MEMBER_OLD_LOCATION_LIST:
                rDialog.dismiss();
                Prefs.putString("Nav", "Maps");
                Prefs.putString("LiveTrack", String.valueOf(member.getMemberId()));
                Prefs.putString("TrackType", "Member");
                finish();
                break;
        }
    }
}
