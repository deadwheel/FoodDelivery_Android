package com.fooddv.fooddelivery.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.fooddv.fooddelivery.BasketListener;
import com.fooddv.fooddelivery.Config;
import com.fooddv.fooddelivery.OfferRecyklerAdapter;
import com.fooddv.fooddelivery.R;
import com.fooddv.fooddelivery.models.Offer;
import com.fooddv.fooddelivery.models.Response.OfferResponse;
import com.fooddv.fooddelivery.network.ApiService;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 26.11.17.
 */

@SuppressLint("ValidFragment")
public class OfferFragment extends Fragment implements BasketListener{

    private Context context;
    private ApiService apiService;
    private Map<String, Object> detailed = new HashMap<String, Object>();
    private Set<Offer> basket = new HashSet<Offer>();

    private OfferRecyklerAdapter offerRecyklerAdapter;
    private List<Offer> offers=new ArrayList<Offer>();
    private static PayPalConfiguration paypalConfig = new PayPalConfiguration()
            .environment(Config.PAYPAL_ENVIRONMENT).clientId(
                    Config.PAYPAL_CLIENT_ID);

    @SuppressLint("ValidFragment")
    public OfferFragment(Context context, ApiService apiService) {
        super();
        this.context = context;
        this.apiService=apiService;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Intent intent = new Intent(getActivity(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
        getActivity().startService(intent);
        getOffers();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_user,null);
        RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.offerRecycle);
        offerRecyklerAdapter=new OfferRecyklerAdapter(context,offers,this);
        recyclerView.setAdapter(offerRecyklerAdapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, 1));
        return v;

    }

    public void getOffers(){

        Call<OfferResponse>callOffer =  apiService.offers();

        callOffer.enqueue(new Callback<OfferResponse>() {
            @Override
            public void onResponse(Call<OfferResponse> call, Response<OfferResponse> response) {

                if(response.isSuccessful()){

                    final OfferResponse rsp = response.body();
                    final List<Offer> o = response.body().getData();

                    for(int i=0;i<o.size();i++){
                        o.get(i).setPrice(new BigDecimal(o.get(i).getPrice()).doubleValue());
                        offers.add(o.get(i));
                    }
                    //adapter.notifyDataSetChanged();
                    offerRecyklerAdapter.notifyDataSetChanged();

                    Button order = (Button)getActivity().findViewById(R.id.brOrder);
                    order.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Call<OfferResponse> call;
                            detailed.put("order_details", basket);

                            if(basket.size() > 0){

                                OrderDialog dialog =  new OrderDialog();
                                dialog.show(getActivity().getSupportFragmentManager(),"");

                            }else {
                                Toast.makeText(context,"Musisz wybrać conajmniej 1 produkt",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else {


                }
            }

            @Override
            public void onFailure(Call<OfferResponse> call, Throwable t) {
                getOffers();
            }
        });
    }

    @Override
    public void addOfferToBasket(Offer offer) {

    }

    @Override
    public void removeOfferFromBasket(Offer offer) {

    }

    @Override
    public void clearBasket() {

    }

    @Override
    public List<Offer> getBasket() {
        return null;
    }
}
