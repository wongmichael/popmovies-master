package com.udacity.mike.popmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.mike.popmovies.data.MovieContract;

import butterknife.BindView;

import static com.udacity.mike.popmovies.MovieAdapter.showPics;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private Context mContext;
    //private int fNumberOfFavorites;
    private Cursor mCursor;

    //@BindView(R.id.tv_movie_item_number) TextView movieItemView;

    private final ListItemClickListener fOnClickListener;
    public interface ListItemClickListener{
        void onListItemClick(int clickedItemIndex);
        //void onListItemClick(String movieId);
    }

    //public FavoriteAdapter(Context context,int count){
    public FavoriteAdapter(Context context, Cursor cursor, ListItemClickListener listener){
        this.mContext = context;
        //fNumberOfFavorites = count;
        this.mCursor = cursor;
        this.fOnClickListener = listener;
        Log.d("favadapter constr","favadapter constr");
    }

    @Override
    public FavoriteAdapter.FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Context context = parent.getContext();
        int layoutIdForItem = R.layout.movie_item;
        LayoutInflater inflater = LayoutInflater
                //.from(context)
                .from(mContext)
                ;
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForItem, parent, shouldAttachToParentImmediately);
        FavoriteViewHolder viewHolder = new FavoriteViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FavoriteAdapter.FavoriteViewHolder holder, int position) {
        Log.d("obvh","obvh");
        if (!mCursor.moveToPosition(position)) return;
        TextView movieItemView = (TextView) holder.itemView.findViewById(R.id.tv_movie_item_number);
        ImageView movieImageView = (ImageView) holder.itemView.findViewById(R.id.iv_movie_thumb);
        String title = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE));
        String image = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
        String overview = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));
        Double rating = Double.valueOf(mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING)));
        String releaseDate = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
        int id = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)));
        movieItemView.append(title);
        movieImageView.setContentDescription(title);
        TextView movieIdItemView = (TextView) holder.itemView.findViewById(R.id.tv_movie_item_movieId);
        movieIdItemView.append(String.valueOf(id));
        if(showPics){
            Picasso.with(mContext)
                    //with(movieImageView.getContext())
                    .load(NetworkUtils.HTTP+NetworkUtils.TMDB_BASE_URL_IMAGE+"/"+NetworkUtils.SIZE_W185+image)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(movieImageView);
        }
    }

    @Override
    public int getItemCount() {
        Log.d("mCursor getct", String.valueOf(mCursor.getCount()));
        return mCursor.getCount();
        //return fNumberOfFavorites;
    }

    public void swapCursor(Cursor newCursor){
        if (mCursor!=null) mCursor.close();
        mCursor=newCursor;
        if (newCursor!=null){
            this.notifyDataSetChanged();
        }
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public FavoriteViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
/*            int clickedPosition = getAdapterPosition();
            fOnClickListener.onListItemClick(clickedPosition);*/
            TextView movieId = view.findViewById(R.id.tv_movie_item_movieId);
            Log.d("getText MovID",String.valueOf(movieId.getText()));
            fOnClickListener.onListItemClick(Integer.parseInt(String.valueOf(movieId.getText())));
        }
    }
}
