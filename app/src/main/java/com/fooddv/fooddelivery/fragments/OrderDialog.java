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

import com.fooddv.fooddelivery.R;
import com.fooddv.fooddelivery.UserActivity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vr on 2017-11-19.
 */

public class OrderDialog extends DialogFragment {

    public static OrderDialog newInstance(Map<String, Object> map) {

        OrderDialog f = new OrderDialog();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putSerializable("map", (HashMap<String,Object>)map);

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
        final Map<String,Object> map = (HashMap<String,Object>)getArguments().getSerializable("map");
               // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setTitle("Zamawiam:");
        builder.setView(myView);
        // Add action buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                ((UserActivity)getActivity()).makeOrder(map);
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
