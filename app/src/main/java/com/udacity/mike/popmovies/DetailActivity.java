package com.udacity.mike.popmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerClickListener {
    public static final String EXTRA_POS = "extra_position";
    private static final int DEFAULT_POS = -1;
    @BindView(R.id.tv_movie_title)
    TextView titleTv;
    @BindView(R.id.iv_movie_image)
    ImageView imageIv;
    @BindView(R.id.tv_overview) TextView overviewTv;
    @BindView(R.id.tv_rating) TextView ratingTv;
    @BindView(R.id.tv_release_date) TextView releaseDateTv;

    @BindView(R.id.rv_trailers) RecyclerView trailerRv;

    public static JSONArray resultsArray;

    private TrailerAdapter tAdapter;

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

        //necessary?
        int position = intent.getIntExtra(EXTRA_POS,DEFAULT_POS);
        if (position==DEFAULT_POS){
            closeOnError(); return;
        }

        JSONObject j = JsonUtils.parseJsonArray(MainActivity.resultsArray,position);
        Movie m = JsonUtils.parseMovieJsonResult(j);
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

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        trailerRv.setLayoutManager(layoutManager);
        trailerRv.setHasFixedSize(true);

        getTrailers(m.getId());
    }

    private void getTrailers(int movieId) {
        URL searchUrl = NetworkUtils.buildUrl(R.string.query_trailers,String.valueOf(movieId));
        Log.d("trailer request",searchUrl.toString());
        new QueryTask().execute(searchUrl);
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
        JSONObject j = JsonUtils.parseJsonArray(DetailActivity.resultsArray,clickedItemIndex);
        Trailer t = JsonUtils.parseTrailerJsonResult(j);
        if (t.getSite().equalsIgnoreCase(getString(R.string.youtube))){
            intent.setData(Uri.parse(NetworkUtils.HTTP+NetworkUtils.YOUTUBE_BASE_URL+t.getKey()));
            startActivity(intent);
        }
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
                resultsArray = JsonUtils.createJSONarray(s);
                int num_of_trailer_items = resultsArray.length();
                tAdapter = new TrailerAdapter(num_of_trailer_items, DetailActivity.this);
                trailerRv.setAdapter(tAdapter);

            } else {
                Log.d("no trailer data found~~","no trailer data found!");
                Toast.makeText(getApplicationContext(),R.string.error_message,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
