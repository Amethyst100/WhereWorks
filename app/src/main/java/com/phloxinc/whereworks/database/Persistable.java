package com.phloxinc.whereworks.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public interface Persistable {

    String getTableName() throws SQLException;

    String[] getColumnNames() throws SQLException;

    String getCreateQuery() throws SQLException;

    void onCreate(SQLiteDatabase database) throws SQLException;

    void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) throws SQLException;

    ContentValues getContentValues();

    ArrayList<?> loadList(Cursor queryCursor);

    long getID();

}