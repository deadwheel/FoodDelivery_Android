package com.fooddv.fooddelivery.models.Response;

/**
 * Created by Denis on 2017-12-02.
 */

public class DriverOrderActive {

    private DriverOrder data;

    public DriverOrderActive(DriverOrder data) {
        this.data = data;
    }

    public DriverOrder getData() {
        return data;
    }

    public void setData(DriverOrder data) {
        this.data = data;
    }
}
