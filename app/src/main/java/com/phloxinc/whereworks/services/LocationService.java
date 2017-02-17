package com.phloxinc.whereworks.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationService extends Service implements LocationListener {

//    private final Context mContext;

    private static LocationService instance;

    Location location;
    double latitude;
    double longitude;

    public LocationService() {
//        this.mContext = context;
        location = getLocation();
    }

    public static LocationService getInstance() {
        if (instance == null) {
            instance = new LocationService();
        }
        return instance;
    }

    @SuppressWarnings("MissingPermission")
    public Location getLocation() {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Location location = null;
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isWifiEnabled = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (isGPSEnabled) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, this);
                Log.d("GPS Enabled", "GPS Enabled");

                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    Log.i("GPS_LOCATION", "Location: " + location.toString());
                }
            }
            if (location == null)
                if (wifi.isWifiEnabled()) {
                    locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1, 1, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                        if (location != null) {
                            Log.i("WIFI", "Location: " + location.toString());
                        }
                    }
                }
            if (location == null)
                if (isNetworkEnabled) {
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        Criteria criteria = new Criteria();
                        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                        String provider = locationManager.getBestProvider(criteria, true);
                        locationManager.requestLocationUpdates(provider, 1000, 5, this);
                        location = locationManager.getLastKnownLocation(provider);
                        if (location != null) {
                            Log.i("Cellular", "Location: " + location.toString());
                        }
                    }
                }
//        }
        return location;
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public String getCity() {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            return addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getAddress() {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            return addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

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
}
