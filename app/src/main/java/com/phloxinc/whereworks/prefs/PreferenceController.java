package com.phloxinc.whereworks.prefs;

import android.content.ContentValues;
import android.database.Cursor;

import com.phloxinc.whereworks.database.BaseDAO;
import com.phloxinc.whereworks.database.BaseEntity;
import com.phloxinc.whereworks.database.Column;

import java.util.ArrayList;
import java.util.HashMap;

public class PreferenceController {

    private static PreferenceController instance;

    private HashMap<String, Preference> preferenceMap = new HashMap<>();

    static PreferenceController getInstance() {
        if (instance == null) {
            instance = new PreferenceController();
        }
        return instance;
    }

    private PreferenceController () {
        @SuppressWarnings("unchecked")
        ArrayList<Preference> preferenceList = (ArrayList<Preference>) PreferenceDAO.getInstance().fetch(new Preference());
        for (Preference preference : preferenceList) {
            preferenceMap.put(preference.key, preference);
        }
    }

    public static class PreferenceDAO extends BaseDAO {
        private static PreferenceDAO instance;
        private static String COL_KEY = "key";
        private static String COL_VALUE = "value";
        private static String COL_TYPE = "type";

        private static String tableName = "preference";
        private static Column[] columns = {
                new Column(COL_KEY, Column.TYPE_TEXT, Column.NULLABLE_NOT_NULL),
                new Column(COL_VALUE, Column.TYPE_TEXT, Column.NULLABLE_NULL),
                new Column(COL_TYPE, Column.TYPE_BLOB, Column.NULLABLE_NOT_NULL)
        };

        @SuppressWarnings("all")
        public PreferenceDAO() {
            super(tableName, columns);
        }

        public synchronized static PreferenceDAO getInstance() {
            if (instance == null) {
                instance = new PreferenceDAO();
            }
            return instance;
        }

        @Override
        protected BaseEntity cursorToEntity(Cursor cursor) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
            String key = cursor.getString(cursor.getColumnIndexOrThrow(COL_KEY));
            String value = cursor.getString(cursor.getColumnIndexOrThrow(COL_VALUE));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE));
            return new Preference(id, key, value, type);
        }

        @Override
        public ContentValues getContentValues() {
            Preference preference = (Preference) entity;
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_KEY, preference.key);
            contentValues.put(COL_VALUE, preference.value);
            contentValues.put(COL_TYPE, preference.type);
            return contentValues;
        }

        @Override
        public ArrayList<?> loadList(Cursor queryCursor) {
            ArrayList<Preference> preferenceList = new ArrayList<>();

            queryCursor.moveToFirst();
            while (!queryCursor.isAfterLast()) {
                Preference preference = (Preference) cursorToEntity(queryCursor);
                preferenceList.add(preference);
                queryCursor.moveToNext();
            }

            return preferenceList;
        }
    }

    void putInt(String key, int value) {
        Preference preference;
        if (preferenceMap.containsKey(key)) {
            preference = preferenceMap.get(key);
            preference.value = String.valueOf(value);
            PreferenceDAO.getInstance().update(preference);
        } else {
            preference = new Preference(key, value);
            PreferenceDAO.getInstance().insert(preference);
            preferenceMap.put(key, preference);
        }
    }

    void putFloat(String key, float value) {
        Preference preference;
        if (preferenceMap.containsKey(key)) {
            preference = preferenceMap.get(key);
            preference.value = String.valueOf(value);
            PreferenceDAO.getInstance().update(preference);
        } else {
            preference = new Preference(key, value);
            PreferenceDAO.getInstance().insert(preference);
            preferenceMap.put(key, preference);
        }
    }

    void putLong(String key, long value) {
        Preference preference;
        if (preferenceMap.containsKey(key)) {
            preference = preferenceMap.get(key);
            preference.value = String.valueOf(value);
            PreferenceDAO.getInstance().update(preference);
        } else {
            preference = new Preference(key, value);
            PreferenceDAO.getInstance().insert(preference);
            preferenceMap.put(key, preference);
        }
    }

    void putBoolean(String key, boolean value) {
        Preference preference;
        if (preferenceMap.containsKey(key)) {
            preference = preferenceMap.get(key);
            preference.value = String.valueOf(value);
            PreferenceDAO.getInstance().update(preference);
        } else {
            preference = new Preference(key, value);
            PreferenceDAO.getInstance().insert(preference);
            preferenceMap.put(key, preference);
        }
    }

    void putString(String key, String value) {
        Preference preference;
        if (preferenceMap.containsKey(key)) {
            preference = preferenceMap.get(key);
            preference.value = value;
            PreferenceDAO.getInstance().update(preference);
        } else {
            preference = new Preference(key, value);
            PreferenceDAO.getInstance().insert(preference);
            preferenceMap.put(key, preference);
        }
    }

    int getInt(String key, int defValue) {
        int returnValue = defValue;
        if (preferenceMap.containsKey(key)) {
            Preference preference = preferenceMap.get(key);
            if ("int".equals(preference.type)) {
                returnValue = Integer.parseInt(preference.value);
            }
        }
        return returnValue;
    }

    float getFloat(String key, float defValue) {
        float returnValue = defValue;
        if (preferenceMap.containsKey(key)) {
            Preference preference = preferenceMap.get(key);
            if ("float".equals(preference.type)) {
                returnValue = Integer.parseInt(preference.value);
            }
        }
        return returnValue;
    }

    long getLong(String key, long defValue) {
        long returnValue = defValue;
        if (preferenceMap.containsKey(key)) {
            Preference preference = preferenceMap.get(key);
            if ("long".equals(preference.type)) {
                returnValue = Integer.parseInt(preference.value);
            }
        }
        return returnValue;
    }

    boolean getBoolean(String key, boolean defValue) {
        boolean returnValue = defValue;
        if (preferenceMap.containsKey(key)) {
            Preference preference = preferenceMap.get(key);
            if ("boolean".equals(preference.type)) {
                returnValue = Boolean.parseBoolean(preference.value);
            }
        }
        return returnValue;
    }

    String getString(String key, String defValue) {
        String returnValue = defValue;
        if (preferenceMap.containsKey(key)) {
            Preference preference = preferenceMap.get(key);
            if ("String".equals(preference.type)) {
                returnValue = preference.value;
            }
        }
        return returnValue;
    }

    boolean isExists(String key) {
        return preferenceMap.containsKey(key);
    }
}

final class Preference extends BaseEntity {

    String key;
    String value;
    String type;

    Preference() {
    }

    Preference(String key, boolean value) {
        this(key, String.valueOf(value), "boolean");
    }

    Preference(String key, int value) {
        this(key, String.valueOf(value), "int");
    }

    Preference(String key, float value) {
        this(key, String.valueOf(value), "float");
    }

    Preference(String key, long value) {
        this(key, String.valueOf(value), "long");
    }

    Preference(String key, String value) {
        this(key, value, "String");
    }

    private Preference(String key, String value, String type) {
        this.key = key;
        this.value = value;
        this.type = type;
    }

    Preference(long _id, String key, String value, String type) {
        this.setId(_id);
        this.key = key;
        this.value = value;
        this.type = type;
    }
}
