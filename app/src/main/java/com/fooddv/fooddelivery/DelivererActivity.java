package com.fooddv.fooddelivery;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fooddv.fooddelivery.models.Movie;
import com.fooddv.fooddelivery.models.Offer;
import com.fooddv.fooddelivery.models.Order;
import com.fooddv.fooddelivery.models.Response.DriverTakeItResponse;
import com.fooddv.fooddelivery.models.Response.Offer2;
import com.fooddv.fooddelivery.models.Response.OfferResponse;
import com.fooddv.fooddelivery.models.Response.drive_order;
import com.fooddv.fooddelivery.models.Response.driver_order_2;
import com.fooddv.fooddelivery.models.Response.dupa;
import com.fooddv.fooddelivery.network.ApiService;
import com.fooddv.fooddelivery.network.RetrofitBuilder;
import com.fooddv.fooddelivery.services.LocationService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vr on 2017-10-27.
 */

public class DelivererActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "MyActivity";
    private List<Movie> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private OrderDeliverRecycler mAdapter;
    private Call<drive_order> callOffer;
    private Call<DriverTakeItResponse> callTakeIt;
    private Call<driver_order_2> callCheck;
    private SwipeRefreshLayout swipeRefreshLayout;


    private List<dupa> orders = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        //mAdapter = new OrderDeliverRecycler(movieList);
        mAdapter = new OrderDeliverRecycler(orders);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        prepareMovieData();
                                    }
                                }
        );

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
               dupa movie = orders.get(position);
                Toast.makeText(getApplicationContext(), movie.getAddress() + " is selected!", Toast.LENGTH_SHORT).show();
                Intent serviceIntent = new Intent(DelivererActivity.this, LocationService.class);
                stopService(serviceIntent);
            }

            @Override
            public void onLongClick(View view, int position) {
               final dupa movie = orders.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(DelivererActivity.this);

                builder.setMessage("Czy chcesz aktywowac to zlecenie?").setTitle("Aktywuj");
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                    check_it(movie);


                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }
        }));

        prepareMovieData();

    }

    private void prepareMovieData() {


        swipeRefreshLayout.setRefreshing(true);
            callOffer = service.GetDriverOffers();
            //final OfferActivity root = this;
            callOffer.enqueue(new Callback<drive_order>() {
                @Override
                public void onResponse(Call<drive_order> call, Response<drive_order> response) {

                    if(!orders.isEmpty()) {
                        orders.clear();

                    }


                    List<dupa> test = response.body().getData();

                    for(int i=0;i<test.size();i++) {

                        dupa nd = new dupa();
                        nd.setAddress(test.get(i).getAddress());
                        nd.setId(test.get(i).getId());
                        nd.setDeliverer_id(test.get(i).getDeliverer_id());
                        nd.setDet(test.get(i).getDet());
                        nd.setPrice(test.get(i).getPrice());
                        orders.add(nd);

                    }




                    swipeRefreshLayout.setRefreshing(false);
                    mAdapter.notifyDataSetChanged();

                }

                @Override
                public void onFailure(Call<drive_order> call, Throwable t) {

                    swipeRefreshLayout.setRefreshing(false);
                    Log.v(TAG, "dupa");

                }

            });

        }

    @Override
    public void onRefresh() {
        prepareMovieData();
    }


    private void activate(final dupa movie) {


        callTakeIt = service.take_it(movie.getId());
        callTakeIt.enqueue(new Callback<DriverTakeItResponse>() {
            @Override
            public void onResponse(Call<DriverTakeItResponse> call, Response<DriverTakeItResponse> response) {
                if(response.isSuccessful()) {

                    DriverTakeItResponse blabla = response.body();

                    Toast.makeText(getApplicationContext(), movie.getAddress() + blabla.getSuccess(), Toast.LENGTH_SHORT).show();


                    orders.remove(movie);
                    mAdapter.notifyDataSetChanged();

                    Intent i = new Intent(getBaseContext(), ActiveOrder.class);
                    i.putExtra("order", movie);
                    startActivity(i);


                }
            }

            @Override
            public void onFailure(Call<DriverTakeItResponse> call, Throwable t) {

                Toast.makeText(getApplicationContext(), movie.getAddress() + "  dupa ! ", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void check_it(final dupa movie) {

        callCheck = service.get_active();
        callCheck.enqueue(new Callback<driver_order_2>() {
            @Override
            public void onResponse(Call<driver_order_2> call, Response<driver_order_2> response) {


                //List<dupa> test = response.body().getData();

                if(response.body().getData() == null) {

                     activate(movie);

                }

                else {

                    Toast.makeText(getApplicationContext(),  " Najpierw zakoncz swoje aktywne zlecenie ! ", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<driver_order_2> call, Throwable t) {

                Toast.makeText(getApplicationContext(), movie.getAddress() + "  dupa ! ", Toast.LENGTH_SHORT).show();


            }
        });

    }



}
