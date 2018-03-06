package com.fooddv.fooddelivery;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fooddv.fooddelivery.models.Offer;
import com.fooddv.fooddelivery.models.Order;
import com.fooddv.fooddelivery.models.Response.ResponseStatus;
import com.fooddv.fooddelivery.network.ApiService;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
    private OrderHolder currentHolder;

    public OrderRecyklerAdapter(Context context, List<Order> orders, ApiService service) {
        this.context = context;
        this.orders = orders;
        this.service = service;
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
       // holder.date.setText(orders.get(position).getDate());
        holder.refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int id = orders.get(position).getId();
                getStatus(id);
                currentHolder = holderTmp;

            }

        });

        holder.ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<ItemListOffer> itemListOffers = new ArrayList<ItemListOffer>();

                for(Offer it:orders.get(position).getOffers()){

                   itemListOffers.add(new ItemListOffer(true,it,null));

                }

                saveBasketMapToJson(itemListOffers);
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
        ImageButton refresh;
        Button ref;

        public OrderHolder(View itemView) {
            super(itemView);
            location = (TextView)itemView.findViewById(R.id.textViewOrderLocation);
            status = (TextView)itemView.findViewById(R.id.textViewOrderStatus);
            refresh = (ImageButton)itemView.findViewById(R.id.btOrderRefresh);
            ref = (Button)itemView.findViewById(R.id.btRefreshMakeOrder);

         }
    }

    public void getStatus(int id){

        responseStatus = service.getStatusOrder(id);
        responseStatus.enqueue(new Callback<ResponseStatus>() {
            @Override
            public void onResponse(Call<ResponseStatus> call, Response<ResponseStatus> response) {

               setStatus(response.body().getMessage());

            }

            @Override
            public void onFailure(Call<ResponseStatus> call, Throwable t) {

            }
        });

    }

    public void setStatus(String status){

        this.status = status;
        if(this.currentHolder!=null)
            this.currentHolder.status.setText(status);

    }



    private void saveBasketMapToJson(List<ItemListOffer> itemListOffer){

        Moshi moshi = new Moshi.Builder().build();

        Type type = Types.newParameterizedType(List.class,ItemListOffer.class);
        JsonAdapter<List<ItemListOffer>> jsonAdapter = moshi.adapter(type);

        String json = jsonAdapter.toJson(itemListOffer);
        SharedPreferences basketShared = context.getSharedPreferences("basket", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = basketShared.edit();

        editor.remove("products").commit();
        editor.putString("products",json).commit();


    }



}
