package com.phloxinc.whereworks.receivers;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.phloxinc.whereworks.database.DatabaseUtils;
import com.phloxinc.whereworks.prefs.Prefs;
import com.phloxinc.whereworks.process.Process;
import com.phloxinc.whereworks.process.ProcessRequest;
import com.phloxinc.whereworks.utils.Utils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseUtils.initDB(context);
        if (Prefs.getBoolean("Reporting", false)) {
            Location location = Utils.getLocation(context);
            if (location != null) {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(context, Locale.getDefault());

                String address = "";
                String city = "";
                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    address = addresses.get(0).getAddressLine(0);
                    city = addresses.get(0).getLocality();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                new ProcessRequest<String>(Process.MEMBER_APP_LOCATION_ADD, null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), city, address);

//                for (Team team : Team.all()) {
//                    new ProcessRequest<String>(Process.MEMBER_LIVE_LOCATION_LIST, null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(team.getTeamId()));
//                }
            }
            Utils.startAlarm(context);
        }
    }
}
