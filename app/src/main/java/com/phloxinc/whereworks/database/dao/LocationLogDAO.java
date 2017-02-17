package com.phloxinc.whereworks.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;

import com.phloxinc.whereworks.bo.LocationLog;
import com.phloxinc.whereworks.database.BaseDAO;
import com.phloxinc.whereworks.database.BaseEntity;
import com.phloxinc.whereworks.database.Column;

import java.util.ArrayList;
import java.util.List;

public class LocationLogDAO extends BaseDAO {

    private static LocationLogDAO instance;
    private static String COL_LOCATION_ID = "locationId";
    private static String COL_MEMBER_ID = "memberId";
    private static String COL_NAME = "name";
    private static String COL_EMAIL = "email";
    private static String COL_CONTACT_NUMBER = "number";
    private static String COL_LAT = "lat";
    private static String COL_LNG = "lng";
    private static String COL_DATE = "date";
    private static String COL_LOC_ENTRY = "locEntry";
    private static String COL_LOC_UPDATE = "locUpdate";
    private static String COL_TEXT = "text";
    private static String COL_CITY = "city";

    private static String tableName = "locations";
    private static Column[] columns = {
            new Column(COL_LOCATION_ID, Column.TYPE_INTEGER, Column.NULLABLE_NOT_NULL),
            new Column(COL_MEMBER_ID, Column.TYPE_INTEGER, Column.NULLABLE_NULL),
            new Column(COL_NAME, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_EMAIL, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_CONTACT_NUMBER, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_LAT, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_LNG, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_DATE, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_LOC_ENTRY, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_LOC_UPDATE, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_TEXT, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_CITY, Column.TYPE_TEXT, Column.NULLABLE_NULL)
    };

    @SuppressWarnings("all")
    public LocationLogDAO() {
        super(tableName, columns);
    }

    public static LocationLogDAO getInstance() {
        if (instance == null) {
            instance = new LocationLogDAO();
        }
        return instance;
    }

    @Override
    protected BaseEntity cursorToEntity(Cursor cursor) {
        LocationLog locationLog = new LocationLog();

        locationLog.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
        locationLog.setLocationId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_LOCATION_ID)));
        locationLog.setMemberId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_MEMBER_ID)));
        locationLog.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
        locationLog.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)));
        locationLog.setNumber(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTACT_NUMBER)));
        locationLog.setLat(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(COL_LAT))));
        locationLog.setLng(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(COL_LNG))));
        locationLog.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)));
        locationLog.setLocEntry(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOC_ENTRY)));
        locationLog.setLocUpdate(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOC_UPDATE)));
        locationLog.setText(cursor.getString(cursor.getColumnIndexOrThrow(COL_TEXT)));
        locationLog.setCity(cursor.getString(cursor.getColumnIndexOrThrow(COL_CITY)));

        return locationLog;
    }

    @Override
    public ContentValues getContentValues() {
        LocationLog locationLog = (LocationLog) entity;
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_LOCATION_ID, locationLog.getLocationId());
        contentValues.put(COL_MEMBER_ID, locationLog.getMemberId());
        contentValues.put(COL_NAME, locationLog.getName());
        contentValues.put(COL_EMAIL, locationLog.getEmail());
        contentValues.put(COL_CONTACT_NUMBER, locationLog.getNumber());
        contentValues.put(COL_LAT, locationLog.getLat());
        contentValues.put(COL_LNG, locationLog.getLng());
        contentValues.put(COL_DATE, locationLog.getDate());
        contentValues.put(COL_LOC_ENTRY, locationLog.getLocEntry());
        contentValues.put(COL_LOC_UPDATE, locationLog.getLocUpdate());
        contentValues.put(COL_TEXT, locationLog.getText());
        contentValues.put(COL_CITY, locationLog.getCity());

        return contentValues;
    }

    @Override
    public ArrayList<LocationLog> loadList(Cursor queryCursor) {
        ArrayList<LocationLog> locationLogs = new ArrayList<>();

        queryCursor.moveToFirst();
        while (!queryCursor.isAfterLast()) {
            LocationLog member = (LocationLog) cursorToEntity(queryCursor);
            locationLogs.add(member);
            queryCursor.moveToNext();
        }
        queryCursor.close();

        return locationLogs;
    }

    public ArrayList<LocationLog> getAllLogs() {
        String query = SQLiteQueryBuilder.buildQueryString(true, tableName, null, null, null, null, null, null);
        Cursor cursor = MemberDAO.getInstance().executeRawQueryForCursor(query, null);
        return loadList(cursor);
    }

    public LocationLog getLog(int locationId) {
        String[] args = { String.valueOf(locationId) };
        String query = SQLiteQueryBuilder.buildQueryString(true, tableName, null, COL_LOCATION_ID + "=?", null, null, null, null);
        Cursor cursor = MemberDAO.getInstance().executeRawQueryForCursor(query, args);

        List<LocationLog> locationLogs = loadList(cursor);
        int first = 0;
        if (!locationLogs.isEmpty()) {
            return locationLogs.get(first);
        }
        return null;
    }

    public List<LocationLog> getLogsByMemberId(int memberId) {
        String[] args = { String.valueOf(memberId) };
        String query = SQLiteQueryBuilder.buildQueryString(true, tableName, null, COL_MEMBER_ID + "=?", null, null, null, null);
        Cursor cursor = MemberDAO.getInstance().executeRawQueryForCursor(query, args);
        return loadList(cursor);
    }
}
