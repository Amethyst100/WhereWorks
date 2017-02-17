package com.phloxinc.whereworks.database;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseManager extends SQLiteOpenHelper {

    private static final String TAG = DatabaseManager.class.getName().toUpperCase();
    private static final String KEY_ID = "_id";
    private static SQLiteDatabase database = null;
    private static DatabaseManager databaseManager = null;
    private static ArrayList<Class<? extends Persistable>> persistables = null;

    private DatabaseManager(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static String getDatabasePath() {
        return database.getPath();
    }

    public static void initialize(Context context, String databaseName, int databaseVersion, ArrayList<Class<? extends Persistable>> pList) {
        if (databaseManager == null) {
            if (pList != null && pList.size() > 0) {
                persistables = pList;
                databaseManager = new DatabaseManager(context, databaseName, null, databaseVersion);
                try {
                    Log.i(TAG, "Creating or opening the database [ " + databaseName + " ].");
                    database = databaseManager.getWritableDatabase();
                } catch (SQLiteException se) {
                    Log.e(TAG, "Cound not create and/or open the database [ " + databaseName + " ] that will be used for reading and writing.", se);
                }
            }
        } else {
            Log.i(TAG, "Database Manager Already Inialized");
        }
    }

    public static DatabaseManager getDatabaseManager() throws SQLiteException {
        if (databaseManager == null) {
            Log.e(TAG, "Database Manager not Initialized");
            throw new SQLiteException("Database Manager not Inialized");
        } else {
            Log.i(TAG, "Database Manager Already Initialized");
            return databaseManager;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "Trying to create database table if it isn't existed .");
        try {
            for (Class<? extends Persistable> persistable : persistables) {
                Persistable inst = persistable.newInstance();
                inst.onCreate(db);
            }
        } catch (SQLException | InstantiationException | IllegalAccessException se) {
            Log.e(TAG, "Cound not create the database table according to the SQL statement.", se);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        try {
            for (Class<? extends Persistable> persistable : persistables) {
                Persistable inst = persistable.newInstance();
                inst.onUpgrade(db, oldVersion, newVersion);
            }
        } catch (SQLException | InstantiationException | IllegalAccessException se) {
            Log.e(TAG, "Cound not drop the database table.", se);
        }
    }

    protected long delete(Persistable obj) {
        return database.delete(obj.getTableName(), KEY_ID + "=" + obj.getID(), null);
    }

    protected long update(Persistable obj) {
        return database.update(obj.getTableName(), obj.getContentValues(), KEY_ID + "=" + obj.getID(), null);
    }

    protected long insert(Persistable obj) {
        return database.insert(obj.getTableName(), null, obj.getContentValues());
    }

    protected Cursor executeRawQuery(String rawQuery, String[] args) {
        return database.rawQuery(rawQuery, args);
    }

    protected Cursor executeRawQuery(String rawQuery) {
        return database.rawQuery(rawQuery, null);
    }

    @SuppressWarnings({"rawtypes"})
    protected ArrayList fetch(Persistable obj) {
        Cursor cursor;
        String WHERE_CLAUSE = null;
        if (obj.getID() > 0) {
            WHERE_CLAUSE = KEY_ID + "=" + obj.getID();
        }
        cursor = database.query(obj.getTableName(), obj.getColumnNames(), WHERE_CLAUSE, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            ArrayList list = obj.loadList(cursor);
            cursor.close();
            return list;
        } else {
            throw new SQLiteException("Cursor is null fot fetch " + obj.getTableName());
        }
    }

}
