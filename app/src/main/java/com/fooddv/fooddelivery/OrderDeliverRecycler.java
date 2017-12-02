package com.fooddv.fooddelivery;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fooddv.fooddelivery.models.Movie;
import com.fooddv.fooddelivery.models.Response.dupa;

import java.util.List;

/**
 * Created by Denis on 2017-11-27.
 */

public class OrderDeliverRecycler extends RecyclerView.Adapter<OrderDeliverRecycler.MyView> {

    private List<dupa> moviesList;


    public OrderDeliverRecycler(List<dupa> moviesList) {
        this.moviesList = moviesList;
    }

    @Override
    public OrderDeliverRecycler.MyView onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_row, parent, false);

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(OrderDeliverRecycler.MyView holder, int position) {
        dupa movie = moviesList.get(position);
        holder.title.setText(movie.getAddress());
        holder.genre.setText(String.valueOf(movie.getDeliverer_id()));
        holder.year.setText(String.valueOf(movie.getPrice()));
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public class MyView extends RecyclerView.ViewHolder {

        public TextView title,genre, year;

        public MyView(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            genre = (TextView) itemView.findViewById(R.id.genre);
            year = (TextView) itemView.findViewById(R.id.year);
        }
    }
}
