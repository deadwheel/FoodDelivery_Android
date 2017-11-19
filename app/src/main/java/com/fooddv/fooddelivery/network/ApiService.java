package com.fooddv.fooddelivery.network;


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

import java.util.List;
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


    @Headers({"Accept: application/json",
            "Content-Type: application/json"})
    @POST("orders")
    Call<OfferResponse> orders(@Body Map<String, Object> order );



}
