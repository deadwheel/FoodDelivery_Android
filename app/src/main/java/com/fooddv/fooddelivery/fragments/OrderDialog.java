package com.fooddv.fooddelivery.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import com.fooddv.fooddelivery.BasketListener;
import com.fooddv.fooddelivery.DialogRecyclerView;
import com.fooddv.fooddelivery.OfferActivity;
import com.fooddv.fooddelivery.OfferRecyklerAdapter;
import com.fooddv.fooddelivery.R;

/**
 * Created by vr on 2017-11-19.
 */

public class OrderDialog extends DialogFragment {

    public static OrderDialog newInstance(BasketListener listener) {

        OrderDialog f = new OrderDialog();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putSerializable("listener",listener);

        f.setArguments(args);

        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View myView = inflater.inflate(R.layout.order_dialog, null);
        BasketListener listener = (BasketListener) getArguments().getSerializable("listener");
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setTitle("Zamawiam:");


        RecyclerView recyclerView = (RecyclerView) myView.findViewById(R.id.recycler_view_offer_dialog);
        DialogRecyclerView dialogRecyklerAdapter = new DialogRecyclerView(getActivity(),listener.getBasket());
        recyclerView.setAdapter(dialogRecyklerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        builder.setView(myView);
        // Add action buttons
        builder.setPositiveButton(R.string.Zaplac, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                ((OfferActivity)getActivity()).launchPayPalPayment();
                //clear.clear();

            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        OrderDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }



}
