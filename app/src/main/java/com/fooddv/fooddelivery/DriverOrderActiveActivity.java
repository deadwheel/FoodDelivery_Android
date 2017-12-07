package com.fooddv.fooddelivery;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
import com.fooddv.fooddelivery.services.LocationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Denis on 2017-11-28.
 */

public class DriverOrderActiveActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback,
        PermissionUtils.PermissionResultCallback, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "Active Order";


    private Call<DriverOrderActive> callCheck;
    private Call<DriverSimpleResponse> callCancel;
    private Call<DriverSimpleResponse> callEnd;


    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<DriverOrder> listDataHeader = new ArrayList<>();
    private HashMap<DriverOrder,List<DriverOrderOffer>> listHash = new HashMap<>();

    ArrayList<String> permissions=new ArrayList<>();
    PermissionUtils permissionUtils;
    boolean isPermissionGranted;


    Intent serviceIntent;


    private SwipeRefreshLayout swipeRefreshLayout;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dx);

        listView = (ExpandableListView)findViewById(R.id.lvExp);
        listAdapter = new ExpandableListAdapter(this,listDataHeader,listHash);
        listView.setAdapter(listAdapter);

        Intent i = getIntent();

        if(i.getSerializableExtra("order") != null) {


            DriverOrder order = (DriverOrder) i.getSerializableExtra("order");
            listDataHeader.add(order);
            List<DriverOrderOffer> oferdet = order.getDet();
            List<DriverOrderOffer> tmp = new ArrayList<>();

            for (int j = 0; j < oferdet.size(); j++) {

                DriverOrderOffer offer = new DriverOrderOffer(oferdet.get(j).getOffer_id(), oferdet.get(j).getQuantity(), oferdet.get(j).getOffer_det());
                tmp.add(offer);
            }

            listHash.put(order, tmp);
            listAdapter.notifyDataSetChanged();


        }



        permissionUtils=new PermissionUtils(DriverOrderActiveActivity.this);

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




    }

    private void onGroupLongClick(int groupPosition) {

        final DriverOrder order = listDataHeader.get(groupPosition);
        AlertDialog.Builder builder = new AlertDialog.Builder(DriverOrderActiveActivity.this);

        builder.setMessage("Czy chcesz zalaczyc mape")
                .setTitle("Wlacz mape");

        builder.setNeutralButton("Anuluj zamowienie", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                cancel_order(order);

            }
        });

        builder.setPositiveButton("Kieruj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                drive_to(order);




            }
        });

        builder.setNegativeButton("Zakoncz zamowienie", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                end_drive(order);

            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    private void end_drive(final DriverOrder order) {

        Toast.makeText(DriverOrderActiveActivity.this,"Koncze", Toast.LENGTH_LONG).show();
        serviceIntent = new Intent(DriverOrderActiveActivity.this, LocationService.class);
        stopService(serviceIntent);

        callEnd = service.end_it(order.getId());
        callEnd.enqueue(new Callback<DriverSimpleResponse>() {
            @Override
            public void onResponse(Call<DriverSimpleResponse> call, Response<DriverSimpleResponse> response) {

                if(response.isSuccessful()) {

                    listHash.remove(order);
                    listDataHeader.remove(order);
                    listAdapter.notifyDataSetChanged();

                    Intent i = new Intent(getBaseContext(), DriverOrdersActivity.class);
                    startActivity(i);
                }

            }

            @Override
            public void onFailure(Call<DriverSimpleResponse> call, Throwable t) {
                Toast.makeText(DriverOrderActiveActivity.this,"Koncz fail", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void cancel_order(final DriverOrder order) {

        Toast.makeText(DriverOrderActiveActivity.this,"anuluj", Toast.LENGTH_LONG).show();
        serviceIntent = new Intent(DriverOrderActiveActivity.this, LocationService.class);
        stopService(serviceIntent);

        callCancel = service.cancel_it(order.getId());
        callCancel.enqueue(new Callback<DriverSimpleResponse>() {
            @Override
            public void onResponse(Call<DriverSimpleResponse> call, Response<DriverSimpleResponse> response) {

                if(response.isSuccessful()) {

                    listHash.remove(order);
                    listDataHeader.remove(order);
                    listAdapter.notifyDataSetChanged();

                    Intent i = new Intent(getBaseContext(), DriverOrdersActivity.class);
                    startActivity(i);
                }

            }

            @Override
            public void onFailure(Call<DriverSimpleResponse> call, Throwable t) {
                Toast.makeText(DriverOrderActiveActivity.this,"anuluj fail", Toast.LENGTH_LONG).show();
            }
        });


    }

    private void drive_to(DriverOrder order) {

        serviceIntent = new Intent(DriverOrderActiveActivity.this, LocationService.class);
        serviceIntent.putExtra("order_id", order.getId());
        startService(serviceIntent);

        Uri address = Uri.parse("google.navigation:q="+Uri.parse(order.getAddress()));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, address);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);



    }

    private void get_active_order() {

        swipeRefreshLayout.setRefreshing(true);
        callCheck = service.get_active();
        callCheck.enqueue(new Callback<DriverOrderActive>() {
            @Override
            public void onResponse(Call<DriverOrderActive> call, Response<DriverOrderActive> response) {


                if(response.isSuccessful()) {

                    if (response.body().getData() == null) {

                    } else {

                        if (!listDataHeader.isEmpty() && !listHash.isEmpty()) {

                            listDataHeader.clear();
                            listHash.clear();

                        }

                        DriverOrder test = response.body().getData();


                        DriverOrder nd = new DriverOrder();
                        nd.setAddress(test.getAddress());
                        nd.setId(test.getId());
                        nd.setDeliverer_id(test.getDeliverer_id());
                        nd.setDet(test.getDet());
                        nd.setPrice(test.getPrice());
                        //orders.add(nd);
                        listDataHeader.add(nd);

                        List<DriverOrderOffer> oferdet = test.getDet();
                        List<DriverOrderOffer> tmp = new ArrayList<>();

                        for (int j = 0; j < oferdet.size(); j++) {

                            DriverOrderOffer offer = new DriverOrderOffer(oferdet.get(j).getOffer_id(), oferdet.get(j).getQuantity(), oferdet.get(j).getOffer_det());
                            tmp.add(offer);
                        }

                        listHash.put(nd, tmp);


                        listAdapter.notifyDataSetChanged();
                    }

                }

                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onFailure(Call<DriverOrderActive> call, Throwable t) {

                Toast.makeText(getApplicationContext(),  "  DriverOrder ! ", Toast.LENGTH_SHORT).show();

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
