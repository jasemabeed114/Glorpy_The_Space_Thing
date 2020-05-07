package com.bronzeswordstudios.glorpythespacething;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "score.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + DataHolder.DataEntry.TABLE_NAME + " (" +
                DataHolder.DataEntry._ID + " INTEGER PRIMARY KEY, " +
                DataHolder.DataEntry.HIGHEST_SCORE + " INTEGER NOT NULL); ";
        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL_DELETE_ENTRY = "DROP TABLE IF EXISTS " + DataHolder.DataEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_ENTRY);
        onCreate(db);
    }
}
