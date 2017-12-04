package com.fooddv.fooddelivery;

import com.fooddv.fooddelivery.models.Offer;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Created by root on 26.11.17.
 */

public interface BasketListener extends Serializable {

    void addOfferToBasket(Offer offer);
    void removeOfferFromBasket(Offer offer);
    void clearBasket();
    List<Offer> getBasket();


}
