package com.udacity.mike.popmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

/**
 * Created by mike on 10/28/2018.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private int rNumberOfReviews;
    private final ReviewClickListener rOnClickListener;
    public interface ReviewClickListener{
        void onReviewItemClick(int clickedItemIndex);
    }
    public ReviewAdapter(int num_of_review_items, ReviewClickListener listener) {
        this.rNumberOfReviews=num_of_review_items;
        rOnClickListener = listener;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForItem = R.layout.review_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForItem,parent,shouldAttachToParentImmediately);
        ReviewViewHolder viewHolder = new ReviewViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return rNumberOfReviews;
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //@BindView(R.id.tv_trailer_item)
        TextView reviewTextView;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            reviewTextView = (TextView) itemView.findViewById(R.id.tv_review_item);
            itemView.setOnClickListener(this);
        }

        public void bind(int itemIndex){
            JSONObject j = JsonUtils.parseJsonArray(DetailActivity.reviewResultsArray,itemIndex);
            Review r = JsonUtils.parseReviewJsonResults(j);
            reviewTextView.append(r.getAuthor()+'\n');
            reviewTextView.append(r.getContent());
        }

        @Override
        public void onClick(View view) {
            rOnClickListener.onReviewItemClick(getAdapterPosition());
        }
    }
}
