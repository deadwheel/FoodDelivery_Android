package com.fooddv.fooddelivery.network;



import android.util.Log;
import android.widget.Toast;

import com.fooddv.fooddelivery.TokenManager;
import com.fooddv.fooddelivery.models.AccessToken;
import com.google.android.gms.common.api.Api;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class RetrofitBuilder {

    private static final String BASE_URL = "http://192.168.0.100/fooddelivery/public/api/";

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

        OkHttpClient newClient = client.newBuilder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request request = chain.request();

                Request.Builder builder = request.newBuilder();

                if(tokenManager.getToken().getAccessToken() != null){

                    DateTime now = DateTime.now().withZone(DateTimeZone.forID("Europe/Warsaw"));
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
                     String str = now.toString(formatter);


                     Log.d("GET CREATED AT", "dupa "+tokenManager.getToken().getCreated_at());

                     DateTime simple = formatter.parseDateTime(str);
                     DateTime created = formatter.parseDateTime(tokenManager.getToken().getCreated_at());

                    Seconds ile_sekund = Seconds.secondsBetween(created, simple);
                    long sk = ile_sekund.getSeconds();

                    if(tokenManager.getToken().getExpiresIn() < sk) {

                        ApiService service = RetrofitBuilder.createService(ApiService.class);
                        Call<AccessToken> call = service.refresh(tokenManager.getToken().getRefreshToken());
                        call.enqueue(new Callback<AccessToken>() {
                            @Override
                            public void onResponse(Call<AccessToken> call, retrofit2.Response<AccessToken> response) {

                                if(response.isSuccessful()) {

                                    tokenManager.deleteToken();


                                    AccessToken xd;
                                    xd = response.body();

                                    DateTime now = DateTime.now().withZone(DateTimeZone.forID("Europe/Warsaw")).minusMinutes(5);
                                    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
                                    String str = now.toString(formatter);
                                    xd.setCreated_at(str);

                                    tokenManager.saveToken(xd);

                                }

                            }

                            @Override
                            public void onFailure(Call<AccessToken> call, Throwable throwable) {

                            }
                        });


                    }

                   // Log.d("ZAPYTANIE", str);


                    builder.addHeader("Authorization", "Bearer " + tokenManager.getToken().getAccessToken());
                }
                request = builder.build();
                TokenManager tokenManager;

                return chain.proceed(request);
            }
        }).build();

        Retrofit newRetrofit = retrofit.newBuilder().client(newClient).build();
        return newRetrofit.create(service);

    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }
}
