package com.udacity.mike.popmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

/**
 * Created by mwong on 10/3/2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private int mNumberItems;
    private final ListItemClickListener mOnClickListener;
    public static boolean showPics = true; //false;

    public interface ListItemClickListener{
        void onListItemClick(int clickedItemIndex);
    }

    public MovieAdapter(int mNumberItems, ListItemClickListener listener) {
        this.mNumberItems = mNumberItems;
        mOnClickListener = listener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForItem = R.layout.movie_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForItem, parent, shouldAttachToParentImmediately);
        MovieViewHolder viewHolder = new MovieViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mNumberItems;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView movieItemView;
        ImageView movieImageView;
        TextView movieIdItemView;
        public MovieViewHolder(View itemView) {
            super(itemView);
            movieItemView = (TextView) itemView.findViewById(R.id.tv_movie_item_number);
            movieImageView = (ImageView) itemView.findViewById(R.id.iv_movie_thumb);
            movieIdItemView = (TextView) itemView.findViewById(R.id.tv_movie_item_movieId);
            itemView.setOnClickListener(this);
        }
        public void bind(int itemIndex){
            //movieItemView.setText(String.valueOf(itemIndex));
            JSONObject j = JsonUtils.parseJsonArray(MainActivity.resultsArray,itemIndex);
            Movie m = JsonUtils.parseMovieJsonResult(j);
            movieItemView.append(m.getOrigTitle());
            movieImageView.setContentDescription(m.getOrigTitle());
            movieIdItemView.append(String.valueOf(m.getId()));
            if (showPics) {
                Picasso.with(movieImageView.getContext())
                        .load(m.getImage(NetworkUtils.SIZE_W185))
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .into(movieImageView);
            }
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
