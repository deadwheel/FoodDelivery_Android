package com.fooddv.fooddelivery.models;

/**
 * Created by root on 08.12.17.
 */

public class Status {

    private String message;

    public Status(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
