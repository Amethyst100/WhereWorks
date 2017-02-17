package com.phloxinc.whereworks.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.phloxinc.whereworks.ActivityController;
import com.phloxinc.whereworks.constant.Constants;
import com.phloxinc.whereworks.R;
import com.phloxinc.whereworks.database.DatabaseManager;
import com.phloxinc.whereworks.database.DatabaseUtils;
import com.phloxinc.whereworks.database.Persistable;
import com.phloxinc.whereworks.database.dao.ChatDAO;
import com.phloxinc.whereworks.database.dao.MemberDAO;
import com.phloxinc.whereworks.database.dao.MemberTeamMapsDAO;
import com.phloxinc.whereworks.database.dao.MessageDAO;
import com.phloxinc.whereworks.database.dao.NotificationDAO;
import com.phloxinc.whereworks.database.dao.TeamDAO;
import com.phloxinc.whereworks.database.dao.TimelineLogDAO;
import com.phloxinc.whereworks.prefs.PreferenceController;
import com.phloxinc.whereworks.utils.Utils;

import java.util.ArrayList;

public class SplashScreen extends Activity {

    private static final int SPLASH_SCREEN_DELAY_MILLIS = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        DatabaseUtils.initDB(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                finish();
            }
        }, SPLASH_SCREEN_DELAY_MILLIS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityController.getInstance().setCurrentActivity(this);
    }


}
