package com.fooddv.fooddelivery.models.Response;

import com.fooddv.fooddelivery.models.Offer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Denis on 2017-11-27.
 */

public class drive_order {

    private List<dupa> data;

    public drive_order(List<dupa> data) {
        this.data = data;
    }

    public List<dupa> getData() {
        return data;
    }

    public void setData(List<dupa> data) {
        this.data = data;
    }
}
