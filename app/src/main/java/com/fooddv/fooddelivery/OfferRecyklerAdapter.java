package com.fooddv.fooddelivery;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fooddv.fooddelivery.fragments.OfferItemDialog;
import com.fooddv.fooddelivery.fragments.OrderDialog;
import com.fooddv.fooddelivery.models.Offer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by vr on 2017-11-21.
 */

public class OfferRecyklerAdapter extends RecyclerView.Adapter<OfferRecyklerAdapter.OfferViewHolder> {

    private Context context;

    private List<Offer> offers;
    private LayoutInflater inflater;
    private BasketListener listener;

    public OfferRecyklerAdapter(Context context, List<Offer> offers, BasketListener listener) {
        this.context = context;
        this.offers = offers;
        this.listener = listener;


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

        holder.offerName.setText(offers.get(position).getName());
        holder.price.setText(String.format("%s %s",new BigDecimal(offers.get(position).getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP).toString(),Config.DEFAULT_CURRENCY));

        holder.bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OfferItemDialog dialog =  new OfferItemDialog();
                dialog.newInstance(offers.get(p), listener, myViewHolder).show(((OfferActivity)context).getSupportFragmentManager(),"");

            }
        });

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




}
