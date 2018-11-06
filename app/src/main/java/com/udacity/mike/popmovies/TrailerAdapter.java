package com.udacity.mike.popmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private int tNumberTrailers;
    private final TrailerClickListener tOnClickListener;

    public interface TrailerClickListener{
        void onListItemClick(int clickedItemIndex);
    }

    public TrailerAdapter(int tNumberTrailers,TrailerClickListener listener) {
        this.tNumberTrailers=tNumberTrailers;
        tOnClickListener = listener;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForItem = R.layout.trailer_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForItem,parent,shouldAttachToParentImmediately);
        TrailerViewHolder viewHolder = new TrailerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return tNumberTrailers;
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //@BindView(R.id.tv_trailer_item)
        TextView trailerTextView;
        TextView trailerShareTextView;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            trailerTextView = (TextView) itemView.findViewById(R.id.tv_trailer_item);
            trailerShareTextView = (TextView) itemView.findViewById(R.id.tv_trailer_item_share);
            itemView.setOnClickListener(this);
        }

        public void bind(int itemIndex){
            JSONObject j = JsonUtils.parseJsonArray(DetailActivity.trailerResultsArray,itemIndex);
            Trailer t = JsonUtils.parseTrailerJsonResult(j);
            trailerTextView.append(t.getName());
            //trailerShareTextView.append("Share");
        }

        @Override
        public void onClick(View view) {
            tOnClickListener.onListItemClick(getAdapterPosition());
        }
    }
}
