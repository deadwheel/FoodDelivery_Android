package com.fooddv.fooddelivery.network;



import android.support.annotation.Nullable;

import com.fooddv.fooddelivery.TokenManager;
import com.fooddv.fooddelivery.models.AccessToken;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class RetrofitBuilder {

    private static final String BASE_URL = "http://stormy-chamber-44477.herokuapp.com/api/";

    private final static OkHttpClient client = buildClient();
    private final static Retrofit retrofit = buildRetrofit(client);
    private static TokenManager token;


    private static OkHttpClient buildClient(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
// set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


// add your other interceptors …


        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1,TimeUnit.MINUTES)
                .addInterceptor(logging)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();

                        Request.Builder builder = request.newBuilder()
                                .addHeader("Accept", "application/json")
                                .addHeader("Connection", "close");

                        request = builder.build();

                        return chain.proceed(request);

                    }
                });



        return builder.build();

    }

    private static Retrofit buildRetrofit(OkHttpClient client){
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static <T> T createService(Class<T> service){
        return retrofit.create(service);
    }

    public static <T> T createServiceWithAuth(Class<T> service, final TokenManager tokenManager){

        OkHttpClient newClient = client.newBuilder()
                .authenticator(new Authenticator() {
                    @Nullable
                    @Override
                    public Request authenticate(Route route, final Response response) throws IOException {

                        AccessToken token = tokenManager.getToken();

                        ApiService service = RetrofitBuilder.createService(ApiService.class);
                        Call<AccessToken> call = service.refresh(token.getRefreshToken());
                        AccessToken fgh = call.execute().body();

                           if(fgh != null) {

                               tokenManager.saveToken(fgh);

                           }


                        return response.request().newBuilder().header("Authorization", "Bearer " + tokenManager.getToken().getAccessToken()).build();

                    }

                })
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(final Chain chain) throws IOException {

                        Request request = chain.request();
                        Request.Builder builder = request.newBuilder();


                        if(tokenManager.getToken().getAccessToken() != null) {

                            builder.addHeader("Authorization", "Bearer " + tokenManager.getToken().getAccessToken());

                        }



                        request = builder.build();
                        return chain.proceed(request);

                    }

                })
                .build();



        Retrofit newRetrofit = retrofit.newBuilder().client(newClient).build();
        return newRetrofit.create(service);

    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }

}
