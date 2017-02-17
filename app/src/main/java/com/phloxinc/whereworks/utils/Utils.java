package com.phloxinc.whereworks.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.phloxinc.whereworks.receivers.AlarmReceiver;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Utils {

    public static boolean IsInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
            boolean isWiMax = activeNetwork.getType() == ConnectivityManager.TYPE_WIMAX;
            boolean isMobile = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
            WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = null;
            if (wifiMgr != null) {
                wifiInfo = wifiMgr.getConnectionInfo();
            }
            String name = "";
            if (wifiInfo != null) {
                name = wifiInfo.getSSID();
            }

            return true;
        } else {
            return false;
        }
    }

    public static void startAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (5 * 60 * 1000), pendingIntent);
    }

    @SuppressWarnings("MissingPermission")
    public static Location getLocation(Context context) {
        if (context != null) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            Location location = null;
            if (locationManager != null) {
                boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                LocationListener listener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                };
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, listener);
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                if (location == null)
                    if (wifi.isWifiEnabled()) {
                        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1, 1, listener);
                        location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    }
                if (location == null)
                    if (isNetworkEnabled) {
                        Criteria criteria = new Criteria();
                        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                        String provider = locationManager.getBestProvider(criteria, true);
                        locationManager.requestLocationUpdates(provider, 1000, 5, listener);
                        location = locationManager.getLastKnownLocation(provider);
                    }
            }
            return location;
        } else {
            return null;
        }
    }

    public static TextDrawable getTextDrawable(String letter) {
        ColorGenerator generator = ColorGenerator.MATERIAL;
        return TextDrawable.builder().buildRound(letter, generator.getRandomColor());
    }

    public static TextDrawable getTextDrawable(String letter, String key) {
        ColorGenerator generator = ColorGenerator.MATERIAL;
        return TextDrawable.builder().buildRound(letter, generator.getColor(key));
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static Address getAddressFromLocation(@NonNull Context context, double lat, double lng) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null) {
                if (!addresses.isEmpty()) {
                    return addresses.get(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public static String getPathFromUri(Activity context, Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.managedQuery(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }

    @SuppressLint("NewApi")
    public static String getRealPathFromURI(Context context, Uri contentURI) {
        String result = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, contentURI)) {
            if (isExternalStorageDocument(contentURI)) {
                final String docId = DocumentsContract.getDocumentId(contentURI);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(contentURI)) {

                final String id = DocumentsContract.getDocumentId(contentURI);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(contentURI)) {
                final String docId = DocumentsContract.getDocumentId(contentURI);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if (contentURI.getScheme().equals("content")) {
            result = getDataColumn(context, contentURI, null, null);
        } else if (contentURI.getScheme().equals("file")) {
            result = contentURI.getPath();
        }
        return result;
    }

    private static String getDataColumn(Context context, Uri contentUri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = MediaStore.Images.ImageColumns.DATA;
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(contentUri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static void showDialog(@NonNull Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.show();
    }

    public static int[] getColorScheme() {
        return new int[]{android.R.color.holo_orange_light,
                android.R.color.holo_green_light,
                android.R.color.holo_red_dark,
                android.R.color.holo_blue_dark};
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    public static void jsPost(Context context, final String link, final String process, final String token, final String userId, final String teamId, final String type, final File file) {
        final WebView webView = new WebView(context);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        JavaScriptInterface myJSInterface = new JavaScriptInterface();
        webView.addJavascriptInterface(myJSInterface, "JSInterface");
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:(function () { " +
                        "var fd = new FormData();" +
                        "fd.append(\"process\", " + process + ");" +
                        "fd.append(\"token\", " + token + ");" +
                        "fd.append(\"userid\", " + userId + ");" +
                        "fd.append(\"teamid\", " + teamId + ");" +
                        "fd.append(\"type\", " + type + ");" +
                        "fd.set(\"file\", " + file + ");" +
                        "$http.post(" + link + ", fd, {withCredentials : false, headers : {'Content-Type' : undefined},transformRequest : angular.identity})" +
                        ".success(function(res){" +
                        "JSInterface.showLog(\"ok\")" +
                        "})" +
                        ".error(function(e){" +
                        "JSInterface.showLog(\"error\")" +
                        "});" +
                        "})()");
            }
        });
    }

    public static class JavaScriptInterface {
        public void showLog(String message) {
            Log.e("JSInterface", message);
        }
    }
}
