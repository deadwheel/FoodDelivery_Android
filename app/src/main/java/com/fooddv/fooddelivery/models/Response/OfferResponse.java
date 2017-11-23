package com.fooddv.fooddelivery.models.Response;

import android.database.Observable;

import com.fooddv.fooddelivery.models.Offer;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class OfferResponse{

    List<Offer> data;

    public List<Offer> getData() {
        return data;
    }


}
