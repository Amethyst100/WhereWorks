package com.phloxinc.whereworks.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;

import com.phloxinc.whereworks.bo.MemberTeamMap;
import com.phloxinc.whereworks.database.BaseDAO;
import com.phloxinc.whereworks.database.BaseEntity;
import com.phloxinc.whereworks.database.Column;
import com.phloxinc.whereworks.prefs.Prefs;

import java.util.ArrayList;
import java.util.List;

public class MemberTeamMapsDAO extends BaseDAO {

    private static MemberTeamMapsDAO instance;
    private static String COL_TEAM_ID = "teamId";
    private static String COL_MEMBER_ID = "memberId";

    private static String tableName = "memberTeamMap";
    private static Column[] columns = {
            new Column(COL_TEAM_ID, Column.TYPE_INTEGER, Column.NULLABLE_NULL),
            new Column(COL_MEMBER_ID, Column.TYPE_INTEGER, Column.NULLABLE_NULL)
    };

    @SuppressWarnings("all")
    public MemberTeamMapsDAO() {
        super(tableName, columns);
    }

    public static MemberTeamMapsDAO getInstance() {
        if (instance == null) {
            instance = new MemberTeamMapsDAO();
        }
        return instance;
    }

    @Override
    protected BaseEntity cursorToEntity(Cursor cursor) {
        MemberTeamMap map = new MemberTeamMap();

        map.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
        map.setTeamId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_TEAM_ID)));
        map.setMemberId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_MEMBER_ID)));

        return map;
    }

    @Override
    public ContentValues getContentValues() {
        MemberTeamMap map = (MemberTeamMap) entity;
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_TEAM_ID, map.getTeamId());
        contentValues.put(COL_MEMBER_ID, map.getMemberId());

        return contentValues;
    }

    @Override
    public ArrayList<MemberTeamMap> loadList(Cursor queryCursor) {
        ArrayList<MemberTeamMap> mapList = new ArrayList<>();

        queryCursor.moveToFirst();
        while (!queryCursor.isAfterLast()) {
            MemberTeamMap map = (MemberTeamMap) cursorToEntity(queryCursor);
            mapList.add(map);
            queryCursor.moveToNext();
        }
        queryCursor.close();

        return mapList;
    }

    public List<MemberTeamMap> getAllMaps() {
        String query = SQLiteQueryBuilder.buildQueryString(true, tableName, null, null, null, null, null, null);
        Cursor cursor = TeamDAO.getInstance().executeRawQueryForCursor(query, null);
        return loadList(cursor);
    }

    public List<MemberTeamMap> getMapListByTeamId(int teamId) {
        String[] args = { String.valueOf(teamId) };
        String userId = Prefs.getString("userId", "0");
        String query = SQLiteQueryBuilder.buildQueryString(true, tableName, null, COL_TEAM_ID + "=? AND " + COL_MEMBER_ID + "!=" + userId, null, null, null, null);
        Cursor cursor = TeamDAO.getInstance().executeRawQueryForCursor(query, args);
        return loadList(cursor);
    }

    public List<MemberTeamMap> getMapListByMemberId(int memberId) {
        String[] args = { String.valueOf(memberId) };
        String query = SQLiteQueryBuilder.buildQueryString(true, tableName, null, COL_MEMBER_ID + "=?", null, null, null, null);
        Cursor cursor = TeamDAO.getInstance().executeRawQueryForCursor(query, args);
        return loadList(cursor);
    }

    public MemberTeamMap getMap(int teamId, int memberId) {
        String[] args = { String.valueOf(teamId) };
        String query = SQLiteQueryBuilder.buildQueryString(true, tableName, null, COL_TEAM_ID + "=? AND " + COL_MEMBER_ID + "=" + String.valueOf(memberId), null, null, null, null);
        Cursor cursor = TeamDAO.getInstance().executeRawQueryForCursor(query, args);
        List<MemberTeamMap> maps = loadList(cursor);
        if (!maps.isEmpty())
            return maps.get(0);
        return null;
    }
}
