package com.fooddv.fooddelivery;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fooddv.fooddelivery.models.Offer;
import com.fooddv.fooddelivery.models.Order;
import com.fooddv.fooddelivery.models.Response.ResponseStatus;
import com.fooddv.fooddelivery.network.ApiService;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 22.01.18.
 */

public class OrderUserAdapter extends ExpandableRecyclerViewAdapter<OrderViewHolder, OfferViewHolder> {

    private Context context;
    private List<Order> orders;
    private ApiService service;
    private Call<ResponseStatus> responseStatus;
    private String status;
    private OrderViewHolder currentHolder;

    public OrderUserAdapter(Context context, List<? extends ExpandableGroupOrder> groups, ApiService service) {
        super(groups);
        this.context = context;
        this.service = service;
    }

    @Override
    public OrderViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_row, parent, false);

        return new OrderViewHolder(view);
    }

    @Override
    public OfferViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exper_offer, parent, false);

        return new OfferViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(OfferViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        ExpandableGroupOrder groupp =  (ExpandableGroupOrder)group;
        Offer offer = (Offer)groupp.getItems().get(childIndex);
        holder.offerTitle.setText(offer.getName());
        holder.quantity.setText(String.valueOf(offer.getQuantity()));
        holder.price.setText(String.format("%s %s",new BigDecimal(offer.getPrice()*offer.getQuantity()).setScale(2, BigDecimal.ROUND_HALF_UP).toString(),Config.DEFAULT_CURRENCY));
    }

    @Override
    public void onBindGroupViewHolder(OrderViewHolder holder, int flatPosition, ExpandableGroup group) {
        ExpandableGroupOrder groupp =  (ExpandableGroupOrder)group;
        final OrderViewHolder holderTmp = holder;
        final Order order = groupp.getOrder();


        holder.location.setText(order.getLocation());

        if(order.getState().equals("PAID"))
            holder.status.setText("W trakcie realizacji");
        else if(order.getState().equals("DRIVER_ON_MY_WAY"))
            holder.status.setText("Kierowca juz jedzie do ciebie");
        else if(order.getState().equals("DELIVERED"))
            holder.status.setText("Dostarczono");
        else if(order.getState().equals("DRIVER_READY_TO_GO"))
            holder.status.setText("Twoje zamowienie jest juz u kierowcy");

        double sum = 0.0;
        for(Offer o:order.getOffers()){

            sum+=o.getQuantity()*o.getPrice();

        }

        holder.suma.setText(String.format("%s %s",new BigDecimal(sum).setScale(2, BigDecimal.ROUND_HALF_UP).toString(),Config.DEFAULT_CURRENCY));
        // holder.date.setText(orders.get(position).getDate());
        holder.refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                int id = order.getId();
                getStatus(id);
                currentHolder = holderTmp;

            }

        });

        holder.ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final List<ItemListOffer> itemListOffers = new ArrayList<ItemListOffer>();

                for(Offer it:order.getOffers()){
                    it.setOffer_id(it.getId());
                    itemListOffers.add(new ItemListOffer(true,it,null));
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Ponowne zamówienie");
                builder.setMessage("Czy napewno chcesz złożyc ponowne zamówienie?");
// Add the buttons
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        saveBasketMapToJson(itemListOffers);
                        Intent basketActivity = new Intent(context, BasketActivity.class);

                        context.startActivity(basketActivity);
                        // User clicked OK button
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
// Set other dialog properties


// Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });
        // holder.titleOrder.setText(groupp.getOrder().getLocation());
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
