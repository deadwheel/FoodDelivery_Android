package com.fooddv.fooddelivery;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.fooddv.fooddelivery.models.Response.DriverOrder;
import com.fooddv.fooddelivery.models.Response.DriverOrderActive;
import com.fooddv.fooddelivery.models.Response.DriverSimpleResponse;
import com.fooddv.fooddelivery.models.Response.DriverOrderOffer;
import com.fooddv.fooddelivery.models.Response.DriverOrders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Denis on 2017-10-27.
 */

public class DriverOrdersActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "Driver Orders";


    private Call<DriverOrders> callDriverOrders;
    private Call<DriverSimpleResponse> callTakeIt;
    private Call<DriverOrderActive> callCheckActive;


    private SwipeRefreshLayout swipeRefreshLayout;


    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<DriverOrder> listDataHeader = new ArrayList<>();
    private HashMap<DriverOrder,List<DriverOrderOffer>> listHash = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dx);


        listView = (ExpandableListView)findViewById(R.id.lvExp);
        listAdapter = new ExpandableListAdapter(this,listDataHeader,listHash);
        listView.setAdapter(listAdapter);



        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        init_orders();
                                    }
                                }
        );




        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {


            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                long packedPosition = listView.getExpandableListPosition(position);

                int itemType = ExpandableListView.getPackedPositionType(packedPosition);
                int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
                int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);



  //if group item clicked

                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    //  ...
                    onGroupLongClick(groupPosition);
                }


                return false;
            }
        });

        init_orders();

    }

    private void onGroupLongClick(int groupPosition) {

        final DriverOrder movie = listDataHeader.get(groupPosition);
        AlertDialog.Builder builder = new AlertDialog.Builder(DriverOrdersActivity.this);

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

    private void init_orders() {


        swipeRefreshLayout.setRefreshing(true);
            callDriverOrders = service.GetDriverOrders();
            callDriverOrders.enqueue(new Callback<DriverOrders>() {
                @Override
                public void onResponse(Call<DriverOrders> call, Response<DriverOrders> response) {

                    if(response.isSuccessful()) {


                        if (!listDataHeader.isEmpty() && !listHash.isEmpty()) {

                            listDataHeader.clear();
                            listHash.clear();

                        }


                        List<DriverOrder> test = response.body().getData();


                        for (int i = 0; i < test.size(); i++) {

                            DriverOrder nd = new DriverOrder();
                            nd.setAddress(test.get(i).getAddress());
                            nd.setId(test.get(i).getId());
                            nd.setDeliverer_id(test.get(i).getDeliverer_id());
                            nd.setDet(test.get(i).getDet());
                            nd.setPrice(test.get(i).getPrice());
                            listDataHeader.add(nd);

                            List<DriverOrderOffer> oferdet = test.get(i).getDet();
                            List<DriverOrderOffer> tmp = new ArrayList<>();

                            for (int j = 0; j < oferdet.size(); j++) {

                                DriverOrderOffer offer = new DriverOrderOffer(oferdet.get(j).getOffer_id(), oferdet.get(j).getQuantity(), oferdet.get(j).getOffer_det());
                                tmp.add(offer);
                            }

                            listHash.put(nd, tmp);

                        }


                        swipeRefreshLayout.setRefreshing(false);
                        listAdapter.notifyDataSetChanged();


                    }
                }

                @Override
                public void onFailure(Call<DriverOrders> call, Throwable t) {

                    swipeRefreshLayout.setRefreshing(false);
                    Log.v(TAG, "DriverOrder");

                }

            });

        }

    @Override
    public void onRefresh() {
        init_orders();
    }


    private void activate(final DriverOrder order) {


        callTakeIt = service.take_it(order.getId());
        callTakeIt.enqueue(new Callback<DriverSimpleResponse>() {
            @Override
            public void onResponse(Call<DriverSimpleResponse> call, Response<DriverSimpleResponse> response) {
                if(response.isSuccessful()) {

                    DriverSimpleResponse blabla = response.body();

                    Toast.makeText(getApplicationContext(), order.getAddress() + blabla.getSuccess(), Toast.LENGTH_SHORT).show();


                    listHash.remove(order);
                    listDataHeader.remove(order);


                    Intent i = new Intent(getBaseContext(), DriverOrderActiveActivity.class);
                    i.putExtra("order", order);
                    startActivity(i);


                }
            }

            @Override
            public void onFailure(Call<DriverSimpleResponse> call, Throwable t) {

                Toast.makeText(getApplicationContext(), order.getAddress() + "  DriverOrder ! ", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void check_it(final DriverOrder movie) {

        callCheckActive = service.get_active();
        callCheckActive.enqueue(new Callback<DriverOrderActive>() {
            @Override
            public void onResponse(Call<DriverOrderActive> call, Response<DriverOrderActive> response) {


                if(response.isSuccessful()) {


                    if (response.body().getData() == null) {

                        activate(movie);

                    } else {

                        Toast.makeText(getApplicationContext(), " Najpierw zakoncz swoje aktywne zlecenie ! ", Toast.LENGTH_SHORT).show();

                    }


                }

            }

            @Override
            public void onFailure(Call<DriverOrderActive> call, Throwable t) {

                Toast.makeText(getApplicationContext(), movie.getAddress() + "  DriverOrder ! ", Toast.LENGTH_SHORT).show();


            }
        });

    }



}
