package com.fooddv.fooddelivery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fooddv.fooddelivery.fragments.OfferItemDialog;
import com.fooddv.fooddelivery.models.Offer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Damian Rakowski on 2017-11-21.
 * Custom Adapter to recyclerView responsibility for shows list of the offers
 *
 * Added findItem on 2017-12-01 by Damian Rakowski
 *
 */

public class OfferRecyklerAdapter extends RecyclerView.Adapter<OfferRecyklerAdapter.OfferViewHolder> {

    private Context context;

    private List<ItemListOffer> offers;
    private LayoutInflater inflater;
    private BasketListener listener;
    private int currentPosition;
    private OfferViewHolder currentHolder;


    public OfferRecyklerAdapter(Context context, List<ItemListOffer> offers) {
        this.context = context;
        this.offers = offers;

        if(context instanceof BasketListener) {
            this.listener = (BasketListener)context;
        }

    }

    @Override
    public OfferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.offer_recycle_row,parent,false);

        OfferViewHolder  myViewHolder = new OfferViewHolder(view);

        return myViewHolder;

    }

    @Override
    public void onBindViewHolder(OfferViewHolder holder, int position) {

        final int p = position;

        final OfferViewHolder myViewHolder = holder;

        final Offer offer = offers.get(position).getOffer();

        holder.offerName.setText(offers.get(position).getOffer().getName());
        holder.price.setText(String.format("%s %s",new BigDecimal(offers.get(position).getOffer().getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP).toString(),Config.DEFAULT_CURRENCY));

        holder.bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                  OfferItemDialog dialog = new OfferItemDialog();
                  dialog.newInstance(p == offers.get(p).position ? p : findItem(offer) , listener, myViewHolder).show(((OfferActivity) context).getSupportFragmentManager(), "");
            }
        });

            if (offers.get(position).isPurchased()) {

                holder.bt.setText("ZMIEN" + "(" + offers.get(position).getOffer().getQuantity() + ")");

            } else {

                holder.bt.setText("KUP");

            }

    }


    @Override
    public int getItemCount() {
        return offers.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class OfferViewHolder extends RecyclerView.ViewHolder implements Serializable{

        TextView offerName;
          TextView price;
        public Button bt;

        public OfferViewHolder(View itemView) {

            super(itemView);

             offerName = (TextView)itemView.findViewById(R.id.textViewOfferName);
             price  = (TextView)itemView.findViewById(R.id.textViewOfferPrice);
             bt = (Button)itemView.findViewById(R.id.btOfferBuy);

        }
    }

    public void setFilter(ArrayList<ItemListOffer> list){

        this.offers = new ArrayList<>();

        for(ItemListOffer o:list){

            this.offers.add(o);

        }

        notifyDataSetChanged();

    }

    public void refresh(List<ItemListOffer> list){

        this.offers.clear();
        this.offers = new ArrayList<>();

        for(ItemListOffer o:list){

            this.offers.add(o);

        }

        notifyDataSetChanged();

    }

/**
 *
 * Method to return position offer at the list
 *
 * @param offer is reference to Offer class
 * @return int is position element at the list
 *
 */
 private int findItem(Offer offer){

    List<ItemListOffer> listOffers = listener.getAllItemListOffer();

     int i;
    for(i=0;i<listOffers.size();i++){

            if(listOffers.get(i).getOffer().equals(offer)){

                return i;

            }

    }

     return -1;

 }


 }
