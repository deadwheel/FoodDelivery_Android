package com.fooddv.fooddelivery.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fooddv.fooddelivery.BasketListener;
import com.fooddv.fooddelivery.OfferRecyklerAdapter;
import com.fooddv.fooddelivery.R;

/**
 * Created by vr on 2017-11-19.
 */

public class OfferItemDialog extends DialogFragment {

    public static OfferItemDialog newInstance(int position, BasketListener listener, OfferRecyklerAdapter.OfferViewHolder holder) {

        OfferItemDialog f = new OfferItemDialog();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putSerializable("listener",listener);
        args.putSerializable("holder",holder);

        f.setArguments(args);

        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View myView = inflater.inflate(R.layout.offer_item_dialog, null);

        final int position = getArguments().getInt("position");
        final BasketListener listener = (BasketListener) getArguments().getSerializable("listener");
        final OfferRecyklerAdapter.OfferViewHolder holder = (OfferRecyklerAdapter.OfferViewHolder)getArguments().getSerializable("holder");
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setTitle("Wpisz ilość:");
        builder.setView(myView);
                // Add action buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                          EditText quantity = (EditText)myView.findViewById(R.id.editTextOfferDialogQuantity);

                            try {

                            int q = Integer.parseInt(quantity.getText().toString());
                            if(q > 0 && position!=-1) {

                                   listener.setQuantityItem(position,q);
                                   // listener.addOfferToBasket(listener.getOffer(position));
                                    listener.setPurchasedItem(position,true);
                                    if(holder != null)
                                        holder.bt.setText("ZMIEN" + "(" + String.valueOf(q) + ")");

                                }else {
                                    if(holder != null)
                                        holder.bt.setText("KUP");
                                        listener.setPurchasedItem(position,false);
                                        listener.removeOfferFromBasket(listener.getOffer(position).getOffer());
                                        Toast.makeText(getContext(),"Ilośc musi być większa niż 0",Toast.LENGTH_SHORT).show();
                                }

                            }catch(NumberFormatException msg){
                                Toast.makeText(getContext(),msg.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        OfferItemDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }






}
