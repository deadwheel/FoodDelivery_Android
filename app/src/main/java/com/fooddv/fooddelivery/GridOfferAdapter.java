package com.fooddv.fooddelivery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.fooddv.fooddelivery.models.Offer;

import java.util.List;

/**
 * Created by vr on 2017-11-17.
 */

public class GridOfferAdapter extends BaseAdapter
{
    private List<Offer> offers;

    private Context context;

    private LayoutInflater inflater;

    public GridOfferAdapter(Context context, List<Offer> offers){
        this.context = context;
        this.offers = offers;

    }

    @Override
    public int getCount() {
        return this.offers.size();
    }

    @Override
    public Object getItem(int position) {
        return this.offers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View gridView = convertView;

        if(convertView == null){

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.offer_row,null);

        }



        TextView offerName = (TextView)gridView.findViewById(R.id.textViewOfferName);
        TextView price  = (TextView)gridView.findViewById(R.id.textViewOfferPrice);

        offerName.setText(offers.get(position).getName());
        price.setText(String.valueOf(offers.get(position).getPrice()));

        return gridView;


    }

    public List<Offer> offers(){

        return offers;

    }
}
