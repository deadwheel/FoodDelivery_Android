package com.fooddv.fooddelivery;

import com.fooddv.fooddelivery.models.Offer;

import java.io.Serializable;

/**
 * Created by root on 01.12.17.
 */

public class ItemListOffer implements Serializable{

    boolean purchased = false;
    Integer position;
    Offer offer;

    public ItemListOffer(boolean purchased, Offer offer, Integer position) {

        this.purchased = purchased;
        this.offer = offer;
        this.position = position;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemListOffer listOffer = (ItemListOffer) o;

        if (purchased != listOffer.purchased) return false;
        if (position != null ? !position.equals(listOffer.position) : listOffer.position != null)
            return false;
        return offer != null ? offer.equals(listOffer.offer) : listOffer.offer == null;

    }

    @Override
    public int hashCode() {
        int result = (purchased ? 1 : 0);
        result = 31 * result + (position != null ? position.hashCode() : 0);
        result = 31 * result + (offer != null ? offer.hashCode() : 0);
        return result;
    }
}
/*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemListOffer that = (ItemListOffer) o;

        if (purchased != that.purchased) return false;
        return position == that.position;

    }

    @Override
    public int hashCode() {
        int result = (purchased ? 1 : 0);
        result = 31 * result + position;
        return result;
    }
*/