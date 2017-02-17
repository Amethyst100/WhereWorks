package com.phloxinc.whereworks.database;

import android.content.Context;

import com.phloxinc.whereworks.activities.SplashScreen;
import com.phloxinc.whereworks.constant.Constants;
import com.phloxinc.whereworks.database.dao.ChatDAO;
import com.phloxinc.whereworks.database.dao.LocationLogDAO;
import com.phloxinc.whereworks.database.dao.MemberDAO;
import com.phloxinc.whereworks.database.dao.MemberTeamMapsDAO;
import com.phloxinc.whereworks.database.dao.MessageDAO;
import com.phloxinc.whereworks.database.dao.NotificationDAO;
import com.phloxinc.whereworks.database.dao.TeamDAO;
import com.phloxinc.whereworks.database.dao.TimelineLogDAO;
import com.phloxinc.whereworks.prefs.PreferenceController;

import java.util.ArrayList;

public class DatabaseUtils {

    public static void initDB(Context context) {
        ArrayList<Class<? extends Persistable>> persistableList = new ArrayList<>();
        persistableList.add(PreferenceController.PreferenceDAO.class);
        persistableList.add(MemberDAO.class);
        persistableList.add(TeamDAO.class);
        persistableList.add(TimelineLogDAO.class);
        persistableList.add(NotificationDAO.class);
        persistableList.add(MemberTeamMapsDAO.class);
        persistableList.add(MessageDAO.class);
        persistableList.add(ChatDAO.class);
        persistableList.add(LocationLogDAO.class);
        DatabaseManager.initialize(context, Constants.DATABASE_NAME, Constants.DATABASE_VERSION, persistableList);
    }
}
