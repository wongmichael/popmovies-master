package com.udacity.mike.popmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Network;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener {

    //private static final int NUM_LIST_ITEMS = 25;
    private int NUM_LIST_ITEMS = 5;
    private MovieAdapter mAdapter;
    private RecyclerView mList;
    private Toast mToast;
    private static final int gridLayoutSpanCount = 4;

    public static JSONArray resultsArray;
    /*    private static final String RESULTS_ARRAY_JSON = "results";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(RESULTS_ARRAY_JSON,resultsArray.toString());
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkUtils.API_KEY = getString(R.string.apikey);

        mList = (RecyclerView) findViewById(R.id.rv_movies);
        //LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this,gridLayoutSpanCount);
        mList.setLayoutManager(layoutManager);
        mList.setHasFixedSize(true);

/*        if (savedInstanceState!=null){
            try {
                resultsArray = new JSONArray(savedInstanceState.getString(RESULTS_ARRAY_JSON));
                NUM_LIST_ITEMS = resultsArray.length();
                mAdapter = new MovieAdapter(NUM_LIST_ITEMS,MainActivity.this);
                mList.setAdapter(mAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("savedInstanceStateNN","savedInstanceState!=null");
        } else{
            makeSearchQuery();
        }*/
        makeSearchQuery();

 /*       mAdapter = new MovieAdapter(NUM_LIST_ITEMS, this);
        mList.setAdapter(mAdapter);*/

    }

    private void makeSearchQuery(){
        URL searchUrl = NetworkUtils.buildUrl(R.string.query_movies,null);
        //Toast.makeText(this,searchUrl.toString(),Toast.LENGTH_LONG).show();
        Log.d("json request",searchUrl.toString());
        new QueryTask().execute(searchUrl);
    }

    public class QueryTask extends AsyncTask<URL,Void,String>{

        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String searchResults = null;
            try {
                searchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                //Toast.makeText(this,searchResults,Toast.LENGTH_LONG).show();
                //Log.d("search results",searchResults);
            } catch (IOException e){
                e.printStackTrace();
            }
            return searchResults;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null && !s.equals("")){
                //Toast.makeText(this,s,Toast.LENGTH_LONG).show();
                Log.d("search results",s);
                resultsArray = JsonUtils.createJSONarray(s);
                NUM_LIST_ITEMS = resultsArray.length();
                mAdapter = new MovieAdapter(NUM_LIST_ITEMS,MainActivity.this);
                mList.setAdapter(mAdapter);


            } else {
                Log.d("no data found~~","no data found!");
                Toast.makeText(getApplicationContext(),R.string.error_message,Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemId = item.getItemId();
        switch (menuItemId){
            case R.id.most_popular:
                //
/*                Context context = MainActivity.this;
                String textToShow = "Sort clicked";
                Toast.makeText(context,textToShow,Toast.LENGTH_LONG).show();*/
                NetworkUtils.sortBy = NetworkUtils.popularity+"."+NetworkUtils.desc;
                makeSearchQuery();
                setTitle(getString(R.string.most_popular));
                return true;
            case R.id.least_popular:
                NetworkUtils.sortBy = NetworkUtils.popularity+"."+NetworkUtils.asc;
                makeSearchQuery();
                setTitle(getString(R.string.least_popular));
                return true;
            case R.id.top_rated:
                NetworkUtils.sortBy = NetworkUtils.vote_average+"."+NetworkUtils.desc;
                makeSearchQuery();
                setTitle(getString(R.string.top_rated));
                return true;
            case R.id.bottom_rated:
                NetworkUtils.sortBy = NetworkUtils.vote_average+"."+NetworkUtils.asc;
                makeSearchQuery();
                setTitle(getString(R.string.bottom_rated));
                return true;
            case R.id.action_show_pics:
                if(item.isChecked()){
                    item.setChecked(false);
                    MovieAdapter.showPics=false;
                }else{
                    item.setChecked(true);
                    MovieAdapter.showPics=true;
                }
                mAdapter = new MovieAdapter(NUM_LIST_ITEMS,MainActivity.this);
                mList.setAdapter(mAdapter);
                //makeSearchQuery();
                return true;
        }
        //makeSearchQuery();
        /*mAdapter = new MovieAdapter(NUM_LIST_ITEMS,this);
        mList.setAdapter(mAdapter);*/
        //return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
/*        if (mToast!=null){
            mToast.cancel();
        }
        String toastMessage = "Item #"+clickedItemIndex+" clicked.";
        mToast = Toast.makeText(this,toastMessage,Toast.LENGTH_LONG);
        mToast.show();*/

        launchDetailActivity(clickedItemIndex);
    }

    private void launchDetailActivity(int clickedItemIndex) {
        Intent intent = new Intent(this,DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_POS, clickedItemIndex);
        startActivity(intent);
    }
}
