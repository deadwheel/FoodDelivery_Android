package com.fooddv.fooddelivery.models.Response;

/**
 * Created by root on 08.12.17.
 */

public class ResponseStatus {

    private String status;
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
