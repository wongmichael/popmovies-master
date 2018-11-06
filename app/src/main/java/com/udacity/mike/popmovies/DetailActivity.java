package com.udacity.mike.popmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.mike.popmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.udacity.mike.popmovies.MainActivity.showFavs;

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerClickListener, ReviewAdapter.ReviewClickListener {
    public static final String EXTRA_POS = "extra_position";
    public static final String EXTRA_MOVID = "extra_movid";
    private static final int DEFAULT_POS = -1;
    @BindView(R.id.tv_movie_title)
    TextView titleTv;
    @BindView(R.id.iv_movie_image)
    ImageView imageIv;
    @BindView(R.id.tv_overview) TextView overviewTv;
    @BindView(R.id.tv_rating) TextView ratingTv;
    @BindView(R.id.tv_release_date) TextView releaseDateTv;

    @BindView(R.id.butt_fav)
    Button favButton;

    @BindView(R.id.rv_trailers) RecyclerView trailerRv;
    @BindView(R.id.rv_reviews) RecyclerView reviewRv;

    @BindView(R.id.tv_review_label) TextView reviewLabelTv;
    @BindView(R.id.tv_trailer_label) TextView trailerLabelTv;

    public static JSONArray trailerResultsArray;
    public static JSONArray reviewResultsArray;

    private TrailerAdapter tAdapter;
    private ReviewAdapter rAdapter;

    private Cursor getMovie(String movId){
        //Log.d("get favs","get favs");
        return MainActivity.mDb.query(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID+"=?",
                new String[]{movId},
                null,
                null,
                null
        );
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent==null){
            closeOnError();
            return;
        }
        final Movie m;
        if(showFavs){
            String movieId = intent.getStringExtra(EXTRA_MOVID);
            Cursor mCursor = getMovie(movieId);
            mCursor.moveToFirst();
            String title = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE));
            String image = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
            String overview = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));
            Double rating = Double.valueOf(mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING)));
            String releaseDate = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
            m = new Movie(Integer.parseInt(movieId),title,image,overview,rating,releaseDate);
        } else {
            //necessary?
            int position = intent.getIntExtra(EXTRA_POS, DEFAULT_POS);
            if (position == DEFAULT_POS) {
                closeOnError();
                return;
            }
            JSONObject j = JsonUtils.parseJsonArray(MainActivity.resultsArray, position);
            m = JsonUtils.parseMovieJsonResult(j);
        }
        titleTv.append(m.getOrigTitle());
        imageIv.setContentDescription(m.getOrigTitle());
        if (MovieAdapter.showPics) {
            Picasso.with(this)
                    .load(m.getImage(NetworkUtils.SIZE_500))
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(imageIv);
        }
        overviewTv.append(m.getOverview());
        releaseDateTv.append(m.getReleaseDate());
        ratingTv.append(String.valueOf(m.getRating()));

        setTitle(m.getOrigTitle());
        Log.d("m.getId~~~",String.valueOf(m.getId()));
        new CheckFavTask().execute(String.valueOf(m.getId()));

        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d("favbutton-text", String.valueOf(favButton.getText()));
                //Log.d("text-compare", String.valueOf(favButton.getText().equals(getString(R.string.favorite))));
                if (favButton.getText().equals(getString(R.string.favorite)) && addFavorite(m)!=null){
                //(addFavorite(m)!=-1){ //-1 is db insert error
                    favButton.setText(R.string.unfavorite);
                } else{
                    removeFavorite(m);
                    favButton.setText(R.string.favorite);
                }
                //MainActivity.fAdapter.swapCursor(MainActivity.getFavorites());
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        trailerRv.setLayoutManager(layoutManager);
        trailerRv.setHasFixedSize(true);

        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this);
        reviewRv.setLayoutManager(reviewLayoutManager);
        reviewRv.setHasFixedSize(true);

        getTrailers(m.getId());
        getReviews(m.getId());
    }

    private Uri addFavorite(Movie m){
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,m.getId());
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,m.getOrigTitle());
        cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW,m.getOverview());
        cv.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH,m.getImage());
        cv.put(MovieContract.MovieEntry.COLUMN_RATING,m.getRating());
        cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,m.getReleaseDate());
        //return MainActivity.mDb.insert(MovieContract.MovieEntry.TABLE_NAME,null,cv);
        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,cv);
        if(uri!=null){
            Toast.makeText(getBaseContext(),uri.toString(),Toast.LENGTH_LONG).show();
        }
        return uri;
    }

    private long removeFavorite(Movie m){
        return MainActivity.mDb.delete(MovieContract.MovieEntry.TABLE_NAME,MovieContract.MovieEntry.COLUMN_MOVIE_ID+"=?", new String[]{String.valueOf(m.getId())});
    }

    private void getReviews(int movieId) {
        URL searchUrl = NetworkUtils.buildUrl(R.string.query_reviews,String.valueOf(movieId));
        Log.d("review request",searchUrl.toString());
        new ReviewQueryTask().execute(searchUrl);
    }

    private void getTrailers(int movieId) {
        URL searchUrl = NetworkUtils.buildUrl(R.string.query_trailers,String.valueOf(movieId));
        Log.d("trailer request",searchUrl.toString());
        new QueryTask().execute(searchUrl);
    }

    private Boolean checkFav(int movieId){
        Log.d("checkfav","checkfav");
        Cursor cf = MainActivity.mDb.query(
                    MovieContract.MovieEntry.TABLE_NAME,
                new String[]{MovieContract.MovieEntry.COLUMN_MOVIE_ID},
                MovieContract.MovieEntry.COLUMN_MOVIE_ID+"=?",
                new String[]{String.valueOf(movieId)},
                    null,
                    null,
                    null
            );
        if (!cf.moveToFirst()){
            //return null;
            return false;
        } else{
            return cf.getCount()>0;
            //return cf.getString(0);
        }
    }
    private class CheckFavTask extends AsyncTask<String,Void,Boolean>{
        @Override
        protected Boolean doInBackground(String... ids) {
/*            Boolean b = checkFav(Integer.parseInt(ids[0])).equals(ids[0]);
            Log.d("checkfav",checkFav(Integer.parseInt(ids[0]))+"==?"+ids[0]);
            Log.d("checkfav2", String.valueOf(b));
            return (checkFav(Integer.parseInt(ids[0])).equals(ids[0]));*/
            return checkFav(Integer.parseInt(ids[0]));
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                favButton.setText(R.string.unfavorite);
            } else {
                favButton.setText(R.string.favorite);
            }
        }
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this,R.string.error_message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        launchTrailer(clickedItemIndex);
    }

    private void launchTrailer(int clickedItemIndex) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        JSONObject j = JsonUtils.parseJsonArray(DetailActivity.trailerResultsArray,clickedItemIndex);
        Trailer t = JsonUtils.parseTrailerJsonResult(j);
        if (t.getSite().equalsIgnoreCase(getString(R.string.youtube))){
            intent.setData(Uri.parse(NetworkUtils.HTTP+NetworkUtils.YOUTUBE_BASE_URL+t.getKey()));
            startActivity(intent);
        }
    }

    @Override
    public void onReviewItemClick(int clickedItemIndex) {
        launchReview(clickedItemIndex);
    }

    private void launchReview(int clickedItemIndex) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        JSONObject j = JsonUtils.parseJsonArray(DetailActivity.reviewResultsArray,clickedItemIndex);
        Review r = JsonUtils.parseReviewJsonResults(j);
        intent.setData(Uri.parse(r.getUrl()));
        startActivity(intent);
    }

    private class QueryTask extends AsyncTask<URL,Void,String> {
        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String searchResults = null;
            try{
                searchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e){
                e.printStackTrace();
            }
            return searchResults;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null && !s.equals("")){
                //Toast.makeText(this,s,Toast.LENGTH_LONG).show();
                Log.d("trailer search results",s);
                trailerResultsArray = JsonUtils.createJSONarray(s);
                int num_of_trailer_items = trailerResultsArray.length();
                tAdapter = new TrailerAdapter(num_of_trailer_items, DetailActivity.this);
                trailerRv.setAdapter(tAdapter);
                if(num_of_trailer_items==0){
                    trailerLabelTv.setText(R.string.no_trailers);
                } else {
                    trailerLabelTv.setText(R.string.query_trailers);
                }

            } else {
                Log.d("no trailer data found~~","no trailer data found!");
                Toast.makeText(getApplicationContext(),R.string.error_message,Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ReviewQueryTask extends AsyncTask<URL,Void,String>{
        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String searchResults = null;
            try{
                searchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e){
                e.printStackTrace();
            }
            return searchResults;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null && !s.equals("")){
                //Toast.makeText(this,s,Toast.LENGTH_LONG).show();
                Log.d("review search results",s);
                reviewResultsArray = JsonUtils.createJSONarray(s);
                int num_of_review_items = reviewResultsArray.length();
                rAdapter = new ReviewAdapter(num_of_review_items, DetailActivity.this);
                reviewRv.setAdapter(rAdapter);
                if (num_of_review_items==0){
                    reviewLabelTv.setText(R.string.no_reviews);
                } else{
                    reviewLabelTv.setText(R.string.query_reviews);
                }

            } else {
                Log.d("no review data found~~","no review data found!");
                Toast.makeText(getApplicationContext(),R.string.error_message,Toast.LENGTH_SHORT).show();
            }
        }

    }
}
