package com.phloxinc.whereworks.database;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public abstract class BaseDAO implements Persistable, EntityCRUD {
    private static final String TAG = BaseDAO.class.getName().toUpperCase();
    protected String COL_ID = "_id";
    public String tableName = null;
    public String[] columnsName = null;
    public Column[] columns = null;
    protected BaseEntity entity = null;

    protected BaseDAO(String tableName, Column[] columns) {
        this.tableName = tableName;
        this.columns = columns;
    }

    @Override
    public String getTableName() throws SQLException {
        return tableName;
    }

    @Override
    public String[] getColumnNames() throws SQLException {
        if (columnsName != null) {
            List<String> columnList = new ArrayList<>();
            for (Column column : columns) {
                columnList.add(column.getName());
            }
            columnsName = new String[columns.length];
            columnList.toArray(columnsName);
        }
        return columnsName;
    }

    @Override
    public String getCreateQuery() throws SQLException {
        StringBuilder stringBuilder = new StringBuilder("create table ");
        stringBuilder.append(this.tableName);
        stringBuilder.append("(");
        stringBuilder.append(COL_ID);
        stringBuilder.append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
        for (int i = 0; i < columns.length; i++) {
            stringBuilder.append(columns[i].getName());
            stringBuilder.append(" ");
            stringBuilder.append(columns[i].getType());
            stringBuilder.append(" ");
            stringBuilder.append(columns[i].getIsNullable());
            if (i < columns.length - 1) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    @Override
    public void onCreate(SQLiteDatabase database) throws SQLException {
        database.execSQL(getCreateQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) throws SQLException {
         database.execSQL("DROP TABLE IF EXISTS " + getTableName());
         onCreate(database);
    }

    @Override
    public void delete(BaseEntity entity) {
        this.entity = entity;
        DatabaseManager.getDatabaseManager().delete(this);
    }

    @Override
    public void update(BaseEntity entity) {
        this.entity = entity;
        DatabaseManager.getDatabaseManager().update(this);
    }

    @Override
    public void insert(BaseEntity entity) {
        if (entity.getId() > 0) {
            update(entity);
        } else {
            this.entity = entity;
            this.entity.setId(DatabaseManager.getDatabaseManager().insert(this));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public ArrayList<? extends BaseEntity> fetch(BaseEntity entity) {
        this.entity = entity;
        return DatabaseManager.getDatabaseManager().fetch(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ArrayList<? extends BaseEntity> fetchAll(Class<? extends BaseEntity> clazz) {
        try {
            this.entity = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return DatabaseManager.getDatabaseManager().fetch(this);
    }

    public Cursor executeRawQueryForCursor(String rawQuery, String[] args) {
        if (args == null) {
            return DatabaseManager.getDatabaseManager().executeRawQuery(rawQuery);
        } else {
            return DatabaseManager.getDatabaseManager().executeRawQuery(rawQuery, args);
        }
    }

    public ArrayList<? extends BaseEntity> executeRawQuery(String rawQuery) {
        Cursor cursor = DatabaseManager.getDatabaseManager().executeRawQuery(rawQuery);
        if (cursor != null) {
            cursor.moveToFirst();
            @SuppressWarnings("unchecked")
            ArrayList<? extends BaseEntity> list = (ArrayList<? extends BaseEntity>) loadList(cursor);
            // close it to avoid memory leak
            cursor.close();
            return list;

        } else {
            Log.i(TAG, "fetch All: queryCursor = null ");
            throw new SQLiteException("Cursor is null for fetch " + getTableName());
        }
    }

    @Override
    public long getID() {
        return this.entity.getId();
    }

    protected abstract BaseEntity cursorToEntity(Cursor cursor);

}
