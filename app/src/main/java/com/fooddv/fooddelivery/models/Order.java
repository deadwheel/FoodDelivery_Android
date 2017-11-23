package com.fooddv.fooddelivery.models;

import com.google.gson.annotations.SerializedName;
import com.squareup.moshi.Json;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vr on 2017-11-19.
 */

public class Order {

      private List<Offer> offers;
      private int order_id;

      public List<Offer> getOffers() {
            return offers;
      }

      public void setOffers(List<Offer> offers) {
            this.offers = offers;
      }

      public int getOrder_id() {
            return order_id;
      }

      public void setOrder_id(int order_id) {
            this.order_id = order_id;
      }
}
