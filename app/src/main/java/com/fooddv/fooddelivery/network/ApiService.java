package com.fooddv.fooddelivery.network;


import io.reactivex.Observable;
import retrofit2.Call;

import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

import com.fooddv.fooddelivery.models.AccessToken;
import com.fooddv.fooddelivery.models.Offer;
import com.fooddv.fooddelivery.models.Order;
import com.fooddv.fooddelivery.models.Response.DriverTakeItResponse;
import com.fooddv.fooddelivery.models.Response.OfferResponse;
import com.fooddv.fooddelivery.models.Response.PaymentResponse;
import com.fooddv.fooddelivery.models.Response.Response;
import com.fooddv.fooddelivery.models.Response.TestResponse;
import com.fooddv.fooddelivery.models.Response.drive_order;
import com.fooddv.fooddelivery.models.Response.driver_order_2;
import com.fooddv.fooddelivery.models.Response.dupa;

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

    @GET("driver/orders/")
    Call<drive_order> GetDriverOffers();

    @POST("driver/take_it/{id}")
    Call<DriverTakeItResponse> take_it(@Path("id") int id);

    @FormUrlEncoded
    @POST("driver/update_pos/{id}")
    Call<DriverTakeItResponse> update_pos(@Path("id") int id, @Field("position") String position);


    @GET("driver/orders/active/")
    Call<driver_order_2> get_active();

    @POST("verifyPayment")
    @FormUrlEncoded
    Call<PaymentResponse> verify(@Field("paymentId") String paymentId, @Field("paymentClientJson") String paymentClientJson);

    @Headers({"Accept: application/json",
            "Content-Type: application/json"})
    @POST("orders")
    Call<TestResponse> orders(@Body Map<String, Object> order );



}
