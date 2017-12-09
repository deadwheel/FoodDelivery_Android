package com.fooddv.fooddelivery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fooddv.fooddelivery.models.Order;
import com.fooddv.fooddelivery.models.Response.ResponseStatus;
import com.fooddv.fooddelivery.network.ApiService;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 05.12.17.
 */

public class OrderRecyklerAdapter extends RecyclerView.Adapter<OrderRecyklerAdapter.OrderHolder>{
    private Context context;
    private List<Order> orders;
    private ApiService service;
    private Call<ResponseStatus> responseStatus;
    private String status;

    public OrderRecyklerAdapter(Context context, List<Order> orders, ApiService service) {
        this.context = context;
        this.orders = orders;
        this.service = service;



    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public OrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_row, parent, false);

        return new OrderHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OrderHolder holder, final int position) {

        final OrderHolder holderTmp = holder;
        holder.location.setText(orders.get(position).getLocation());
        holder.status.setText(orders.get(position).getState());
        holder.refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = orders.get(position).getId();
                getStatus(id);

                try {
                    responseStatus.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                holderTmp.status.setText(status);
            }

        });

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class OrderHolder extends RecyclerView.ViewHolder {
        TextView location;
        TextView status;
        Button refresh;
        public OrderHolder(View itemView) {
            super(itemView);
            location = (TextView)itemView.findViewById(R.id.textViewOrderLocation);
           status = (TextView)itemView.findViewById(R.id.textViewOrderStatus);
            refresh = (Button)itemView.findViewById(R.id.btOrderRefresh);
        }
    }

    public void getStatus(int id){

        responseStatus = service.getStatusOrder(id);
        responseStatus.enqueue(new Callback<ResponseStatus>() {
            @Override
            public void onResponse(Call<ResponseStatus> call, Response<ResponseStatus> response) {

                OrderRecyklerAdapter.this.status = response.body().getStatus();

            }

            @Override
            public void onFailure(Call<ResponseStatus> call, Throwable t) {

            }
        });

    }




}
