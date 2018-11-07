package com.udacity.mike.popmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "popmovie.db";
    private static final int DATABASE_VERSION = 1;

    private static MovieDbHelper sInstance;
/*
    public MovieDbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }*/

    //private to prevent direct instantiation; use call to static method getInstanace instead
    private MovieDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    public static synchronized MovieDbHelper getInstance(Context context) {
        // Use the application context, which will ensure that you don't accidentally leak an Activity's context http://bit.ly/6LRzfx
        if(sInstance==null){
            sInstance=new MovieDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE "+
                MovieContract.MovieEntry.TABLE_NAME+
                " ("+MovieContract.MovieEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                MovieContract.MovieEntry.COLUMN_MOVIE_ID+" TEXT UNIQUE NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE+" TEXT, "+
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE+" TEXT, "+
                MovieContract.MovieEntry.COLUMN_POSTER_PATH+" TEXT, "+
                MovieContract.MovieEntry.COLUMN_RATING+" REAL, "+
                MovieContract.MovieEntry.COLUMN_OVERVIEW+" TEXT "+
                ");";
        Log.d("SQL create",SQL_CREATE_MOVIE_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+MovieContract.MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("ALTER TABLE "+MovieContract.MovieEntry.TABLE_NAME+
                " RENAME TO "+MovieContract.MovieEntry.TABLE_NAME+i);
        onCreate(sqLiteDatabase);
    }
}
