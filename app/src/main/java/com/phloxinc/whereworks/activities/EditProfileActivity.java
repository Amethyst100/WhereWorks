package com.phloxinc.whereworks.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.phloxinc.whereworks.ActivityController;
import com.phloxinc.whereworks.CircleTransform;
import com.phloxinc.whereworks.R;
import com.phloxinc.whereworks.constant.Constants;
import com.phloxinc.whereworks.database.DatabaseUtils;
import com.phloxinc.whereworks.prefs.Prefs;
import com.phloxinc.whereworks.process.Process;
import com.phloxinc.whereworks.process.ProcessRequest;
import com.phloxinc.whereworks.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, ProcessRequest.RequestListener<Object> {

    private static final int SELECT_PICTURE = 100;

    private ImageView image;
    private EditText profileName;
    private EditText profileNumber;
    private ProgressDialog dialog;
    private String timezone;
    private TextView profileEmail;
    private TextDrawable drawable;
    private String selectedImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        image = (ImageView) findViewById(R.id.profile_image);
        profileName = (EditText) findViewById(R.id.name);
        profileEmail = (TextView) findViewById(R.id.email);
        profileNumber = (EditText) findViewById(R.id.number);
        AppCompatSpinner timezoneSpinner = (AppCompatSpinner) findViewById(R.id.timezone);

        String[] timezones = getResources().getStringArray(R.array.timezones);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, timezones);
        timezoneSpinner.setAdapter(adapter);
        timezoneSpinner.setOnItemSelectedListener(this);
        timezoneSpinner.setVisibility(View.GONE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityController.getInstance().setCurrentActivity(this);

        DatabaseUtils.initDB(this);
        String email = Prefs.getString("userEmail", "");
        String name = Prefs.getString("userName", "");
        String number = Prefs.getString("userNumber", "");
        String photo = Prefs.getString("userPhoto", "");
        timezone = "Islamabad";

        profileName.setText(name);
        profileNumber.setText(number);
        profileEmail.setText(email);


        if (!name.isEmpty()) {
            drawable = Utils.getTextDrawable(String.valueOf(name.charAt(0)), name);
        } else {
            drawable = Utils.getTextDrawable("A");
        }
        if (selectedImagePath == null) {
            image.setImageDrawable(drawable);
            Picasso.with(this).load(Constants.MEMBER_IMAGE_URL + photo)
                    .transform(new CircleTransform())
                    .placeholder(drawable)
                    .into(image);
        }
        image.setOnClickListener(this);
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
            case R.id.fab:
                if (Utils.IsInternetAvailable(this)) {
                    String name = profileName.getText().toString();
                    String number = profileNumber.getText().toString();
                    new ProcessRequest<>(Process.MEMBER_UPDATE, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, name, number, timezone);
                    dialog = new ProgressDialog(this);
                    dialog.setMessage("Updating profile");
                    dialog.show();
                } else {
                    Utils.showDialog(this, "No internet");
                }
                break;

            case R.id.profile_image:
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                startActivityForResult(intent, SELECT_PICTURE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = Utils.getRealPathFromURI(this, selectedImageUri);//Utils.getPathFromUri(this, selectedImageUri);
                if (selectedImagePath != null) {
                    Picasso.with(this).load(new File(selectedImagePath))
                            .transform(new CircleTransform())
                            .placeholder(drawable)
                            .into(image);
                }

//                Utils.jsPost(this, Constants.SERVER_URL, Process.PHOTO_CHANGE, Prefs.getString("token", ""), Prefs.getString("userId", ""), "0", "1", new File(selectedImagePath));
                new ProcessRequest<>(Process.PHOTO_CHANGE, new ProcessRequest.RequestListener<Object>() {
                    @Override
                    public void onSuccess(String process, Object result) {

                    }

                    @Override
                    public void onFailure(String process) {

                    }
                }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, selectedImagePath);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onSuccess(String process, Object result) {
        dialog.dismiss();
        finish();
    }

    @Override
    public void onFailure(String process) {
        dialog.dismiss();
        Utils.showDialog(this, "Failed to update profile");
    }


//    public int uploadFile(String sourceFileUri) {
//
////        String fileName = sourceFileUri.replace(sourceFileUri, tag);
//        String fileName = sourceFileUri;
//        HttpURLConnection conn;
//        DataOutputStream dos;
//        String lineEnd = "\r\n";
//        String twoHyphens = "--";
//        String boundary = "*****";
//        int bytesRead, bytesAvailable, bufferSize;
//        byte[] buffer;
//        int serverResponseCode = 0;
//        int maxBufferSize = 1024 * 1024;
//        File sourceFile = new File(sourceFileUri);
//
//        if (!sourceFile.isFile()) {
//            Log.e("uploadFile", "Source File not exist");
//            return 0;
//        } else {
//            try {
//                FileInputStream fileInputStream = new FileInputStream(sourceFile);
//                URL url = new URL(Constants.SERVER_URL);
//
//                conn = (HttpURLConnection) url.openConnection();
//                conn.setDoInput(true); // Allow Inputs
//                conn.setDoOutput(true); // Allow Outputs
//                conn.setUseCaches(false); // Don't use a Cached Copy
//                conn.setRequestMethod("POST");
//                conn.setRequestProperty("Connection", "Keep-Alive");
//                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
//                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
//                conn.setRequestProperty("uploaded_file", fileName);
//                dos = new DataOutputStream(conn.getOutputStream());
//                dos.writeBytes(twoHyphens + boundary + lineEnd);
//                dos.writeBytes("Content-Disposition: form-data; name=\"process\"" + lineEnd);
//                dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + fileName + "\"" + lineEnd);
//                dos.writeBytes(lineEnd);
//
//                // create a buffer of  maximum size
//                bytesAvailable = fileInputStream.available();
//                bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                buffer = new byte[bufferSize];
//                // read file and write it into form...
//                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//
//                while (bytesRead > 0) {
//                    dos.write(buffer, 0, bufferSize);
//                    bytesAvailable = fileInputStream.available();
//                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//                }
//
//                // send multipart form data necesssary after file data...
//                dos.writeBytes(lineEnd);
//                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//
//                // Responses from the server (code and message)
//                serverResponseCode = conn.getResponseCode();
//                String serverResponseMessage = conn.getResponseMessage();
//
//                Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
//                //close the streams //
//                fileInputStream.close();
//                dos.flush();
//                dos.close();
//
//            } catch (MalformedURLException ex) {
//                ex.printStackTrace();
//                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return serverResponseCode;
//
//        }
//    }
}
