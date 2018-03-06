package com.fooddv.fooddelivery;

import com.fooddv.fooddelivery.models.Offer;

import java.io.Serializable;
import java.util.List;

/**
 * Created by root on 26.11.17.
 */

public interface BasketListener extends Serializable {

    public void setPurchasedItem(int position, boolean value);
    public void removeOfferFromBasket(Offer offer);
    public void clearBasket();
    public void setQuantityItem(int position, int value);
    public ItemListOffer getOffer(int position);
    public List<ItemListOffer> getAllItemListOffer();

}
