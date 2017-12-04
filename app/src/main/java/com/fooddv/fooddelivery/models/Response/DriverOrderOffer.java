package com.fooddv.fooddelivery.models.Response;

import com.fooddv.fooddelivery.models.Offer;

import java.io.Serializable;

/**
 * Created by Denis on 2017-11-28.
 */

public class DriverOrderOffer implements Serializable {

    private int offer_id;
    private int quantity;
    private Offer offer_det;

    public DriverOrderOffer() {

    }

    public int getOffer_id() {
        return offer_id;
    }

    public void setOffer_id(int offer_id) {
        this.offer_id = offer_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Offer getOffer_det() {
        return offer_det;
    }

    public void setOffer_det(Offer offer_det) {
        this.offer_det = offer_det;
    }

    public DriverOrderOffer(int offer_id, int quantity, Offer offer_det) {
        this.offer_id = offer_id;
        this.quantity = quantity;
        this.offer_det = offer_det;

    }
}
