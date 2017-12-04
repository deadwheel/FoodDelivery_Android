package com.fooddv.fooddelivery.models.Response;

/**
 * Created by Denis on 2017-11-28.
 */

public class DriverSimpleResponse {

    private String success, error;

    public DriverSimpleResponse(String success, String error) {
        this.success = success;
        this.error = error;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
