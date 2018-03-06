package com.fooddv.fooddelivery;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.fooddv.fooddelivery.models.Order;
import com.fooddv.fooddelivery.models.Response.OrderResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderUserActivity extends BaseActivity {

    private Call<OrderResponse> call;
    private List<Order> orders = new ArrayList<>();
    private OrderRecyklerAdapter orderRecyklerAdapter;
    private OrderUserAdapter adapter;
    RecyclerView reyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_user);
        reyclerView = (RecyclerView) findViewById(R.id.OrderRecyclerView);
        setTitle("Zamówienia");
        List<OfferItem> offerItem = new ArrayList<>();



        getOrders();
    }

public void getOrders() {

    call = service.get_orders();

    if (call != null) {

        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {

                if (response.isSuccessful()) {
                    List<OfferItem> offerItem = new ArrayList<>();
                    for(Order o:response.body().getData()){

                        //if(){}

                        orders.add(o);

                    }

                    for(Order o:orders){

                       offerItem.add(new OfferItem("nic", o, o.getOffers()));

                    }

                    adapter = new OrderUserAdapter(OrderUserActivity.this, offerItem, service);
                    //   orderRecyklerAdapter = new OrderRecyklerAdapter(this,orders,service);
                    reyclerView.setAdapter(adapter);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    reyclerView.setLayoutManager(layoutManager);
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(reyclerView.getContext(),
                    layoutManager.getOrientation());
                    reyclerView.addItemDecoration(dividerItemDecoration);

                    adapter.notifyDataSetChanged();


                } else {
                    Toast.makeText(getApplicationContext(),"orders nie powodzenie",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {

            }
        });


    }else {

        Toast.makeText(getApplicationContext(),"order null",Toast.LENGTH_SHORT).show();

    }
}



    }



