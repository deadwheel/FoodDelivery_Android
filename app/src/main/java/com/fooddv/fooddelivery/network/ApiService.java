package com.fooddv.fooddelivery.network;


import io.reactivex.Observable;
import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import com.fooddv.fooddelivery.models.AccessToken;
import com.fooddv.fooddelivery.models.Offer;
import com.fooddv.fooddelivery.models.Order;
import com.fooddv.fooddelivery.models.Response.OfferResponse;
import com.fooddv.fooddelivery.models.Response.PaymentResponse;
import com.fooddv.fooddelivery.models.Response.Response;
import com.fooddv.fooddelivery.models.Response.TestResponse;

import java.util.List;
import java.util.Map;

import static android.R.attr.id;

public interface ApiService {

    @POST("register")
    @FormUrlEncoded
    Call<AccessToken> register(@Field("name") String name, @Field("email") String email, @Field("password") String password);

    @POST("login")
    @FormUrlEncoded
    Call<AccessToken> login(@Field("username") String username, @Field("password") String password);

    @POST("refresh")
    @FormUrlEncoded
    Call<AccessToken> refresh(@Field("refresh_token") String refreshToken);

    @POST("logout")
    Call<AccessToken> logout();

    @GET("offers")
    Call<OfferResponse> offers();

    @POST("verifyPayment")
    @FormUrlEncoded
    Call<PaymentResponse> verify(@Field("paymentId") String paymentId, @Field("paymentClientJson") String paymentClientJson);

    @Headers({"Accept: application/json",
            "Content-Type: application/json"})
    @POST("orders")
    Call<TestResponse> orders(@Body Map<String, Object> order );



}
