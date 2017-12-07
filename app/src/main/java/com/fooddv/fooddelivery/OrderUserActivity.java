package com.fooddv.fooddelivery;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_user);
        RecyclerView reyclerView = (RecyclerView) findViewById(R.id.OrderRecyclerView);
        orderRecyklerAdapter = new OrderRecyklerAdapter(this,orders);
        reyclerView.setAdapter(orderRecyklerAdapter);
        reyclerView.setLayoutManager(new LinearLayoutManager(this));
        getOrders();
    }

public void getOrders() {

    call = service.get_orders();

    if (call != null) {

        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {

                if (response.isSuccessful()) {
                    for(Order o:response.body().getData()){

                        orders.add(o);

                    }

                    orderRecyklerAdapter.notifyDataSetChanged();


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



