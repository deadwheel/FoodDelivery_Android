package com.fooddv.fooddelivery.models;

/**
 * Created by root on 15.01.18.
 */

public class AddressContener {

   private String firstName;
   private String lastName;
   private String address2;
   private String postCode;
   private String city;
   private String phoneNumber;

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

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public boolean valid(){

        if(firstName.length() > 0
                && lastName.length() > 0
                && address2.length() > 0
                && postCode.length() > 0
                && city.length()>0
                && phoneNumber.length()>0
                ) {
            return true;
        }



        return false;


    }


}
