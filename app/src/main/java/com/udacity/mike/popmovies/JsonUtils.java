package com.udacity.mike.popmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

    private static final String KEY_ORIG_TITLE = "title";
    private static final String KEY_IMAGE = "poster_path";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_RATING = "vote_average";
    private static final String KEY_RELEASE_DATE = "release_date";
    private static final String KEY_RESULTS = "results";
    private static final String KEY_ID = "id";

    private static final String KEY_NAME = "name";
    private static final String KEY_KEY = "key";
    private static final String KEY_SITE = "site";
    private static final String KEY_SIZE = "size";
    private static final String KEY_TYPE = "type";

    private static final String KEY_AUTHOR = "author";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_URL = "url";

    public static Review parseReviewJsonResults(JSONObject results){
        try{
            String id = results.getString(KEY_ID);
            String author = results.getString(KEY_AUTHOR);
            String content = results.getString(KEY_CONTENT);
            String url = results.getString(KEY_URL);
            return new Review(id,author,content,url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Trailer parseTrailerJsonResult(JSONObject results){
        try {
            String id = results.getString(KEY_ID);
            String key = results.getString(KEY_KEY);
            String name = results.getString(KEY_NAME);
            String site = results.getString(KEY_SITE);
            int size = results.getInt(KEY_SIZE);
            String type = results.getString(KEY_TYPE);
            return new Trailer(id,key,name,site,size,type);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Movie parseMovieJsonResult(JSONObject results){
            try {
                //JSONObject results = new JSONObject(json);
                String title = results.getString(KEY_ORIG_TITLE);
                //String image = results.getString(KEY_IMAGE);
                String image = results.optString(KEY_IMAGE,"no image");
                String overview = results.getString(KEY_OVERVIEW);
                Double rating = results.getDouble(KEY_RATING);
                String releaseDate = results.getString(KEY_RELEASE_DATE);
                int id = results.getInt(KEY_ID);
                return new Movie(id,title,image,overview,rating,releaseDate);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
    }

    public static JSONArray createJSONarray(String json){
        try {
            JSONObject response = new JSONObject(json);
            JSONArray resultsArray = response.getJSONArray(KEY_RESULTS);
            return resultsArray;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject parseJsonArray(JSONArray jsonArray,int position){
        try {
            JSONObject result = jsonArray.getJSONObject(position);
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
