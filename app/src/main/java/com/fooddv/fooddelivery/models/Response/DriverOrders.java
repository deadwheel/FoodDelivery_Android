package com.fooddv.fooddelivery.models.Response;

import java.util.List;

/**
 * Created by Denis on 2017-11-27.
 */

public class DriverOrders {

    private List<DriverOrder> data;

    public DriverOrders(List<DriverOrder> data) {
        this.data = data;
    }

    public List<DriverOrder> getData() {
        return data;
    }

    public void setData(List<DriverOrder> data) {
        this.data = data;
    }
}
