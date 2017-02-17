package com.phloxinc.whereworks.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;

import com.phloxinc.whereworks.bo.Team;
import com.phloxinc.whereworks.database.BaseDAO;
import com.phloxinc.whereworks.database.BaseEntity;
import com.phloxinc.whereworks.database.Column;
import com.phloxinc.whereworks.prefs.Prefs;

import java.util.ArrayList;
import java.util.List;

public class TeamDAO extends BaseDAO {
    private static TeamDAO instance;
    private static String COL_TEAM_ID = "teamId";
    private static String COL_NAME = "name";
    private static String COL_DESC = "desc";
    private static String COL_PARENT_ID = "parentId";
    private static String COL_HEAD_ID = "headId";
    private static String COL_PHOTO = "photo";
    private static String COL_SDT = "sdt";
    private static String COL_UDT = "udt";

    private static String tableName = "team";
    private static Column[] columns = {
            new Column(COL_TEAM_ID, Column.TYPE_INTEGER, Column.NULLABLE_NOT_NULL),
            new Column(COL_NAME, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_DESC, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_PARENT_ID, Column.TYPE_INTEGER, Column.NULLABLE_NULL),
            new Column(COL_HEAD_ID, Column.TYPE_INTEGER, Column.NULLABLE_NULL),
            new Column(COL_PHOTO, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_SDT, Column.TYPE_TIMESTAMP, Column.NULLABLE_NULL),
            new Column(COL_UDT, Column.TYPE_TIMESTAMP, Column.NULLABLE_NULL)
    };

    @SuppressWarnings("all")
    public TeamDAO() {
        super(tableName, columns);
    }

    public static TeamDAO getInstance() {
        if (instance == null) {
            instance = new TeamDAO();
        }
        return instance;
    }

    @Override
    protected BaseEntity cursorToEntity(Cursor cursor) {
        Team team = new Team();

        team.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
        team.setTeamId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_TEAM_ID)));
        team.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
        team.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_DESC)));
        team.setParentId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_PARENT_ID)));
        team.setHeadId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_HEAD_ID)));
        team.setTeamPhoto(cursor.getString(cursor.getColumnIndexOrThrow(COL_PHOTO)));
        team.setSdt(cursor.getLong(cursor.getColumnIndexOrThrow(COL_SDT)));
        team.setUdt(cursor.getLong(cursor.getColumnIndexOrThrow(COL_UDT)));

        return team;
    }

    @Override
    public ContentValues getContentValues() {
        Team team = (Team) entity;
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_TEAM_ID, team.getTeamId());
        contentValues.put(COL_NAME, team.getName());
        contentValues.put(COL_DESC, team.getDescription());
        contentValues.put(COL_PARENT_ID, team.getParentId());
        contentValues.put(COL_HEAD_ID, team.getHeadId());
        contentValues.put(COL_PHOTO, team.getTeamPhoto());
        contentValues.put(COL_SDT, team.getSdt());
        contentValues.put(COL_UDT, team.getUdt());

        return contentValues;
    }

    @Override
    public ArrayList<Team> loadList(Cursor queryCursor) {
        ArrayList<Team> teamList = new ArrayList<>();

        queryCursor.moveToFirst();
        while (!queryCursor.isAfterLast()) {
            Team team = (Team) cursorToEntity(queryCursor);
            teamList.add(team);
            queryCursor.moveToNext();
        }
        queryCursor.close();

        return teamList;
    }

    public ArrayList<Team> getAllTeams() {
        String query = SQLiteQueryBuilder.buildQueryString(true, tableName, null, null, null, null, null, null);
        Cursor cursor = TeamDAO.getInstance().executeRawQueryForCursor(query, null);
        return loadList(cursor);
    }

    public Team getTeamByTeamId(int teamId) {
        String[] args = { String.valueOf(teamId) };
        String query = SQLiteQueryBuilder.buildQueryString(true, tableName, null, COL_TEAM_ID + "=?", null, null, null, null);
        Cursor cursor = TeamDAO.getInstance().executeRawQueryForCursor(query, args);

        List<Team> teamList = loadList(cursor);
        int first = 0;
        if (!teamList.isEmpty()) {
            return teamList.get(first);
        }
        return null;
    }

    public List<Team> getParentTeams() {
        String[] args = { String.valueOf(0) };
        String userId = Prefs.getString("userId", "0");
        String query = SQLiteQueryBuilder.buildQueryString(true, tableName, null, COL_PARENT_ID + "=? OR " + COL_HEAD_ID + "!=" + userId, null, null, null, null);
        Cursor cursor = TeamDAO.getInstance().executeRawQueryForCursor(query, args);
        return loadList(cursor);
    }

    public List<Team> getSubTeams(int parentId) {
        String[] args = { String.valueOf(parentId) };
        String query = SQLiteQueryBuilder.buildQueryString(true, tableName, null, COL_PARENT_ID + "=?", null, null, null, null);
        Cursor cursor = TeamDAO.getInstance().executeRawQueryForCursor(query, args);
        return loadList(cursor);
    }
}
