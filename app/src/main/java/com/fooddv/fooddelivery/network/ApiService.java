package com.fooddv.fooddelivery.network;


import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

import com.fooddv.fooddelivery.models.AccessToken;
import com.fooddv.fooddelivery.models.Response.DriverSimpleResponse;
import com.fooddv.fooddelivery.models.Response.OfferResponse;
import com.fooddv.fooddelivery.models.Response.PaymentResponse;
import com.fooddv.fooddelivery.models.Response.TestResponse;
import com.fooddv.fooddelivery.models.Response.DriverOrders;
import com.fooddv.fooddelivery.models.Response.DriverOrderActive;

import java.util.Map;

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

    @GET("driver/orders/")
    Call<DriverOrders> GetDriverOrders();

    @POST("driver/take_it/{id}")
    Call<DriverSimpleResponse> take_it(@Path("id") int id);

    @POST("driver/cancel_it/{id}")
    Call<DriverSimpleResponse> cancel_it(@Path("id") int id);

    @POST("driver/end_it/{id}")
    Call<DriverSimpleResponse> end_it(@Path("id") int id);

    @FormUrlEncoded
    @POST("driver/update_pos/{id}")
    Call<DriverSimpleResponse> update_pos(@Path("id") int id, @Field("position") String position);


    @GET("driver/orders/active/")
    Call<DriverOrderActive> get_active();

    @POST("verifyPayment")
    @FormUrlEncoded
    Call<PaymentResponse> verify(@Field("paymentId") String paymentId, @Field("paymentClientJson") String paymentClientJson);

    @Headers({"Accept: application/json",
            "Content-Type: application/json"})
    @POST("orders")
    Call<TestResponse> orders(@Body Map<String, Object> order );



}
