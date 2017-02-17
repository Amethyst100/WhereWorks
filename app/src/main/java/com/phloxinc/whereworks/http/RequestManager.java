package com.phloxinc.whereworks.http;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import com.android.internal.http.multipart.FilePart;
import com.android.internal.http.multipart.MultipartEntity;
import com.android.internal.http.multipart.Part;
import com.android.internal.http.multipart.StringPart;
import com.google.gson.Gson;
import com.phloxinc.whereworks.constant.Constants;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestManager {

    private static RequestManager instance;

    public static RequestManager getInstance() {
        if (instance == null) {
            instance = new RequestManager();
        }
        return instance;
    }

    public Map<String, Object> post(String link, Map<String, String> params) throws IOException {

        String urlParameters = "?";
        Iterator iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry param = (Map.Entry) iterator.next();
            String key = (String) param.getKey();
            String value = (String) param.getValue();
            value = value.replace(" ", "+");
            urlParameters = urlParameters.concat(key + "=" + value);
            if (iterator.hasNext()) {
                urlParameters = urlParameters.concat("&");
            }
        }
        URL url = new URL(link + urlParameters);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");

        int code = conn.getResponseCode();
        if (code == HttpURLConnection.HTTP_OK) {
            String line;
            InputStream inputSteam = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputSteam));

            StringBuilder buffer = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            reader.close();

            Gson gson = new Gson();
            Map<String, Object> map = new HashMap<>();
            map = gson.fromJson(buffer.toString(), map.getClass());

            return map;
        } else {
            return null;
        }
    }

    public Map<String, Object> postPhoto(String link, Map<String, String> params) throws IOException {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(Constants.SERVER_URL);

        try {

//            List < NameValuePair > nameValuePairs = new ArrayList < NameValuePair > ();
//            nameValuePairs.add(new BasicNameValuePair("name", params.get("teamid")));
//            nameValuePairs.add(new BasicNameValuePair("type", params.get("type")));
//            nameValuePairs.add(new BasicNameValuePair("userid", params.get("userid")));
//            nameValuePairs.add(new BasicNameValuePair("token", params.get("token")));
//            nameValuePairs.add(new BasicNameValuePair("process", params.get("process")));
//            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            File file = new File(params.get("file"));
            String filePath = params.get("file");
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            String fileType = "image/jpg";
            if (file.getName().contains("png")) {
                fileType = "image/png";
            } else if (file.getName().contains("jpeg")) {
                fileType = "image/jpeg";
            }
            Part[] parts = new Part[]{
                    new StringPart("name", params.get("teamid")),
                    new StringPart("type", params.get("type")),
                    new StringPart("userid", params.get("userid")),
                    new StringPart("token", params.get("token")),
                    new StringPart("process", params.get("process")),
//                    new StringPart("file[name]", fileName),
//                    new StringPart("file[type]", fileType),
//                    new StringPart("file[tmp_name]", filePath),
//                    new StringPart("file[size]", String.valueOf(file.length()))
//                    new StringPart("file[error]", "0")
                    new FilePart("file", fileName, file, fileType, "UTF-8")
            };
            MultipartEntity entity = new MultipartEntity(parts);
            httppost.setEntity(entity);

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            String sResponse;
            StringBuilder s = new StringBuilder();

            while ((sResponse = reader.readLine()) != null) {
                s = s.append(sResponse);
            }
            Log.e("response", "res");

        } catch (IOException e) {
            e.printStackTrace();
        }


//        String urlParameters = "?";
//        Iterator iterator = params.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry param = (Map.Entry) iterator.next();
//            String key = (String) param.getKey();
//            String value = (String) param.getValue();
//            value = value.replace(" ", "+");
//            urlParameters = urlParameters.concat(key + "=" + value);
//            if (iterator.hasNext()) {
//                urlParameters = urlParameters.concat("&");
//            }
//        }
//        URL url = new URL(link + urlParameters);
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setRequestMethod("POST");
//
//        int code = conn.getResponseCode();
//        if (code == HttpURLConnection.HTTP_OK) {
//            String line;
//            InputStream inputSteam = new BufferedInputStream(conn.getInputStream());
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputSteam));
//
//            StringBuilder buffer = new StringBuilder();
//            while ((line = reader.readLine()) != null) {
//                buffer.append(line);
//            }
//            reader.close();
//
//            Gson gson = new Gson();
//            Map<String, Object> map = new HashMap<>();
//            map = gson.fromJson(buffer.toString(), map.getClass());
//
//            return map;
//        } else {
            return null;
//        }
    }

    public Map<String, Object> postUpload(String link, Map<String, String> params) throws IOException {

//        String urlParameters = "?";
//        Iterator iterator = params.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry param = (Map.Entry) iterator.next();
//            String key = (String) param.getKey();
//            String value = (String) param.getValue();
//            value = value.replace(" ", "+");
//            urlParameters = urlParameters.concat(key + "=" + value);
//            if (iterator.hasNext()) {
//                urlParameters = urlParameters.concat("&");
//            }
//        }
//        URL url = new URL(link + urlParameters);
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setRequestMethod("POST");
//        conn.setDoInput(true);
//        conn.setDoOutput(true);
//        conn.setRequestProperty("Connection", "Keep-Alive");
////        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
//
//        int code = conn.getResponseCode();
//        if (code == HttpURLConnection.HTTP_OK) {
//            String line;
//            InputStream inputSteam = new BufferedInputStream(conn.getInputStream());
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputSteam));
//
//            StringBuilder buffer = new StringBuilder();
//            while ((line = reader.readLine()) != null) {
//                buffer.append(line);
//            }
//            reader.close();
//
//            Gson gson = new Gson();
//            Map<String, Object> map = new HashMap<>();
//            map = gson.fromJson(buffer.toString(), map.getClass());
//
//            return map;
//        } else {
//            return null;
//        }
        String process = params.get("process");
        String userId = params.get("userid");
        String token = params.get("token");
        String teamId = params.get("teamid");
        String type = params.get("type");
        String fileName = params.get("file");
        HttpURLConnection conn;
        DataOutputStream dos;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int serverResponseCode;
        int maxBufferSize = 1024 * 1024;
        File sourceFile = new File(fileName);

        if (!sourceFile.isFile()) {
            Log.e("uploadFile", "Source File not exist");
            return null;
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(link);

                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
//                conn.setRequestProperty("file", fileName);
                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"process\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.write(process.getBytes());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"token\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(token);

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"userid\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(userId);

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"teamid\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(teamId);

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"type\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(type);

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }
    }

    public Map<String, Object> postMultipart(String link, Map<String, String> params) throws IOException {

        String process = params.get("process");
        String userId = params.get("userid");
        String token = params.get("token");
        String teamId = params.get("teamid");
        String type = params.get("type");
        String filePath = params.get("file");
        File file = new File(filePath);


//        try {
//            MultipartUtility utility = new MultipartUtility(link);
//
//            utility.addHeaderField("Connection", "Keep-Alive");
////            utility.addHeaderField("Content-Type", "multipart/form-data");
//            utility.addFormField("process", process);
//            utility.addFormField("token", token);
//            utility.addFormField("userid", userId);
//            utility.addFormField("teamid", teamId);
//            utility.addFormField("type", type);
//            utility.addFilePart("file", file);
//
//            List<String> response = utility.finish();
//            Log.e("response", response.get(0));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        OkHttpClient client = new OkHttpClient();

//        RequestBody body = new FormBody.Builder()
//                .add("process", process)
//                .add("userid", userId)
//                .add("token", token)
//                .add("teamid", teamId)
//                .add("type", type)
//                .build();

        MediaType MEDIA_TYPE = filePath.endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");

        RequestBody multiPartBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
                .addFormDataPart("process", process)
                .addFormDataPart("token", token)
                .addFormDataPart("userid", userId)
                .addFormDataPart("teamid", teamId)
                .addFormDataPart("type", type)
                .addFormDataPart("file", file.getName(), RequestBody.create(MEDIA_TYPE, file))
                .build();

        Request request = new Request.Builder()
                .url(link)
                .post(multiPartBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            Log.e("Response", response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
