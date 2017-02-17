package com.phloxinc.whereworks.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.phloxinc.whereworks.ActivityController;
import com.phloxinc.whereworks.CircleTransform;
import com.phloxinc.whereworks.R;
import com.phloxinc.whereworks.constant.Constants;
import com.phloxinc.whereworks.database.DatabaseUtils;
import com.phloxinc.whereworks.process.Process;
import com.phloxinc.whereworks.prefs.Prefs;
import com.phloxinc.whereworks.process.ProcessRequest;
import com.phloxinc.whereworks.utils.Utils;
import com.squareup.picasso.Picasso;

public class SelfProfileActivity extends AppCompatActivity implements View.OnClickListener, ProcessRequest.RequestListener<Object> {

    private ImageView profileImage;
    private TextView profileName;
    private TextView profileEmail;
    private TextView profileNumber;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        profileImage = (ImageView) findViewById(R.id.profile_image);
        profileName = (TextView) findViewById(R.id.name);
        profileEmail = (TextView) findViewById(R.id.email);
        profileNumber = (TextView) findViewById(R.id.number);
        AppCompatButton editProfileButton = (AppCompatButton) findViewById(R.id.edit_button);
        LinearLayout changePasswordButton = (LinearLayout) findViewById(R.id.change_password);
        editProfileButton.setOnClickListener(this);
        changePasswordButton.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityController.getInstance().setCurrentActivity(this);

        DatabaseUtils.initDB(this);

        String name = Prefs.getString("userName", "");
        String email = Prefs.getString("userEmail", "");
        String number = Prefs.getString("number", "");
        String photo = Prefs.getString("userPhoto", "");

        profileName.setText(name);
        profileEmail.setText(email);
        if (number.length() > 4) {
            profileNumber.setVisibility(View.VISIBLE);
            profileNumber.setText(number);
        }
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
            case R.id.edit_button:
                startActivity(new Intent(this, EditProfileActivity.class));
                break;

            case R.id.change_password:
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View dialogLayout = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null);
                final EditText oldPasswordField = (EditText) dialogLayout.findViewById(R.id.old_password);
                final EditText newPasswordField = (EditText) dialogLayout.findViewById(R.id.new_password);
                builder.setTitle("Change Password");
                builder.setView(dialogLayout);
                builder.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String oldPassword = oldPasswordField.getText().toString();
                        String newPassword = newPasswordField.getText().toString();

                        if (Utils.IsInternetAvailable(SelfProfileActivity.this)) {
                            new ProcessRequest<>(Process.MEMBER_CHANGE_PASSWORD, SelfProfileActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, oldPassword, newPassword);
                            dialog = new ProgressDialog(SelfProfileActivity.this);
                            dialog.setMessage("Changing password");
                            dialog.show();
                        } else {
                            Utils.showDialog(SelfProfileActivity.this, "No internet");
                        }
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
                break;
        }
    }

    @Override
    public void onSuccess(String process, Object result) {
        switch (process) {
            case Process.MEMBER_CHANGE_PASSWORD:
                dialog.dismiss();
                break;
        }
    }

    @Override
    public void onFailure(String process) {
        dialog.dismiss();
    }
}
