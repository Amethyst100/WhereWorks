package com.phloxinc.whereworks.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.phloxinc.whereworks.R;
import com.phloxinc.whereworks.process.Process;
import com.phloxinc.whereworks.process.ProcessRequest;
import com.phloxinc.whereworks.utils.Utils;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener, ProcessRequest.RequestListener<Object> {

    private AppCompatButton submitButton;
    private EditText fieldEmail;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        submitButton = (AppCompatButton) findViewById(R.id.submit);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        fieldEmail = (EditText) findViewById(R.id.email);
        submitButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit:
                String email = fieldEmail.getText().toString();

                if (email.isEmpty()) {
                    fieldEmail.setError("Required");
                }

                if (Utils.IsInternetAvailable(this)) {
                    if (!email.isEmpty()) {
                        new ProcessRequest<>(Process.MEMBER_FORGOT_PASS, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, email);
                        submitButton.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                } else {
                    Utils.showDialog(this, "No internet");
                }
                break;
        }
    }

    @Override
    public void onSuccess(String process, Object result) {
        submitButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        finish();
    }

    @Override
    public void onFailure(String process) {
        submitButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        Utils.showDialog(this, "Failed");
        finish();
    }
}
