package com.fooddv.fooddelivery.models.Response;

import com.fooddv.fooddelivery.models.Order;

import java.util.List;

/**
 * Created by root on 05.12.17.
 */

public class OrderResponse {

    List<Order> data;

    public List<Order> getData(){

        return data;

    }
}
