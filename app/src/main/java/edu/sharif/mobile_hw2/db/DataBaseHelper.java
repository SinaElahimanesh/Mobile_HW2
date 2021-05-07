package edu.sharif.mobile_hw2.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import edu.sharif.mobile_hw2.Bookmark;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "Locations.db";
    public SQLiteDatabase db;
    public SQLiteDatabase db2;

    public static final String BOOKMARK_TABLE = "BOOKMARK_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_BOOKMARK_NAME = "NAME";
    public static final String COLUMN_BOOKMARK_LONG = "LONG";
    public static final String COLUMN_BOOKMARK_LAT = "LAT";
    private static final String TAG = "IN HELPER DB";
    private ExecutorService executorService;

    public DataBaseHelper(@Nullable Context context, ExecutorService executorService) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.executorService = executorService;
        System.out.println("jj");
        db = this.getWritableDatabase();
        db2 = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

//        String createTableStatement = "CREATE TABLE " + BOOKMARK_TABLE + " (" + COLUMN_BOOKMARK_NAME + " TEXT, "
//                + COLUMN_BOOKMARK_LONG + " REAL, " + COLUMN_BOOKMARK_LAT + " REAL)";

        System.out.println("doneeee");
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                sqLiteDatabase.execSQL(DataBaseContract.a);
                System.out.println("hrrr");
            }
        });
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DataBaseContract.SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean addBookmark(Bookmark bookmark) {
//        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_BOOKMARK_NAME, bookmark.getName());
        cv.put(COLUMN_BOOKMARK_LONG, bookmark.getLatLong());
        cv.put(COLUMN_BOOKMARK_LAT, bookmark.getLatLat());
        final long[] insert = new long[1];

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                insert[0] = db.insert(BOOKMARK_TABLE, null, cv);
//                db.close();
            }
        });

        return insert[0] != -1;
    }

    public void deleteBookmark(Bookmark bookmark) {
//        SQLiteDatabase db = this.getWritableDatabase();
        CharSequence name = bookmark.getName();
        String query = "DELETE FROM " + BOOKMARK_TABLE + " WHERE " + COLUMN_BOOKMARK_NAME + " = '" + String.valueOf(name) + "'";
        Log.i(TAG, query);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                db.delete(BOOKMARK_TABLE, COLUMN_BOOKMARK_NAME + " = '" + String.valueOf(name) + "'", null);
//                db.close();
            }
        });
    }

    public void getBookmarks() {
//        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + BOOKMARK_TABLE;
        System.out.println("hhhh");
        executorService.submit(new GettingBookmarksRunnable(db2, query));
    }

    public void deleteAllBookmarks() {
//        SQLiteDatabase db = this.getWritableDatabase();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                db.delete(BOOKMARK_TABLE, null, null);
                Bookmark.deleteBookmarks();
//                db.close();
            }
        });
    }

    static private class GettingBookmarksRunnable implements Runnable {
        SQLiteDatabase db;
        String query;

        private GettingBookmarksRunnable(SQLiteDatabase db, String query) {
            this.db = db;
            this.query = query;
        }

        @Override
        public void run() {
            List<Bookmark> returnedList = new ArrayList<>();
            System.out.println("readdd");
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(1);
                    double latLong = cursor.getDouble(2);
                    double latLat = cursor.getDouble(3);
                    Bookmark bookmark = new Bookmark(name, latLong, latLat);
                    returnedList.add(bookmark);
                } while (cursor.moveToNext());
                Bookmark.addAllBookmarks(returnedList);
                System.out.println("Bookmark.getBookmarks().size()");
            }
//            cursor.close();
//            db.close();
        }

    }

//    void addBookmark(Bookmark bookmark) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_BOOKMARK_NAME, bookmark.getName()); // Contact Name
//        values.put(COLUMN_BOOKMARK_LAT, bookmark.getLatLat()); // Contact Name
//        values.put(COLUMN_BOOKMARK_LONG, bookmark.getLatLong()); // Contact Phone
//
//        // Inserting Row
//        db.insert(BOOKMARK_TABLE, null, values);
//        //2nd argument is String containing nullColumnHack
//        db.close(); // Closing database connection
//    }

}
