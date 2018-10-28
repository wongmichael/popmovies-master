package com.udacity.mike.popmovies;

import android.util.Log;

/**
 * Created by mike on 10/28/2018.
 */

public class Review {
    private String id;
    private String author;
    private String content;
    private String url;

    public Review(String id, String author, String content, String url){
        this.id=id;
        this.author=author;
        this.content=content;
        this.url=url;
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        //Log.d("author",author);
        return author;
    }

    public String getContent() {
        //Log.d("content",content);
        return content;
    }

    public String getUrl() {
        return url;
    }
}
