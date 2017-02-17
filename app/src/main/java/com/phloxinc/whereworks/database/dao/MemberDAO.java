package com.phloxinc.whereworks.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;

import com.phloxinc.whereworks.bo.Member;
import com.phloxinc.whereworks.database.BaseDAO;
import com.phloxinc.whereworks.database.BaseEntity;
import com.phloxinc.whereworks.database.Column;

import java.util.ArrayList;
import java.util.List;

public class MemberDAO extends BaseDAO {

    private static MemberDAO instance;
    private static String COL_MEMBER_ID = "memberId";
    private static String COL_FULL_NAME = "fullName";
    private static String COL_EMAIL = "email";
    private static String COL_CONTACT_NUMBER = "number";
    private static String COL_STATUS = "status";
    private static String COL_LAT = "lat";
    private static String COL_LNG = "lng";
    private static String COL_PHOTO = "photo";
    private static String COL_TIMEZONE = "timezone";
    private static String COL_SDT = "sdt";
    private static String COL_UDT = "udt";

    private static String tableName = "member";
    private static Column[] columns = {
            new Column(COL_MEMBER_ID, Column.TYPE_INTEGER, Column.NULLABLE_NOT_NULL),
            new Column(COL_FULL_NAME, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_EMAIL, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_CONTACT_NUMBER, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_STATUS, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_LAT, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_LNG, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_PHOTO, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_TIMEZONE, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_SDT, Column.TYPE_TIMESTAMP, Column.NULLABLE_NULL),
            new Column(COL_UDT, Column.TYPE_TIMESTAMP, Column.NULLABLE_NULL)
    };

    @SuppressWarnings("all")
    public MemberDAO() {
        super(tableName, columns);
    }

    public static MemberDAO getInstance() {
        if (instance == null) {
            instance = new MemberDAO();
        }
        return instance;
    }

    @Override
    protected BaseEntity cursorToEntity(Cursor cursor) {
        Member member = new Member();

        member.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
        member.setMemberId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_MEMBER_ID)));
        member.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(COL_FULL_NAME)));
        member.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)));
        member.setContactNumber(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTACT_NUMBER)));
        member.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)));
        member.setCurrentLat(cursor.getString(cursor.getColumnIndexOrThrow(COL_LAT)));
        member.setCurrentLng(cursor.getString(cursor.getColumnIndexOrThrow(COL_LNG)));
        member.setMemberPhoto(cursor.getString(cursor.getColumnIndexOrThrow(COL_PHOTO)));
        member.setTimeZone(cursor.getString(cursor.getColumnIndexOrThrow(COL_TIMEZONE)));
        member.setSdt(cursor.getLong(cursor.getColumnIndexOrThrow(COL_SDT)));
        member.setUdt(cursor.getLong(cursor.getColumnIndexOrThrow(COL_UDT)));

        return member;
    }

    @Override
    public ContentValues getContentValues() {
        Member member = (Member) entity;
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_MEMBER_ID, member.getMemberId());
        contentValues.put(COL_FULL_NAME, member.getFullName());
        contentValues.put(COL_EMAIL, member.getEmail());
        contentValues.put(COL_CONTACT_NUMBER, member.getContactNumber());
        contentValues.put(COL_STATUS, member.getStatus());
        contentValues.put(COL_LAT, member.getCurrentLat());
        contentValues.put(COL_LNG, member.getCurrentLng());
        contentValues.put(COL_PHOTO, member.getMemberPhoto());
        contentValues.put(COL_TIMEZONE, member.getTimeZone());
        contentValues.put(COL_SDT, member.getSdt());
        contentValues.put(COL_UDT, member.getUdt());

        return contentValues;
    }

    @Override
    public ArrayList<Member> loadList(Cursor queryCursor) {
        ArrayList<Member> memberList = new ArrayList<>();

        queryCursor.moveToFirst();
        while (!queryCursor.isAfterLast()) {
            Member member = (Member) cursorToEntity(queryCursor);
            memberList.add(member);
            queryCursor.moveToNext();
        }
        queryCursor.close();

        return memberList;
    }

    public ArrayList<Member> getAllMember() {
        String query = SQLiteQueryBuilder.buildQueryString(true, tableName, null, null, null, null, null, null);
        Cursor cursor = MemberDAO.getInstance().executeRawQueryForCursor(query, null);
        return loadList(cursor);
    }

    public ArrayList<Member> getAllNonPendingMember() {
        String[] args = { String.valueOf(2) };
        String query = SQLiteQueryBuilder.buildQueryString(true, tableName, null, COL_STATUS + "!=?", null, null, null, null);
        Cursor cursor = MemberDAO.getInstance().executeRawQueryForCursor(query, args);
        return loadList(cursor);
    }

    public Member getMemberByMemberId(int memberId) {
        String[] args = { String.valueOf(memberId) };
        String query = SQLiteQueryBuilder.buildQueryString(true, tableName, null, COL_MEMBER_ID + "=?", null, null, null, null);
        Cursor cursor = MemberDAO.getInstance().executeRawQueryForCursor(query, args);

        List<Member> memberList = loadList(cursor);
        int first = 0;
        if (!memberList.isEmpty()) {
            return memberList.get(first);
        }
        return null;
    }
}
