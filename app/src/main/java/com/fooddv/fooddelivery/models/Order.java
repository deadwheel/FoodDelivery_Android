package com.fooddv.fooddelivery.models;

import java.util.List;

/**
 * Created by vr on 2017-11-19.
 */

public class Order {

      private List<Offer> offers;
      private Order orders;
      private int order_id;
      private int id;
      private String driver_loc;
      private String location;
      private String status;

      private boolean is_optional_address;

      public String getStatus() {
            return status;
      }

      public void setStatus(String status) {
            this.status = status;
      }


      public List<Offer> getOffers() {
            return offers;
      }

      public void setOffers(List<Offer> offers) {
            this.offers = offers;
      }

      public int getOrder_id() {
            return order_id;
      }

      public String getDriver_loc() {
            return driver_loc;
      }

      public void setDriver_loc(String driver_loc) {
            this.driver_loc = driver_loc;
      }

      public String getLocation() {
            return location;
      }

      public void setLocation(String location) {
            this.location = location;
      }

      public String getState() {
            return status;
      }

      public void setState(String state) {
            this.status = state;
      }

      public boolean is_optional_address() {
            return is_optional_address;
      }

      public void setIs_optional_address(boolean is_optional_address) {
            this.is_optional_address = is_optional_address;
      }

      public void setOrder_id(int order_id) {
            this.order_id = order_id;
      }

      public Order getOrders() {
            return orders;
      }

      public void setOrders(Order orders) {
            this.orders = orders;
      }

      public int getId() {
            return id;
      }

      public void setId(int id) {
            this.id = id;
      }



}
