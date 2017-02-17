package com.phloxinc.whereworks.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;

import com.phloxinc.whereworks.bo.Notification;
import com.phloxinc.whereworks.database.BaseDAO;
import com.phloxinc.whereworks.database.BaseEntity;
import com.phloxinc.whereworks.database.Column;

import java.util.ArrayList;
import java.util.List;

public class NotificationDAO extends BaseDAO {

    private static NotificationDAO instance;
    private static String COL_NOTIFICATION_ID = "notificationId";
    private static String COL_FULL_NAME = "fullName";
    private static String COL_TEAM_NAME = "teamName";
    private static String COL_TEXT = "text";
    private static String COL_TYPE = "type";
    private static String COL_DATE = "date";

    private static String tableName = "notification";
    private static Column[] columns = {
            new Column(COL_NOTIFICATION_ID, Column.TYPE_INTEGER, Column.NULLABLE_NOT_NULL),
            new Column(COL_FULL_NAME, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_TEAM_NAME, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_TEXT, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_TYPE, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_DATE, Column.TYPE_TEXT, Column.NULLABLE_NULL)
    };

    @SuppressWarnings("WeakerAccess")
    public NotificationDAO() {
        super(tableName, columns);
    }

    public static NotificationDAO getInstance() {
        if (instance == null) {
            instance = new NotificationDAO();
        }
        return instance;
    }

    @Override
    protected BaseEntity cursorToEntity(Cursor cursor) {
        Notification notification = new Notification();

        notification.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
        notification.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(COL_FULL_NAME)));
        notification.setTeamName(cursor.getString(cursor.getColumnIndexOrThrow(COL_TEAM_NAME)));
        notification.setText(cursor.getString(cursor.getColumnIndexOrThrow(COL_TEXT)));
        notification.setType(Notification.NotificationType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE))));
        notification.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)));

        return notification;
    }

    @Override
    public ContentValues getContentValues() {
        Notification notification = (Notification) entity;
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_NOTIFICATION_ID, notification.getNotificationId());
        contentValues.put(COL_FULL_NAME, notification.getFullName());
        contentValues.put(COL_TEAM_NAME, notification.getTeamName());
        contentValues.put(COL_TEXT, notification.getText());
        contentValues.put(COL_TYPE, String.valueOf(notification.getType()));
        contentValues.put(COL_DATE, notification.getDate());

        return contentValues;
    }

    @Override
    public ArrayList<Notification> loadList(Cursor queryCursor) {
        ArrayList<Notification> notificationList = new ArrayList<>();

        queryCursor.moveToFirst();
        while (!queryCursor.isAfterLast()) {
            Notification member = (Notification) cursorToEntity(queryCursor);
            notificationList.add(member);
            queryCursor.moveToNext();
        }
        queryCursor.close();

        return notificationList;
    }

    public ArrayList<Notification> getAllNotifications() {
        String query = SQLiteQueryBuilder.buildQueryString(true, tableName, null, null, null, null, null, null);
        Cursor cursor = MemberDAO.getInstance().executeRawQueryForCursor(query, null);
        return loadList(cursor);
    }

    public Notification getNotificationByNotificationId(int notificationId) {
        String[] args = { String.valueOf(notificationId) };
        String query = SQLiteQueryBuilder.buildQueryString(true, tableName, null, COL_NOTIFICATION_ID + "=?", null, null, null, null);
        Cursor cursor = MemberDAO.getInstance().executeRawQueryForCursor(query, args);

        List<Notification> notificationList = loadList(cursor);
        int first = 0;
        if (!notificationList.isEmpty()) {
            return notificationList.get(first);
        }
        return null;
    }
}
