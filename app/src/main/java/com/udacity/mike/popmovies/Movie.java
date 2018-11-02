package com.udacity.mike.popmovies;

public class Movie {

    private String origTitle;
    private String image;
    private String overview;
    //private String rating;
    private double rating;
    private String releaseDate;
    private int id;

    public Movie(){

    }

    public Movie(int id, String origTitle, String image, String overview, double rating, String releaseDate){
        this.id = id;
        this.origTitle = origTitle;
        this.image=image;
        this.overview=overview;
        this.rating=rating;
        this.releaseDate=releaseDate;
    }

    public String getOrigTitle(){return origTitle;}
    public String getImage(String size){
        String fullpath = NetworkUtils.HTTP+NetworkUtils.TMDB_BASE_URL_IMAGE+"/"+size +image;
        //Log.d("image url",fullpath);
        return fullpath;
        //return image;
    }
    public String getImage(){
        return image;
    }
    public String getOverview(){return overview;}
    public String getReleaseDate(){return releaseDate;}
    public double getRating(){return rating;}

    public int getId() {
        return id;
    }
}
