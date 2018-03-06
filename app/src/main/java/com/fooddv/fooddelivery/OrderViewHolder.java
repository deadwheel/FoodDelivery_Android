package com.fooddv.fooddelivery;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

/**
 * Created by root on 22.01.18.
 */

public class OrderViewHolder extends GroupViewHolder {

    public TextView location;
    public TextView status;
    public ImageButton refresh;
    public TextView suma;
    public Button ref;

    public OrderViewHolder(View itemView) {
        super(itemView);
        location = (TextView)itemView.findViewById(R.id.textViewOrderLocation);
        status = (TextView)itemView.findViewById(R.id.textViewOrderStatus);
        refresh = (ImageButton)itemView.findViewById(R.id.btOrderRefresh);
        ref = (Button)itemView.findViewById(R.id.btRefreshMakeOrder);
        suma = (TextView)itemView.findViewById(R.id.textViewOrderSuma2);
    }
}
