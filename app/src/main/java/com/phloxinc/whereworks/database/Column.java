package com.phloxinc.whereworks.database;

public class Column {
    private String name;
    private String type;
    private String isNullable;

    public Column(String name, String type, String isNullable) {
        this.name = name;
        this.type = type;
        this.isNullable = isNullable;
    }

    public String getIsNullable() {
        return isNullable;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static String TYPE_INTEGER = "INTEGER";
    public static String TYPE_TEXT = "TEXT";
    public static String TYPE_BLOB = "BLOB";
    public static String TYPE_TIMESTAMP = "TIMESTAMP";

    public static String NULLABLE_NULL = "NULL";
    public static String NULLABLE_NOT_NULL = "NOT NULL";

}
