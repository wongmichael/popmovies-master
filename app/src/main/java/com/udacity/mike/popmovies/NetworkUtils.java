package com.udacity.mike.popmovies;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils { //based on feedback-class will never be extended so mark final...

    final static String HTTP="http://";
    final static String HTTPS="https://";
    final static String TMDB_BASE_URL_IMAGE="image.tmdb.org/t/p";
    final static String SIZE_W185 = "w185";
    final static String SIZE_500 = "w500";
    //"w92", "w154", "w185", "w342", "w500", "w780", or "original"
    final static String TMDB_BASE_URL="api.themoviedb.org/3";
    final static String TMDB_PATH_QUERY_POPULAR=
            //"api.themoviedb.org/3/movie/popular"
            //"api.themoviedb.org/3/discover/movie"
            "/discover/movie"
            ;
    final static String YOUTUBE_BASE_URL="www.youtube.com/watch?v=";

    final static String TMDB_PATH_MOVIE="/movie";
    final static String TMDB_PATH_VIDEOS="/videos";
    //http://api.themoviedb.org/3/movie/popular?api_key=[YOUR_API_KEY]
    final static String API_KEY= String.valueOf(R.string.apikey)
            //""
            ;
    final static String PARAM_API="api_key";

    final static String PARAM_SORT="sort_by";
    final static String asc = "asc";
    final static String desc = "desc";
    final static String popularity = "popularity";
    final static String vote_average = "vote_average";
    static String sortBy = "popularity.desc";

    public static URL buildUrl(int movieDetail,String movieId) {
        Uri builtUri = null;
        switch (movieDetail) {
            case R.string.query_movies:
                builtUri = Uri.parse(HTTP + TMDB_BASE_URL + TMDB_PATH_QUERY_POPULAR)
                    .buildUpon()
                    .appendQueryParameter(PARAM_API, API_KEY)
                    .appendQueryParameter(PARAM_SORT, sortBy)
                    .build();
                break;
            case R.string.query_trailers:
                builtUri = Uri.parse(HTTP + TMDB_BASE_URL + TMDB_PATH_MOVIE+"/"+ movieId +TMDB_PATH_VIDEOS)
                        .buildUpon()
                        .appendQueryParameter(PARAM_API,API_KEY)
                        .build();
                break;
        }

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d("built-url",url.toString());
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput){
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
