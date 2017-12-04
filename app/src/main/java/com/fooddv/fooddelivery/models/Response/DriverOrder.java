package com.fooddv.fooddelivery.models.Response;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Denis on 2017-11-27.
 */

public class DriverOrder implements Serializable {

    private int id,deliverer_id;
    private double price;
    private String address;
    private List<DriverOrderOffer> det;

    public DriverOrder() {


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

    public List<DriverOrderOffer> getDet() {
        return det;
    }

    public void setDet(List<DriverOrderOffer> det) {
        this.det = det;
    }

    public DriverOrder(int id, int deliverer_id, double price, String address, List<DriverOrderOffer> det) {
        this.id = id;
        this.deliverer_id = deliverer_id;
        this.price = price;
        this.address = address;
        this.det = det;
    }
}
