package com.phloxinc.whereworks;

import android.app.Activity;

public class ActivityController {

    private static ActivityController instance;
    private Activity currentActivity;

    public static ActivityController getInstance() {
        if (instance == null) {
            instance = new ActivityController();
        }
        return instance;
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }
}
