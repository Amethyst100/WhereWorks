package com.phloxinc.whereworks.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;

import com.phloxinc.whereworks.bo.Chat;
import com.phloxinc.whereworks.database.BaseDAO;
import com.phloxinc.whereworks.database.BaseEntity;
import com.phloxinc.whereworks.database.Column;

import java.util.ArrayList;
import java.util.List;

public class ChatDAO extends BaseDAO {

    private static ChatDAO instance;
    private static String COL_MEMBER_ID = "messageText";
    private static String COL_TEAM_ID = "senderId";
    private static String COL_TYPE = "type";

    private static String tableName = "chat";
    private static Column[] columns = {
            new Column(COL_MEMBER_ID, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_TEAM_ID, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_TYPE, Column.TYPE_TEXT, Column.NULLABLE_NULL)
    };

    @SuppressWarnings("all")
    public ChatDAO() {
        super(tableName, columns);
    }

    public static ChatDAO getInstance() {
        if (instance == null) {
            instance = new ChatDAO();
        }
        return instance;
    }

    @Override
    protected BaseEntity cursorToEntity(Cursor cursor) {
        Chat chat = new Chat();

        chat.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
        chat.setMemberId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_MEMBER_ID)));
        chat.setTeamId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_TEAM_ID)));
        chat.setType(Chat.Type.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE))));

        return chat;
    }

    @Override
    public ContentValues getContentValues() {
        Chat chat = (Chat) entity;
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_MEMBER_ID, chat.getMemberId());
        contentValues.put(COL_TEAM_ID, chat.getTeamId());
        contentValues.put(COL_TYPE, String.valueOf(chat.getType()));

        return contentValues;
    }

    @Override
    public ArrayList<Chat> loadList(Cursor queryCursor) {
        ArrayList<Chat> chatList = new ArrayList<>();

        queryCursor.moveToFirst();
        while (!queryCursor.isAfterLast()) {
            Chat chat = (Chat) cursorToEntity(queryCursor);
            chatList.add(chat);
            queryCursor.moveToNext();
        }
        queryCursor.close();

        return chatList;
    }

    public ArrayList<Chat> getAllChats() {
        String query = SQLiteQueryBuilder.buildQueryString(true, tableName, null, null, null, null, null, null);
        Cursor cursor = MemberDAO.getInstance().executeRawQueryForCursor(query, null);
        return loadList(cursor);
    }

    public Chat getChat(long chatId) {
        String[] args = { String.valueOf(chatId) };
        String query = SQLiteQueryBuilder.buildQueryString(true, tableName, null, COL_ID + "=?", null, null, null, null);
        Cursor cursor = MemberDAO.getInstance().executeRawQueryForCursor(query, args);

        List<Chat> chatList = loadList(cursor);
        int first = 0;
        if (!chatList.isEmpty()) {
            return chatList.get(first);
        }
        return null;
    }

    public Chat getOneToOneChat(int memberId) {
        String[] args = { String.valueOf(memberId) };
        String query = SQLiteQueryBuilder.buildQueryString(true, tableName, null, COL_MEMBER_ID + "=?", null, null, null, null);
        Cursor cursor = MemberDAO.getInstance().executeRawQueryForCursor(query, args);

        List<Chat> chatList = loadList(cursor);
        int first = 0;
        if (!chatList.isEmpty()) {
            return chatList.get(first);
        }
        return null;
    }

    public Chat getTeamChat(int teamId) {
        String[] args = { String.valueOf(teamId) };
        String query = SQLiteQueryBuilder.buildQueryString(true, tableName, null, COL_TEAM_ID + "=?", null, null, null, null);
        Cursor cursor = MemberDAO.getInstance().executeRawQueryForCursor(query, args);

        List<Chat> chatList = loadList(cursor);
        int first = 0;
        if (!chatList.isEmpty()) {
            return chatList.get(first);
        }
        return null;
    }
}
