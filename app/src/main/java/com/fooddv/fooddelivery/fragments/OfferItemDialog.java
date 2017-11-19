package com.fooddv.fooddelivery.fragments;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fooddv.fooddelivery.R;
import com.fooddv.fooddelivery.UserActivity;
import com.fooddv.fooddelivery.models.Offer;

import static android.app.Activity.RESULT_OK;

/**
 * Created by vr on 2017-11-19.
 */

public class OfferItemDialog extends DialogFragment {

    public static OfferItemDialog newInstance(int position) {

        OfferItemDialog f = new OfferItemDialog();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("position", position);
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

        final View myView = inflater.inflate(R.layout.offer_item_dialog, null);

        final int position = getArguments().getInt("position");
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

                               ((UserActivity) getActivity()).modyQuantity(position, Integer.parseInt(quantity.getText().toString()));

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
