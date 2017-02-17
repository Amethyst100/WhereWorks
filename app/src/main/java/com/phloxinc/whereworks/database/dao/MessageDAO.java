package com.phloxinc.whereworks.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;

import com.phloxinc.whereworks.bo.Message;
import com.phloxinc.whereworks.database.BaseDAO;
import com.phloxinc.whereworks.database.BaseEntity;
import com.phloxinc.whereworks.database.Column;

import java.util.ArrayList;
import java.util.List;

public class MessageDAO extends BaseDAO {

    private static MessageDAO instance;
    private static String COL_TEXT = "messageText";
    private static String COL_SENDER_ID = "senderId";
    private static String COL_RECIPIENT_ID = "recipientId";
    private static String COL_CHAT_ID = "chatId";
    private static String COL_TIMESTAMP = "timestamp";

    private static String tableName = "message";
    private static Column[] columns = {
            new Column(COL_TEXT, Column.TYPE_TEXT, Column.NULLABLE_NULL),
            new Column(COL_SENDER_ID, Column.TYPE_INTEGER, Column.NULLABLE_NULL),
            new Column(COL_RECIPIENT_ID, Column.TYPE_INTEGER, Column.NULLABLE_NULL),
            new Column(COL_CHAT_ID, Column.TYPE_INTEGER, Column.NULLABLE_NULL),
            new Column(COL_TIMESTAMP, Column.TYPE_TIMESTAMP, Column.NULLABLE_NULL)
    };

    @SuppressWarnings("all")
    public MessageDAO() {
        super(tableName, columns);
    }

    public static MessageDAO getInstance() {
        if (instance == null) {
            instance = new MessageDAO();
        }
        return instance;
    }

    @Override
    protected BaseEntity cursorToEntity(Cursor cursor) {
        Message message = new Message();

        message.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
        message.setText(cursor.getString(cursor.getColumnIndexOrThrow(COL_TEXT)));
        message.setSenderId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_SENDER_ID)));
        message.setRecipientId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_RECIPIENT_ID)));
        message.setChatId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_CHAT_ID)));
        message.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIMESTAMP)));

        return message;
    }

    @Override
    public ContentValues getContentValues() {
        Message message = (Message) entity;
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_TEXT, message.getText());
        contentValues.put(COL_SENDER_ID, message.getSenderId());
        contentValues.put(COL_RECIPIENT_ID, message.getRecipientId());
        contentValues.put(COL_CHAT_ID, message.getChatId());
        contentValues.put(COL_TIMESTAMP, message.getTimestamp());

        return contentValues;
    }

    @Override
    public ArrayList<Message> loadList(Cursor queryCursor) {
        ArrayList<Message> messageList = new ArrayList<>();

        queryCursor.moveToFirst();
        while (!queryCursor.isAfterLast()) {
            Message message = (Message) cursorToEntity(queryCursor);
            messageList.add(message);
            queryCursor.moveToNext();
        }
        queryCursor.close();

        return messageList;
    }

    public ArrayList<Message> getAllMessages() {
        String query = SQLiteQueryBuilder.buildQueryString(true, tableName, null, null, null, null, null, null);
        Cursor cursor = MemberDAO.getInstance().executeRawQueryForCursor(query, null);
        return loadList(cursor);
    }

    public Message getMessage(long messageId) {
        String[] args = { String.valueOf(messageId) };
        String query = SQLiteQueryBuilder.buildQueryString(true, tableName, null, COL_ID + "=?", null, null, null, null);
        Cursor cursor = MemberDAO.getInstance().executeRawQueryForCursor(query, args);

        List<Message> messageList = loadList(cursor);
        int first = 0;
        if (!messageList.isEmpty()) {
            return messageList.get(first);
        }
        return null;
    }

    public List<Message> getMessages(int chatId) {
        String[] args = { String.valueOf(chatId) };
        String query = SQLiteQueryBuilder.buildQueryString(true, tableName, null, COL_CHAT_ID + "=?", null, null, null, null);
        Cursor cursor = MemberDAO.getInstance().executeRawQueryForCursor(query, args);
        return loadList(cursor);
    }
}
