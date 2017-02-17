package com.phloxinc.whereworks.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.phloxinc.whereworks.R;
import com.phloxinc.whereworks.bo.LocationLog;
import com.phloxinc.whereworks.bo.Member;
import com.phloxinc.whereworks.bo.Team;
import com.phloxinc.whereworks.constant.Constants;
import com.phloxinc.whereworks.prefs.Prefs;
import com.phloxinc.whereworks.process.Process;
import com.phloxinc.whereworks.process.ProcessRequest;
import com.phloxinc.whereworks.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressLint("UseSparseArrays")
@SuppressWarnings("FieldCanBeLocal")
public class MapsFragment extends Fragment implements LocationListener, CompoundButton.OnCheckedChangeListener, OnMapReadyCallback, View.OnClickListener, DatePickerDialog.OnDateSetListener {

    public Map<Integer, Marker> markerMap = new HashMap<>();
    public Map<Integer, Marker> oldMarkerMap = new HashMap<>();
    public Map<Integer, Bitmap> markerBitmapMap = new HashMap<>();
    private Marker myLocation;

    private SwitchCompat reportingSwitch;

    private RelativeLayout trackingLayout;
    private ImageView trackingImage;
    private TextView trackingName;

    private SupportMapFragment mapFragment;
    public GoogleMap mMap;

    private TimePickerDialog timePickerDialog;
    private LinearLayout timeLayout;
    private TextView fromTimeText;
    private TextView toTimeText;
    private TextView dateText;
    private ProgressDialog progressDialog;

    private Member member;
    private Team team;
    private Location location;
    private String addressText;
    private String city;

    private Calendar selectedTime;
    private String isoDate;
    private String fromTime;
    private String toTime;

    private static final int LOCATION_SETTINGS = 100;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_maps, container, false);

            reportingSwitch = (SwitchCompat) rootView.findViewById(R.id.switch_reporting);
            reportingSwitch.setOnCheckedChangeListener(this);

            trackingLayout = (RelativeLayout) rootView.findViewById(R.id.tracking_layout);
            trackingImage = (ImageView) rootView.findViewById(R.id.image);
            trackingName = (TextView) rootView.findViewById(R.id.tracking_name);
            ImageView closeButton = (ImageView) rootView.findViewById(R.id.button_close);
            closeButton.setOnClickListener(this);

            timeLayout = (LinearLayout) rootView.findViewById(R.id.time_layout);
            fromTimeText = (TextView) rootView.findViewById(R.id.from_time);
            toTimeText = (TextView) rootView.findViewById(R.id.to_time);
            dateText = (TextView) rootView.findViewById(R.id.date);
            dateText.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Calendar.getInstance().getTime()));
            timeLayout.setOnClickListener(this);

            FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
            fab.setOnClickListener(this);

            if (mMap == null) {
                updateMap();
            }

            return rootView;
        } catch (InflateException e) {
            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mMap != null) {
            if (Prefs.isExists("LiveTrack") && Prefs.isExists("TrackType")) {
                if (!Prefs.getString("LiveTrack", "").isEmpty()) {
                    if (Prefs.getString("TrackType", "").contains("Member")) {
                        trackingLayout.setVisibility(View.VISIBLE);
                        timeLayout.setVisibility(View.VISIBLE);
                        String memberId = Prefs.getString("LiveTrack", "");
                        if (!memberId.isEmpty()) {
                            member = Member.load(Integer.parseInt(memberId));
                            trackingName.setText(member.getFullName());
                            if (!member.getFullName().isEmpty()) {
                                trackingImage.setImageDrawable(Utils.getTextDrawable(String.valueOf(member.getFullName().charAt(0)), memberId));
                            } else {
                                trackingImage.setImageDrawable(Utils.getTextDrawable("A", memberId));
                            }
                            List<LocationLog> logs = LocationLog.loadByMemberId(Integer.parseInt(memberId));
                            setMarkers(logs);
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for (Marker marker : oldMarkerMap.values()) {
                                if (marker != null) {
                                    builder.include(marker.getPosition());
                                }
                            }
                            try {
                                LatLngBounds bounds = builder.build();
                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 400);
                                mMap.animateCamera(cameraUpdate);
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (Prefs.getString("TrackType", "").contains("Team")) {
                        trackingLayout.setVisibility(View.VISIBLE);
                        timeLayout.setVisibility(View.GONE);
                        String teamId = Prefs.getString("LiveTrack", "");
                        if (!teamId.isEmpty()) {
                            team = Team.load(Integer.parseInt(teamId));
                            if (team != null) {
                                trackingName.setText(team.getName());
                                if (!team.getName().isEmpty()) {
                                    trackingImage.setImageDrawable(Utils.getTextDrawable(String.valueOf(team.getName().charAt(0)), teamId));
                                } else {
                                    trackingImage.setImageDrawable(Utils.getTextDrawable("A", teamId));
                                }
                                for (Member teamMember : team.getMembers()) {
                                    List<LocationLog> logs = LocationLog.loadByMemberId(teamMember.getMemberId());
                                    setTeamMarkers(logs);
                                }
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (Marker marker : markerMap.values()) {
                                    if (marker != null) {
                                        builder.include(marker.getPosition());
                                    }
                                }
                                try {
                                    LatLngBounds bounds = builder.build();
                                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 400);
                                    mMap.animateCamera(cameraUpdate);
                                } catch (IllegalStateException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        trackingLayout.setVisibility(View.GONE);
                        timeLayout.setVisibility(View.GONE);
                    }
                } else {
                    trackingLayout.setVisibility(View.GONE);
                    timeLayout.setVisibility(View.GONE);
                }
            }
        } else {
            Prefs.putString("LiveTrack", "");
        }

        location = getLocation();
        if (location != null) {
            if (mMap != null) {
                if (myLocation != null) {
                    myLocation.remove();
                }
                myLocation = placeMarker(location.getLatitude(), location.getLongitude(), "Me", (addressText != null && !addressText.isEmpty() ? (addressText + "\n") : "") + (city != null ? city : ""), R.drawable.my_location_marker);
            }
            if (Utils.IsInternetAvailable(getActivity())) {
                new ProcessRequest<>(Process.MEMBER_CHECKLOCSTAT_LIST, new ProcessRequest.RequestListener<String>() {
                    @Override
                    public void onSuccess(String process, String result) {
                        if (result.contains("1")) {
                            reportingSwitch.setChecked(true);
                        } else {
                            reportingSwitch.setChecked(false);
                        }
                    }

                    @Override
                    public void onFailure(String process) {
                    }
                }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    @SuppressWarnings("MissingPermission")
    private Location getLocation() {
        if (getContext() != null) {
            LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            Location location = null;
            if (locationManager != null) {
//                boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//                boolean isWifiEnabled = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                WifiManager wifi = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
//                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, this);
                    Log.d("GPS Enabled", "GPS Enabled");
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        Log.i("GPS_LOCATION", "Location: " + location.toString());
                    }
//                }
                if (location == null)
                    if (wifi.isWifiEnabled()) {
                        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1, 1, this);
                        location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                        if (location != null) {
                            Log.i("WIFI", "Location: " + location.toString());
                        }
                    }
                if (location == null)
                    if (isNetworkEnabled) {
                        Log.d("Network", "Network");
                        Criteria criteria = new Criteria();
                        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                        String provider = locationManager.getBestProvider(criteria, true);
                        locationManager.requestLocationUpdates(provider, 1000, 5, this);
                        location = locationManager.getLastKnownLocation(provider);
                    }
            }
            return location;
        } else {
            return null;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.e("onLocationChanged", "Success");
        } else {
            Log.e("onLocationChanged", "Failure");
        }
        if (Prefs.getBoolean("LocationEnabled", false)) {
            if (location != null) {
                LatLng latLng = new LatLng((location.getLatitude()), (location.getLongitude()));
                if (myLocation != null) {
                    myLocation.remove();
                }
                myLocation = placeMarker(location.getLatitude(), location.getLongitude(), "Me", ((addressText != null && !addressText.isEmpty()) ? (addressText + "\n") : "") + (city != null ? city : ""), R.drawable.my_location_marker);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
                mMap.animateCamera(cameraUpdate);
            }
            Prefs.putBoolean("LocationEnabled", false);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        Log.e("onProviderEnabled", s);
        Prefs.putBoolean("LocationEnabled", true);
    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (Utils.IsInternetAvailable(getContext())) {
            location = getLocation();
            if (location != null) {
                if (isChecked) {
                    Prefs.putBoolean("Reporting", true);
                    shareLocation();
                    Utils.startAlarm(getContext());
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Enabling Location Sharing");
                    progressDialog.show();
                    new ProcessRequest<>(Process.MEMBER_CHECKLOCSTAT_UPDATE, new ProcessRequest.RequestListener<String>() {
                        @Override
                        public void onSuccess(String process, String result) {
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onFailure(String process) {
                            progressDialog.dismiss();
                            Utils.showDialog(getContext(), "Location Sharing Failed");
                            Prefs.putBoolean("Reporting", false);
                            reportingSwitch.setChecked(false);
                        }
                    }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "1");
                } else {
                    Prefs.putBoolean("Reporting", false);
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Disabling Location Sharing");
                    progressDialog.show();
                    new ProcessRequest<>(Process.MEMBER_CHECKLOCSTAT_UPDATE, new ProcessRequest.RequestListener<String>() {
                        @Override
                        public void onSuccess(String process, String result) {
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onFailure(String process) {
                            progressDialog.dismiss();
                            Utils.showDialog(getContext(), "Failed");
                            Prefs.putBoolean("Reporting", true);
                            reportingSwitch.setChecked(true);
                        }
                    }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "2");
                }
            } else {
                reportingSwitch.setChecked(false);
                new AlertDialog.Builder(getContext())
                        .setTitle("Use Location")
                        .setMessage("Turn on device location settings to use this feature.")
                        .setPositiveButton("SETTINGS", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION_SETTINGS);
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        } else {
            Utils.showDialog(getContext(), "No internet");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case LOCATION_SETTINGS:
                    break;
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void shareLocation() {
        if (getContext() != null) {
            Address address = Utils.getAddressFromLocation(getContext(), location.getLatitude(), location.getLongitude());
            if (address != null) {
                addressText = address.getAddressLine(0);
                city = address.getLocality();
                if (addressText == null) {
                    addressText = "";
                }
                if (city == null) {
                    city = "";
                }
            }
        }
        new ProcessRequest<String>(Process.MEMBER_APP_LOCATION_ADD, null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), city, addressText);
    }

    public void setMarkers(List<LocationLog> logs) {
        for (Map.Entry<Integer, Marker> integerMarkerEntry : markerMap.entrySet()) {
            Marker marker = integerMarkerEntry.getValue();
            if (marker != null) {
                marker.remove();
            }
        }
        for (LocationLog log : logs) {
            if (log.getLat() != 0 && log.getLng() != 0) {
                String addressText = "";
                String city = "";
                if (log.getCity() != null)
                    city = log.getCity();

                if (log.getText() != null)
                    addressText = log.getText();

                if (city.isEmpty() || addressText.isEmpty()) {
                    Address address = Utils.getAddressFromLocation(getActivity(), log.getLat(), log.getLng());
                    if (address != null) {
                        if (city.isEmpty())
                            city = address.getAddressLine(0);

                        if (addressText.isEmpty())
                            addressText = address.getLocality();
                    }
                }
                Marker marker = placeMarker(log.getLat(), log.getLng(), log.getName(), addressText + "\n" + city, R.drawable.old_location_marker);
                if (oldMarkerMap.containsKey(log.getMemberId())) {
                    Marker oldMarker = oldMarkerMap.get(log.getMemberId());
                    if (oldMarker != null)
                        oldMarker.remove();
                }
                oldMarkerMap.put(log.getMemberId(), marker);
            }
        }
    }

    public void setTeamMarkers(List<LocationLog> logs) {
        for (Map.Entry<Integer, Marker> integerMarkerEntry : oldMarkerMap.entrySet()) {
            Marker marker = integerMarkerEntry.getValue();
            if (marker != null) {
                marker.remove();
            }
        }
        for (LocationLog log : logs) {
            if (log.getLat() != 0 && log.getLng() != 0) {
                String addressText = "";
                String city = "";
                if (log.getCity() != null)
                    city = log.getCity();

                if (log.getText() != null)
                    addressText = log.getText();

                if (city.isEmpty() || addressText.isEmpty()) {
                    Address address = Utils.getAddressFromLocation(getActivity(), log.getLat(), log.getLng());
                    if (address != null) {
                        if (city.isEmpty())
                            city = address.getAddressLine(0);

                        if (addressText.isEmpty())
                            addressText = address.getLocality();
                    }
                }
                Marker marker = placeMarker(log.getLat(), log.getLng(), log.getName(), addressText + "\n" + city, R.drawable.live_location_marker);
                if (markerMap.containsKey(log.getMemberId())) {
                    Marker oldMarker = markerMap.get(log.getMemberId());
                    if (oldMarker != null)
                        oldMarker.remove();
                }
                markerMap.put(log.getMemberId(), marker);
            }
        }
    }

    @SuppressLint("InflateParams")
    public Marker placeMarker(double lat, double lng, String title, String snippet, int resId) {
        LatLng latLng = new LatLng(lat, lng);
        if (mMap != null) {
            Bitmap bitmap = null;
            if (markerBitmapMap.containsKey(resId)) {
                bitmap = markerBitmapMap.get(resId);
            } else {
                if (getActivity() != null) {
                    View markerLayout = LayoutInflater.from(getActivity()).inflate(R.layout.marker_layout, null);
                    ImageView oldImage = (ImageView) markerLayout.findViewById(R.id.old_image);
                    oldImage.setImageResource(resId);
                    bitmap = Utils.createDrawableFromView(getContext(), markerLayout);
                    markerBitmapMap.put(resId, bitmap);
                }
            }
            if (bitmap != null) {
                return mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .snippet(snippet)
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                        .title(title));
            } else {
                return mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .snippet(snippet)
                        .title(title));
            }
        } else
            return null;
    }

    public void updateMap() {
        try {
            mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
            mapFragment.getMapAsync(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mMap.getUiSettings().isMyLocationButtonEnabled())
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                LinearLayout info = new LinearLayout(getActivity());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getActivity());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getActivity());
                snippet.setTextColor(Color.GRAY);
                snippet.setGravity(Gravity.CENTER);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                if (marker.getSnippet() != null) {
                    if (!marker.getSnippet().isEmpty()) {
                        info.addView(snippet);
                    }
                }

                return info;
            }
        });

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                int attempts = 0;
                while (location == null && attempts < 5) {
                    location = getLocation();
                    attempts++;

                    if (Constants.DEBUG_MODE)
                        Log.e("Attempt " + String.valueOf(attempts), location == null ? "Failed" : "Success");
                }

                if (location == null) {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(0.0, 0.0), 0);
                    mMap.animateCamera(cameraUpdate);
                } else {
                    if (getContext() != null) {
                        Address address = Utils.getAddressFromLocation(getContext(), location.getLatitude(), location.getLongitude());
                        if (address != null) {
                            addressText = address.getAddressLine(0);
                            city = address.getLocality();
                            if (addressText == null) {
                                addressText = "";
                            }
                            if (city == null) {
                                city = "";
                            }
                        }
                    }
                    LatLng latLng = new LatLng((location.getLatitude()), (location.getLongitude()));
                    if (myLocation != null) {
                        myLocation.remove();
                    }
                    myLocation = placeMarker(location.getLatitude(), location.getLongitude(), "Me", ((addressText != null && !addressText.isEmpty()) ? (addressText + "\n") : "") + (city != null ? city : ""), R.drawable.my_location_marker);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
                    mMap.animateCamera(cameraUpdate);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                updateMap();
                break;

            case R.id.button_close:
                Prefs.putString("LiveTrack", "");
                trackingLayout.setVisibility(View.GONE);
                timeLayout.setVisibility(View.GONE);
                for (Map.Entry<Integer, Marker> integerMarkerEntry : markerMap.entrySet()) {
                    Marker marker = integerMarkerEntry.getValue();
                    if (marker != null) {
                        marker.remove();
                    }
                }
                for (Map.Entry<Integer, Marker> integerMarkerEntry : oldMarkerMap.entrySet()) {
                    Marker marker = integerMarkerEntry.getValue();
                    if (marker != null) {
                        marker.remove();
                    }
                }
                break;

            case R.id.time_layout:
                DatePickerDialog dayPickerDialog = new DatePickerDialog(getActivity(), this,
                        Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                dayPickerDialog.setTitle("Select Date");
                dayPickerDialog.show();
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        selectedTime = Calendar.getInstance();
        selectedTime.set(year, monthOfYear, dayOfMonth);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        dateText.setText(dateFormatter.format(selectedTime.getTime()));
        isoDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault()).format(selectedTime.getTime());

        timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedTime.set(Calendar.MINUTE, minute);
                SimpleDateFormat dateFormatter = new SimpleDateFormat("HH : mm a", Locale.US);
                fromTimeText.setText(dateFormatter.format(selectedTime.getTime()));
                fromTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(selectedTime.getTime());

                timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
//                        String am_pm = "am";
//                        if (hourOfDay > 12) {
//                            am_pm = "pm";
//                            hourOfDay -= 12;
//                        }
//                        String timeSelected = hourOfDay + " : " + minute + "  " + am_pm;
                        selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedTime.set(Calendar.MINUTE, minute);
                        SimpleDateFormat dateFormatter = new SimpleDateFormat("HH : mm a", Locale.US);
                        toTimeText.setText(dateFormatter.format(selectedTime.getTime()));
                        toTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(selectedTime.getTime());
                        updateLocationData();
                    }
                }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), false);
                timePickerDialog.setTitle("To Time");
                timePickerDialog.show();

            }
        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), false);
        timePickerDialog.setTitle("From Time");
        timePickerDialog.show();
    }

    private void updateLocationData() {
        if (Utils.IsInternetAvailable(getActivity())) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Updating Location");
            progressDialog.show();
            new ProcessRequest<>(Process.MEMBER_OLD_LOCATION_LIST, new ProcessRequest.RequestListener<List<LocationLog>>() {
                @Override
                public void onSuccess(String process, List<LocationLog> logs) {
                    progressDialog.dismiss();
                    for (Map.Entry<Integer, Marker> integerMarkerEntry : oldMarkerMap.entrySet()) {
                        Marker marker = integerMarkerEntry.getValue();
                        if (marker != null) {
                            marker.remove();
                        }
                    }
                    setMarkers(logs);
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Marker marker : oldMarkerMap.values()) {
                        if (marker != null) {
                            builder.include(marker.getPosition());
                        }
                    }
                    try {
                        LatLngBounds bounds = builder.build();
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 400);
                        mMap.animateCamera(cameraUpdate);
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(String process) {
                    progressDialog.dismiss();
                    for (Map.Entry<Integer, Marker> integerMarkerEntry : oldMarkerMap.entrySet()) {
                        Marker marker = integerMarkerEntry.getValue();
                        if (marker != null) {
                            marker.remove();
                        }
                    }
                }
            }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "0", String.valueOf(member.getMemberId()), isoDate, fromTime, toTime);//"01:00:00", "23:59:00");
        }
    }
}
