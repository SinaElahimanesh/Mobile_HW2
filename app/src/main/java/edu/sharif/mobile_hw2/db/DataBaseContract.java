package edu.sharif.mobile_hw2.db;

import android.provider.BaseColumns;

public final class DataBaseContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DataBaseContract() {
    }

    public static class DataBaseEntry implements BaseColumns {
        public static final String TABLE_NAME = "BOOKMARK_TABLE";
        public static final String PLACE_NAME = "NAME";
        public static final String PLACE_LATITUDE = "LAT";
        public static final String PLACE_LONGITUDE = "LONG";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DataBaseEntry.TABLE_NAME + " (" +
                    DataBaseEntry._ID + " INTEGER PRIMARY KEY," +
                    DataBaseEntry.PLACE_NAME + " TEXT," +
                    DataBaseEntry.PLACE_LATITUDE + " REAL," +
                    DataBaseEntry.PLACE_LONGITUDE + " REAL)";

    final static String a = "CREATE TABLE " + DataBaseEntry.TABLE_NAME + " (" + DataBaseEntry._ID + " INTEGER PRIMARY KEY, "
            + DataBaseEntry.PLACE_NAME + " TEXT, " +
            DataBaseEntry.PLACE_LATITUDE + " REAL, " +
            DataBaseEntry.PLACE_LONGITUDE +  " REAL)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DataBaseEntry.TABLE_NAME;
}
