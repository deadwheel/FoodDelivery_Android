package com.fooddv.fooddelivery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.fooddv.fooddelivery.fragments.OfferItemDialog;
import com.fooddv.fooddelivery.models.Adress;
import com.fooddv.fooddelivery.models.Offer;
import com.fooddv.fooddelivery.models.OrderAddress;
import com.fooddv.fooddelivery.models.Response.OfferResponse;
import com.fooddv.fooddelivery.network.ApiService;
import com.fooddv.fooddelivery.network.RetrofitBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.support.v4.app.DialogFragment;
public class UserActivity extends AppCompatActivity {

    private static final String TAG = "UserActivity";
    ApiService service;
    TokenManager tokenManager;
    Call<OfferResponse> call;
    int a=0;
    String s;
    Call<OfferResponse> callOffer;
    GridView gridView;
    GridOfferAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);


       tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

       if(tokenManager.getToken() == null){
            startActivity(new Intent(UserActivity.this, LoginActivity.class));
            finish();
        }

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        getOffers();


    }


       public void getOffers(){

        callOffer = service.offers();
        callOffer.enqueue(new Callback<OfferResponse>() {
            @Override
            public void onResponse(Call<OfferResponse> call, Response<OfferResponse> response) {

                if(response.isSuccessful()){

                    final OfferResponse rsp = response.body();
                    final List<Offer> offers = response.body().getData();
                    a=0;

                    UserActivity.this.gridView = (GridView)findViewById(R.id.gridOffer);
                    gridView.setChoiceMode(gridView.CHOICE_MODE_MULTIPLE);
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            View gridView = view;
                            new OfferItemDialog().newInstance(position).show(getSupportFragmentManager(),"Ilosc");
                            TextView quantity = (TextView)gridView.findViewById(R.id.textViewOfferQuantity);
                           if(s!=null) {
                               quantity.setText(s);
                           }
                        }
                    });

                    adapter = new GridOfferAdapter(UserActivity.this,offers);

                    gridView.setAdapter(adapter);

                    Button order = (Button)findViewById(R.id.brOrder);
                    order.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final SparseBooleanArray checked = gridView.getCheckedItemPositions();

                            List<Offer> bucket = new ArrayList<>();

                                for (int i = 0; i < gridView.getCount(); i++) {

                                    if (checked.get(i)) {
                                        Integer quantity = offers.get(i).getQuantity();

                                        if (quantity > 0 && quantity != null) {

                                            offers.get(i).setOffer_id(offers.get(i).getId());
                                            bucket.add(offers.get(i));
                                        }

                                    }
                                }


                            Call<OfferResponse> call;

                            Map<String, Object> map = new HashMap<String, Object>();

                             map.put("order_details", bucket);

                            Toast.makeText(getApplicationContext(),String.valueOf(bucket.size()),Toast.LENGTH_SHORT).show();

                            if(bucket.size() > 0){
                                  checked.clear();
                                  bucket.clear();
                                  makeOrder(map);

                        }else {

                                Toast.makeText(getApplicationContext(),"Musisz wybrać conajmniej 1 produkt",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

               }else {

                    tokenManager.deleteToken();
                }
            }

            @Override
            public void onFailure(Call<OfferResponse> call, Throwable t) {

            }

        });

       }

        public void modyQuantity(int position,int quantity){

            Offer offer = ((Offer)this.gridView.getItemAtPosition(position));
            if(offer!=null)
                offer.setQuantity(quantity);

       }

        public void makeOrder(Map<String, Object> map){

            OrderAddress address = new OrderAddress(true, new Adress("asdadsasdasd"));
            map.put("order_address", address);

            call = service.orders(map);
            call.enqueue(new Callback<OfferResponse>() {
                @Override
                public void onResponse(Call<OfferResponse> call, Response<OfferResponse> response) {



                    Toast.makeText(getApplicationContext(), call.request().toString(), Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();

                    if (response.isSuccessful()) {
                        Log.w(TAG, "succesfullResp " + response);

                    }

                }

                @Override
                public void onFailure(Call<OfferResponse> call, Throwable t) {
                    Log.w(TAG, "onFailure: " + t.getMessage());

                }
            });


        }


}
