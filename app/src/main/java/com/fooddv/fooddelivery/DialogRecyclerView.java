package com.fooddv.fooddelivery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fooddv.fooddelivery.models.Offer;

import java.io.Serializable;
import java.util.List;

/**
 * Created by root on 26.11.17.
 */

public class DialogRecyclerView extends RecyclerView.Adapter<DialogRecyclerView.DialogViewHolder>{

    private List<Offer> offers;
    private LayoutInflater inflater;
    private Context context;

    public DialogRecyclerView(Context context, List<Offer> offers) {
        this.context = context;
        this.offers = offers;
    }

    @Override
    public DialogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.offer_dialog_recykler_row,parent,false);

        DialogRecyclerView.DialogViewHolder myViewHolder = new DialogRecyclerView.DialogViewHolder(view);


        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(DialogViewHolder holder, int position) {
        holder.title.setText(offers.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class DialogViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        public DialogViewHolder(View itemView) {
            super(itemView);
            this.title = (TextView)itemView.findViewById(R.id.textViewDialogOfferName);


        }
    }
}
