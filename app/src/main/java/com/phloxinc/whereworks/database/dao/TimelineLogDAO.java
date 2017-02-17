package com.phloxinc.whereworks.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;

import com.phloxinc.whereworks.bo.TimelineLog;
import com.phloxinc.whereworks.database.BaseDAO;
import com.phloxinc.whereworks.database.BaseEntity;
import com.phloxinc.whereworks.database.Column;

import java.util.ArrayList;
import java.util.List;

public class TimelineLogDAO extends BaseDAO {

    private static TimelineLogDAO instance;
    private static String COL_LOG_ID = "logId";
    private static String COL_ACTOR_ID = "actorId";
    private static String COL_PARENT_ID = "parentId";
    private static String COL_CHILD_ID = "childId";
    private static String COL_LOG_TYPE = "logType";
    private static String COL_LOG_ACTION = "logAction";
    private static String COL_LOG_NAME = "name";
    private static String COL_LOG_TEAM_NAME = "teamName";
    private static String COL_LOG_DATE = "date";
    private static String COL_LOG_SDT = "sdt";
    private static String tableName = "timeline";
    private static Column[] columns = {
            new Column(COL_LOG_ID, Column.TYPE_INTEGER, Column.NULLABLE_NOT_NULL),
            new Column(COL_ACTOR_ID, Column.TYPE_INTEGER, Column.NULLABLE_NULL),
            new Column(COL_PARENT_ID, Column.TYPE_INTEGER, Column.NULLABLE_NULL),
            new Column(COL_CHILD_ID, Column.TYPE_INTEGER, Column.NULLABLE_NULL),
            new Column(COL_LOG_TYPE, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_LOG_ACTION, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_LOG_NAME, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_LOG_TEAM_NAME, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_LOG_DATE, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_LOG_SDT, Column.TYPE_TIMESTAMP, Column.NULLABLE_NULL),
    };

    public TimelineLogDAO() {
        super(tableName, columns);
    }

    public static TimelineLogDAO getInstance() {
        if (instance == null) {
            instance = new TimelineLogDAO();
        }
        return instance;
    }

    @Override
    public BaseEntity cursorToEntity(Cursor cursor) {
        TimelineLog timelineLog = new TimelineLog();

        timelineLog.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
        timelineLog.setLogId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_LOG_ID)));
        timelineLog.setActorId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ACTOR_ID)));
        timelineLog.setParentId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_PARENT_ID)));
        timelineLog.setChildId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_CHILD_ID)));
        timelineLog.setLogType(TimelineLog.LogType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOG_TYPE))));
        timelineLog.setLogAction(TimelineLog.LogAction.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOG_ACTION))));
        timelineLog.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOG_NAME)));
        timelineLog.setTeamName(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOG_TEAM_NAME)));
        timelineLog.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOG_DATE)));
        timelineLog.setSdt(cursor.getInt(cursor.getColumnIndexOrThrow(COL_LOG_SDT)));

        return timelineLog;
    }

    @Override
    public ContentValues getContentValues() {
        TimelineLog timelineLog = (TimelineLog) entity;
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_LOG_ID, timelineLog.getLogId());
        contentValues.put(COL_ACTOR_ID, timelineLog.getActorId());
        contentValues.put(COL_PARENT_ID, timelineLog.getParentId());
        contentValues.put(COL_CHILD_ID, timelineLog.getChildId());
        contentValues.put(COL_LOG_TYPE, String.valueOf(timelineLog.getLogType()));
        contentValues.put(COL_LOG_ACTION, String.valueOf(timelineLog.getLogAction()));
        contentValues.put(COL_LOG_NAME, timelineLog.getName());
        contentValues.put(COL_LOG_TEAM_NAME, timelineLog.getTeamName());
        contentValues.put(COL_LOG_DATE, timelineLog.getDate());
        contentValues.put(COL_LOG_SDT, timelineLog.getSdt());

        return contentValues;
    }

    @Override
    public ArrayList<TimelineLog> loadList(Cursor queryCursor) {
        ArrayList<TimelineLog> timelineList = new ArrayList<>();

        queryCursor.moveToFirst();
        while (!queryCursor.isAfterLast()) {
            TimelineLog timelineLog = (TimelineLog) cursorToEntity(queryCursor);
            timelineList.add(timelineLog);
            queryCursor.moveToNext();
        }
        queryCursor.close();

        return timelineList;
    }

    public ArrayList<TimelineLog> getAllTimelineLogs () {
        String query = SQLiteQueryBuilder.buildQueryString(true, tableName, null, null, null, null, null, null);
        Cursor cursor = MemberDAO.getInstance().executeRawQueryForCursor(query, null);
        return loadList(cursor);
    }

    public TimelineLog getTimelineLogById (int logId) {
        String[] args = { String.valueOf(logId) };
        String query = SQLiteQueryBuilder.buildQueryString(true, tableName, null, COL_LOG_ID + "=?", null, null, null, null);
        Cursor cursor = MemberDAO.getInstance().executeRawQueryForCursor(query, args);

        List<TimelineLog> memberList = loadList(cursor);
        int first = 0;
        if (!memberList.isEmpty()) {
            return memberList.get(first);
        }
        return null;
    }
}
