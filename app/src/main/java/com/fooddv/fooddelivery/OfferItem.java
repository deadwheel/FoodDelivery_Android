package com.fooddv.fooddelivery;

import android.os.Parcel;

import com.fooddv.fooddelivery.models.Offer;
import com.fooddv.fooddelivery.models.Order;

import java.util.List;

/**
 * Created by root on 22.01.18.
 */

public class OfferItem extends ExpandableGroupOrder<Offer> {

    private Order order;

    private Parcel dest;
    private String title = "ala ma kota";

    public OfferItem(String title, Order order, List<Offer> items){
        super(title,order,items);
        this.order = order;

    }
    @Override
    public String getTitle(){

        return "saddsa";
    }




}
