package com.fooddv.fooddelivery;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.fooddv.fooddelivery.models.Response.DriverOrder;
import com.fooddv.fooddelivery.models.Response.DriverOrderOffer;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Denis on 2017-12-02.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<DriverOrder> listDataHeader;
    private HashMap<DriverOrder,List<DriverOrderOffer>> listHashMap;

    public ExpandableListAdapter(Context context, List<DriverOrder> listDataHeader, HashMap<DriverOrder, List<DriverOrderOffer>> listHashMap) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listHashMap = listHashMap;
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return listHashMap.get(listDataHeader.get(i)).size();
    }

    @Override
    public DriverOrder getGroup(int i) {
        return listDataHeader.get(i);
    }

    @Override
    public DriverOrderOffer getChild(int i, int i1) {
        return listHashMap.get(listDataHeader.get(i)).get(i1); // i = Group Item , i1 = ChildItem
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        String headerTitle = getGroup(i).getAddress();
        String price = String.valueOf(getGroup(i).getPrice());
        if(view == null)
        {
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_group,null);
        }
        TextView lblListHeader = (TextView)view.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        TextView price_l = (TextView)view.findViewById(R.id.price);
        price_l.setText(price);
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final String childText = getChild(i,i1).getOffer_det().getName();
        final String quantinty = String.valueOf(getChild(i,i1).getQuantity());
        if(view == null)
        {
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item,null);
        }

        TextView txtListChild = (TextView)view.findViewById(R.id.lblListItem);
        txtListChild.setText(childText);

        TextView txtListChildPieces = (TextView)view.findViewById(R.id.pieces);
        txtListChildPieces.setText(quantinty);

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

}
