package com.fooddv.fooddelivery.models;

/**
 * Created by vr on 2017-11-21.
 */

public class Payment {

    private String paymentId;
    private String payment_client;

    public Payment(String paymentId, String payment_client) {
        this.paymentId = paymentId;
        this.payment_client = payment_client;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPayment_client() {
        return payment_client;
    }

    public void setPayment_client(String payment_client) {
        this.payment_client = payment_client;
    }
}
