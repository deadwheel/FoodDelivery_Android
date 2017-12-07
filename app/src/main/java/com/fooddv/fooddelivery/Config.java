package com.fooddv.fooddelivery;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;

/**
 * Created by vr on 2017-11-21.
 */

public class Config {

    public static final String PAYPAL_CLIENT_ID = "AQP1-J5EhPqaqT3MCnzPW-zt9IFE8Cm8GTytaayY11DYMkMmoDTIFeIRKzRexDtfgmiwW2nMcrvGwlD6";
    public static final String PAYPAL_CLIENT_SECRET = "EJ5tnuxfZ_sa5FpUcomBUJF9cHkCEKC5tQxZYBLkFW0sCFBY0BeWCBirSNJzUZbne7uwKSJs7K_3f7E_";

    public static final String PAYPAL_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    public static final String PAYMENT_INTENT = PayPalPayment.PAYMENT_INTENT_SALE;
    public static final String DEFAULT_CURRENCY = "PLN";

    public static final String API_NOT_CONNECTED = "Google API not connected";
    public static final String SOMETHING_WENT_WRONG = "OOPs!!! Something went wrong...";
    public static String PlacesTag = "Google Places Auto Complete";
}
