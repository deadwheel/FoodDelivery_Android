package com.fooddv.fooddelivery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fooddv.fooddelivery.models.Order;

import java.util.List;

/**
 * Created by root on 05.12.17.
 */

public class OrderRecyklerAdapter extends RecyclerView.Adapter<OrderRecyklerAdapter.OrderHolder>{
    private Context context;
    private List<Order> orders;

    public OrderRecyklerAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
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
    public void onBindViewHolder(OrderHolder holder, int position) {

        holder.location.setText(orders.get(position).getLocation());

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class OrderHolder extends RecyclerView.ViewHolder {
        TextView location;
        public OrderHolder(View itemView) {
            super(itemView);
            location = (TextView)itemView.findViewById(R.id.textViewOrderLocation);
        }
    }
}
