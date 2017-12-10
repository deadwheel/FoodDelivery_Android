package com.fooddv.fooddelivery;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fooddv.fooddelivery.fragments.OfferItemDialog;
import com.fooddv.fooddelivery.models.Offer;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by root on 29.11.17.
 */

public class BasketRecyklerAdapter extends RecyclerView.Adapter<BasketRecyklerAdapter.BasketViewHolder> {

    private Context context;

    private List<Offer> offers;
    private LayoutInflater inflater;
    private BasketListener listener;

    public BasketRecyklerAdapter(Context context, List<Offer> offers) {
        this.context = context;
        this.offers = offers;

         if(context instanceof BasketListener) {

            this.listener = (BasketListener)context;
        }

    }

    @Override
    public BasketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        inflater = LayoutInflater.from(context);

        View view=inflater.inflate(R.layout.basket_row,parent,false);

        BasketViewHolder  myViewHolder = new BasketViewHolder(view);


        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(BasketViewHolder holder, int position) {
        holder.title.setText(offers.get(position).getName());
        holder.desc.setText(offers.get(position).getDesc());
        holder.price.setText(String.format("%s %s",new BigDecimal(offers.get(position).getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP).toString(),Config.DEFAULT_CURRENCY));
        holder.quantity.setText(String.valueOf(offers.get(position).getQuantity()));

        final Offer offer=offers.get(position);
        final int p = position;

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OfferItemDialog dialog =  new OfferItemDialog();
                dialog.newInstance(p, listener, null).show(((BasketActivity)context).getSupportFragmentManager(),"");

            }
        });

       holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Usuń przedmiot o id:"+p);
                builder.setMessage("Czy na pewno chcesz usunąc ten przedmiot z koszyka?");
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.removeOfferFromBasket(offer);
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return offers.size();
    }


    public class BasketViewHolder extends RecyclerView.ViewHolder {

       private TextView title;
       private TextView desc;
       private TextView quantity;
       private TextView price;
       private Button order;
       private Button clear;
       private Button edit;
       private Button remove;

        public BasketViewHolder(View itemView) {
            super(itemView);
            this.title = (TextView)itemView.findViewById(R.id.textViewBasketOfferTitle);
            this.desc  = (TextView)itemView.findViewById(R.id.textViewBasketOfferDesc);
            this.price = (TextView)itemView.findViewById(R.id.textViewBasketOfferPrice);
            this.quantity = (TextView)itemView.findViewById(R.id.textViewBasketOfferQuantity);
            this.remove = (Button)itemView.findViewById(R.id.btBasketOfferRemove);
            this.edit = (Button)itemView.findViewById(R.id.btBasketOfferModifyQuanity);

        }
    }


}
