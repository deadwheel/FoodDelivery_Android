package com.fooddv.fooddelivery.models;

import com.squareup.moshi.Json;

/**
 * Created by Damian Rakowski on 06.12.17.
 */

public class Profile {

   @Json(name="firstname")
   private String firstName;
   @Json(name="lastname")
   private String lastName;
   @Json(name="address")
   private String address;
   @Json(name="postcode")
   private String postCode;
   @Json(name="city")
   private String city;
   @Json(name="phonenumber")
   private String phonenumber;

    public Profile(String firstName, String lastName, String address, String postCode, String city, String phonenumber) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.postCode = postCode;
        this.city = city;
        this.phonenumber = phonenumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
}
