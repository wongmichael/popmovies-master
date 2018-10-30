package com.udacity.mike.popmovies.data;

import android.provider.BaseColumns;

public class MovieContract {
    public static final class MovieEntry implements BaseColumns{
        public static final String TABLE_NAME = "favorite_movies";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_RELEASE_DATE = "release_dt";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_OVERVIEW = "overview";
    }
}
