package com.fooddv.fooddelivery.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.fooddv.fooddelivery.TokenManager;
import com.fooddv.fooddelivery.models.Response.DriverSimpleResponse;
import com.fooddv.fooddelivery.network.ApiService;
import com.fooddv.fooddelivery.network.RetrofitBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Denis on 2017-11-29.
 */

public class LocationService extends Service implements com.google.android.gms.location.LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {


    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    public static Location mCurrentLocation;
    private final static int PLAY_SERVICES_REQUEST = 1000;
    private final static int REQUEST_CHECK_SETTINGS = 2000;
    private int order_id = 0;

    protected ApiService service;
    protected TokenManager tokenManager;
    private Call<DriverSimpleResponse> callTakeIt;

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {

                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location requests here
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });

    }


    @Override
    public void onDestroy() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }

    public Address getAddress(double latitude, double longitude)
    {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude,longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    public void getAddress()
    {

        Address locationAddress=getAddress(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());

        if(locationAddress!=null)
        {
            String address = locationAddress.getAddressLine(0);
            String address1 = locationAddress.getAddressLine(1);
            String city = locationAddress.getLocality();
            String state = locationAddress.getAdminArea();
            String country = locationAddress.getCountryName();
            String postalCode = locationAddress.getPostalCode();

            String currentLocation;

            if(!TextUtils.isEmpty(address))
            {
                currentLocation=address;

                if (!TextUtils.isEmpty(address1))
                    currentLocation+="\n"+address1;

                if (!TextUtils.isEmpty(city))
                {
                    currentLocation+="\n"+city;

                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation+=" - "+postalCode;
                }
                else
                {
                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation+="\n"+postalCode;
                }

                if (!TextUtils.isEmpty(state))
                    currentLocation+="\n"+state;

                if (!TextUtils.isEmpty(country))
                    currentLocation+="\n"+country;


                //Toast.makeText(this, currentLocation, Toast.LENGTH_LONG).show();


            }


            tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
            service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

            callTakeIt = service.update_pos(order_id, address);
            callTakeIt.enqueue(new Callback<DriverSimpleResponse>() {
                @Override
                public void onResponse(Call<DriverSimpleResponse> call, Response<DriverSimpleResponse> response) {


                    if(response.isSuccessful()) {

                        DriverSimpleResponse blabla = response.body();
                        //Toast.makeText(getApplicationContext(), order_id + " " + blabla.getSuccess(), Toast.LENGTH_SHORT).show();


                    }


                }

                @Override
                public void onFailure(Call<DriverSimpleResponse> call, Throwable t) {

                    //Toast.makeText(getApplicationContext(), order_id + "  DriverOrder ! ", Toast.LENGTH_SHORT).show();

                }
            });

        }

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

        mCurrentLocation = location;

        if(order_id != 0) {


            getAddress();

        }

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.getExtras() != null){
            order_id = intent.getIntExtra("order_id", 0);
        }

        return START_STICKY;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        startLocationUpdates();

    }

    protected void startLocationUpdates() {
        try {
            @SuppressLint("MissingPermission") PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (IllegalStateException e) {}
    }

    @Override
    public void onConnectionSuspended(int i) {

        mGoogleApiClient.connect();


    }
}
