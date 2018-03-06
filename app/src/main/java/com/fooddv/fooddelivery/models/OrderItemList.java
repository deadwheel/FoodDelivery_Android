package com.fooddv.fooddelivery.models;

/**
 * Created by root on 12.12.17.
 */

public class OrderItemList {

    private int position;
    private Order order;

    public OrderItemList(int position, Order order) {
        this.position = position;
        this.order = order;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
