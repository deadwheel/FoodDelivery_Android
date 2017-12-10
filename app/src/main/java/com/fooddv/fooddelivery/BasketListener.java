package com.fooddv.fooddelivery;

import com.fooddv.fooddelivery.models.Offer;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Created by root on 26.11.17.
 */

public interface BasketListener extends Serializable {

    public void addOfferToBasket(Offer offer);
        public void setPurchasedItem(int position, boolean value);

    public void removeOfferFromBasket(Offer offer);
    public void clearBasket();
    public void setQuantityItem(int position, int value);
    public void putOfferToBasket(int position, Offer offer);
    public ItemListOffer getOffer(int position);
    public List<ItemListOffer> getAllItemListOffer();
    public List<Offer> getBasket();

}
