package com.phloxinc.whereworks.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.phloxinc.whereworks.ActivityController;
import com.phloxinc.whereworks.R;
import com.phloxinc.whereworks.constant.Constants;
import com.phloxinc.whereworks.process.Process;
import com.phloxinc.whereworks.controller.MainController;
import com.phloxinc.whereworks.controller.firebase.FirebaseDatabaseController;
import com.phloxinc.whereworks.prefs.Prefs;
import com.phloxinc.whereworks.process.ProcessRequest;
import com.phloxinc.whereworks.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, ProcessRequest.RequestListener<Object> {

    private EditText fieldUserName;
    private EditText fieldNumber;
    private TextInputEditText fieldPassword;
    private AppCompatButton buttonLogin;
    private ProgressBar progressBar;

    private boolean keepSignedUp = false;
    private static final int PERMISSIONS_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermissions();
        }

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        TextView forgotPassword = (TextView) findViewById(R.id.text_forgot_password);
        buttonLogin = (AppCompatButton) findViewById(R.id.button_login);
        forgotPassword.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);

        fieldUserName = (EditText) findViewById(R.id.field_username);
        fieldNumber = (EditText) findViewById(R.id.field_number);
        fieldPassword = (TextInputEditText) findViewById(R.id.field_password);

        CheckBox checkBoxKeepSignedUp = (CheckBox) findViewById(R.id.checkbox_keep_signup_up);
        checkBoxKeepSignedUp.setOnCheckedChangeListener(this);

        if (Prefs.getBoolean("Login", false)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            MainController.getInstance().clearData();
        }

        if (Constants.DEBUG_MODE) {
            fieldUserName.setText(R.string.test_email);
            fieldPassword.setText(R.string.test_password);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityController.getInstance().setCurrentActivity(this);
    }

    private void getPermissions() {
        int hasLocationPermission = checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        int hasLocationPermission1 = checkCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        int hasInternetStatePermission = checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE);
        int hasInternetPermission = checkCallingOrSelfPermission(Manifest.permission.INTERNET);
        int hasWifiStatePermission = checkCallingOrSelfPermission(Manifest.permission.ACCESS_WIFI_STATE);
        int hasWifiChangeStatePermission = checkCallingOrSelfPermission(Manifest.permission.CHANGE_WIFI_STATE);
        int hasWritePermission = checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int hasCameraPermission = checkCallingOrSelfPermission(Manifest.permission.CAMERA);
        int hasCNSPermission = checkCallingOrSelfPermission(Manifest.permission.CHANGE_NETWORK_STATE);
        int hasSendSmsPermission = checkCallingPermission(Manifest.permission.SEND_SMS);
        int hasReadSmsPermission = checkCallingPermission(Manifest.permission.READ_SMS);
        int hasPhoneStatePermission = checkCallingPermission(Manifest.permission.READ_PHONE_STATE);
        int hasBootPermission = checkCallingPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED);

        List<String> permissions = new ArrayList<>();

        if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (hasPhoneStatePermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (hasLocationPermission1 != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (hasInternetStatePermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }
        if (hasInternetPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.INTERNET);
        }
        if (hasWifiStatePermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        }
        if (hasWifiChangeStatePermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CHANGE_WIFI_STATE);
        }
        if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (hasBootPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA);
        }
        if (hasSendSmsPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.SEND_SMS);
        }
        if (hasReadSmsPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_SMS);
        }
        if (hasCNSPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CHANGE_NETWORK_STATE);
        }
        if (!permissions.isEmpty() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions.toArray(new String[permissions.size()]), PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            if (Constants.DEBUG_MODE) {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d("Permissions", "Permission Granted: " + permissions[i]);
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Log.d("Permissions", "Permission Denied: " + permissions[i]);
                    }
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_login:
                Utils.hideKeyboard(this);
                String username = fieldUserName.getText().toString();
                String number = fieldNumber.getText().toString();
                String password = fieldPassword.getText().toString();

                if (username.isEmpty() && number.isEmpty()) {
                    fieldUserName.setError("Required");
                    fieldNumber.setError("Required");
                }
                if (password.isEmpty())
                    fieldPassword.setError("Required");

                if (Utils.IsInternetAvailable(this)) {
                    if ((!username.isEmpty() && !password.isEmpty()) || (!number.isEmpty() && !password.isEmpty())) {
                        new ProcessRequest<>(Process.MEMBER_LOGIN, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, username, number, password);
                        buttonLogin.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                } else {
                    Utils.showDialog(this, "No internet");
                }
                break;

            case R.id.text_forgot_password:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        this.keepSignedUp = isChecked;
    }

    @Override
    public void onSuccess(String process, Object result) {
        switch (process) {
            case Process.MEMBER_LOGIN:
                String[] data = ((String) result).split("\\|");
                String userId = data[0];
                String token = data[1];

                Prefs.putString("userId", userId);
                Prefs.putString("token", token);
                if (keepSignedUp) {
                    Prefs.putBoolean("Login", true);
                }

                String fcmToken = Prefs.getString("fcmToken", "");
                if (fcmToken.isEmpty()) {
                    fcmToken = FirebaseInstanceId.getInstance().getToken();
                    Prefs.putString("fcmToken", fcmToken);
                }

                if (Utils.IsInternetAvailable(this)) {
                    new ProcessRequest<>(Process.MEMBER_DETAIL, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    new ProcessRequest<>(Process.TEAM_LIST, null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    FirebaseDatabaseController.put("M" + userId, fcmToken);
                } else {
                    Prefs.putBoolean("Login", false);
                }
                break;

            case Process.MEMBER_DETAIL:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
    }

    @Override
    public void onFailure(String process) {
        switch (process) {
            case Process.MEMBER_LOGIN:
                Utils.showDialog(this, "Invalid user name or password");
                buttonLogin.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                break;

            case Process.MEMBER_DETAIL:
                break;
        }
    }
}
