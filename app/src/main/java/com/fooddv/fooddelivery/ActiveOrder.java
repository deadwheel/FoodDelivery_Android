package com.fooddv.fooddelivery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fooddv.fooddelivery.models.Movie;
import com.fooddv.fooddelivery.models.Response.DriverTakeItResponse;
import com.fooddv.fooddelivery.models.Response.drive_order;
import com.fooddv.fooddelivery.models.Response.driver_order_2;
import com.fooddv.fooddelivery.models.Response.dupa;
import com.fooddv.fooddelivery.services.LocationService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Denis on 2017-11-28.
 */

public class ActiveOrder extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback,
        PermissionUtils.PermissionResultCallback, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "MyActivity";
    private List<Movie> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private OrderDeliverRecycler mAdapter;
    private Call<drive_order> callOffer;
    private Call<DriverTakeItResponse> callTakeIt;
    private Call<driver_order_2> callCheck;

    ArrayList<String> permissions=new ArrayList<>();
    PermissionUtils permissionUtils;
    boolean isPermissionGranted;

    Intent serviceIntent;
    private List<dupa> orders = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new OrderDeliverRecycler(orders);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        Intent i = getIntent();
        if(i.getSerializableExtra("order") != null) {
            dupa movie = (dupa) i.getSerializableExtra("order");
            orders.add(movie);
            mAdapter.notifyDataSetChanged();
        }

        permissionUtils=new PermissionUtils(ActiveOrder.this);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionUtils.check_permission(permissions,"Need GPS permission for getting your location",1);


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        get_active_order();
                                    }
                                }
        );

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                dupa movie = orders.get(position);
                Toast.makeText(getApplicationContext(), movie.getAddress() + " is selected!", Toast.LENGTH_SHORT).show();
                stopService(serviceIntent);
            }

            @Override
            public void onLongClick(View view, int position) {
                final dupa movie = orders.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(ActiveOrder.this);

                builder.setMessage("Czy chcesz zalaczyc mape")
                        .setTitle("Wlacz mape");

                builder.setPositiveButton("Anuluj zamowienie", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        serviceIntent = new Intent(ActiveOrder.this, LocationService.class);
                        stopService(serviceIntent);

                        //TODO POST TO CANCEL ORDER

                    }
                });

                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        serviceIntent = new Intent(ActiveOrder.this, LocationService.class);
                        serviceIntent.putExtra("order_id", movie.getId());
                        startService(serviceIntent);

       /*                String url = "https://www.google.com/maps/dir/?api=1&destination=Wiejska%2045";
                        Intent ix = new Intent(Intent.ACTION_VIEW);
                        ix.setData(Uri.parse(url));
                        startActivity(ix);*/



                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }

        }));


    }

    private void get_active_order() {

        swipeRefreshLayout.setRefreshing(true);
        callCheck = service.get_active();
        callCheck.enqueue(new Callback<driver_order_2>() {
            @Override
            public void onResponse(Call<driver_order_2> call, Response<driver_order_2> response) {


                //List<dupa> test = response.body().getData();

                if(response.body().getData() == null) {

                }

                else {

                    if(!orders.isEmpty()) {
                        orders.clear();

                    }

                    dupa test = response.body().getData();


                        dupa nd = new dupa();
                        nd.setAddress(test.getAddress());
                        nd.setId(test.getId());
                        nd.setDeliverer_id(test.getDeliverer_id());
                        nd.setDet(test.getDet());
                        nd.setPrice(test.getPrice());
                        orders.add(nd);






                    mAdapter.notifyDataSetChanged();
                }

                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onFailure(Call<driver_order_2> call, Throwable t) {

                Toast.makeText(getApplicationContext(),  "  dupa ! ", Toast.LENGTH_SHORT).show();

                swipeRefreshLayout.setRefreshing(false);


            }
        });

    }


    // Permission check functions


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // redirects to utils
        permissionUtils.onRequestPermissionsResult(requestCode,permissions,grantResults);

    }



    @Override
    public void PermissionGranted(int request_code) {
        Log.i("PERMISSION","GRANTED");
        isPermissionGranted=true;
    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {
        Log.i("PERMISSION PARTIALLY","GRANTED");
    }

    @Override
    public void PermissionDenied(int request_code) {
        Log.i("PERMISSION","DENIED");
    }

    @Override
    public void NeverAskAgain(int request_code) {
        Log.i("PERMISSION","NEVER ASK AGAIN");
    }

    public void showToast(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRefresh() {

        get_active_order();

    }
}
