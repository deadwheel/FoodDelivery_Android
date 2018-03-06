package com.fooddv.fooddelivery;

import android.view.View;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

/**
 * Created by root on 22.01.18.
 */

public class OfferViewHolder extends ChildViewHolder{

    public TextView offerTitle;
    public TextView quantity;
    public TextView price;
    public OfferViewHolder(View itemView) {
        super(itemView);

        offerTitle = (TextView)itemView.findViewById(R.id.textViewExperTitleOffer);
        quantity = (TextView)itemView.findViewById(R.id.textViewExperQuantity);
        price = (TextView)itemView.findViewById(R.id.textViewExperPrice);
    }
}
