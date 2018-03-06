package com.fooddv.fooddelivery;

import android.os.Parcelable;

import com.fooddv.fooddelivery.models.Order;

import java.util.List;

/**
 * Created by root on 22.01.18.
 */

public class ExpandableGroupOrder <T extends Parcelable> extends com.thoughtbot.expandablerecyclerview.models.ExpandableGroup<T>{

    private Order order;

    public ExpandableGroupOrder(String title, Order order , List<T> items) {
          super(title, items);
          this.order = order;
    }

    public Order getOrder(){

        return this.order;

    }



}
