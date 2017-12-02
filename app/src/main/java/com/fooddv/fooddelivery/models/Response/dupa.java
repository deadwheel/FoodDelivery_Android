package com.fooddv.fooddelivery.models.Response;

import com.fooddv.fooddelivery.models.Offer;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Denis on 2017-11-27.
 */

public class dupa implements Serializable {

    private int id,deliverer_id;
    private double price;
    private String address;
    private List<Offer2> det;

    public dupa() {


    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDeliverer_id() {
        return deliverer_id;
    }

    public void setDeliverer_id(int deliverer_id) {
        this.deliverer_id = deliverer_id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Offer2> getDet() {
        return det;
    }

    public void setDet(List<Offer2> det) {
        this.det = det;
    }

    public dupa(int id, int deliverer_id, double price, String address, List<Offer2> det) {
        this.id = id;
        this.deliverer_id = deliverer_id;
        this.price = price;
        this.address = address;
        this.det = det;
    }
}
