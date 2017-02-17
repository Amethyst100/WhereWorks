package com.phloxinc.whereworks.prefs;

public class Prefs {
    private static PreferenceController _provider = PreferenceController.getInstance();

    public static void putInt(String key, int value) {
        _provider.putInt(key, value);
    }

    public static void putFloat(String key, float value) {
        _provider.putFloat(key, value);
    }

    public static void putLong(String key, long value) {
        _provider.putLong(key, value);
    }

    public static void putBoolean(String key, boolean value) {
        _provider.putBoolean(key, value);
    }

    public static void putString(String key, String value) {
        _provider.putString(key, value);
    }

    public static int getInt(String key, int defValue) {
        return _provider.getInt(key, defValue);
    }

    public static float getFloat(String key, float defValue) {
        return _provider.getFloat(key, defValue);
    }

    public static long getLong(String key, long defValue) {
        return _provider.getLong(key, defValue);
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return _provider.getBoolean(key, defValue);
    }

    public static String getString(String key, String defValue) {
        return _provider.getString(key, defValue);
    }

    public static boolean isExists(String key) {
        return _provider.isExists(key);
    }
}
