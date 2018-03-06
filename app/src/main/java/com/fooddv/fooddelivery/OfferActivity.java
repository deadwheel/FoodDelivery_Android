package com.fooddv.fooddelivery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.fooddv.fooddelivery.models.Offer;
import com.fooddv.fooddelivery.models.Response.OfferResponse;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfferActivity extends BaseActivity implements SearchView.OnQueryTextListener, BasketListener {

    private static final String TAG = "OfferActivity";

    private List<ItemListOffer> itemListOffers = new ArrayList<>();
    private Set<Offer> basket = new HashSet<>();

    private List<ItemListOffer> listOfferFromJSON;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private OfferRecyklerAdapter offerRecyklerAdapter;

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        setTitle("Oferty");
        recyclerView = (RecyclerView) findViewById(R.id.offerRecycle);
        offerRecyklerAdapter = new OfferRecyklerAdapter(this, itemListOffers);
        initRecycler();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.offerSwipeLayout);
        swipeRefreshLayout.setRefreshing(true);

        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        getOffers();
                                        swipeRefreshLayout.setRefreshing(false);

                                    }
                                }
        );


        Button order = (Button) findViewById(R.id.btGoToBasket);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                   Intent basketActivity = new Intent(OfferActivity.this, BasketActivity.class);
                   startActivity(basketActivity);

           }
        });

   }


    //Method getOffers using Retrofit to getting offers from database
    public void getOffers() {

        Call<OfferResponse> callOffer = service.offers();
        callOffer.enqueue(new Callback<OfferResponse>() {
            @Override
            public void onResponse(Call<OfferResponse> call, Response<OfferResponse> response) {

                swipeRefreshLayout.setRefreshing(true);

                if (response.isSuccessful()) {

                    final List<Offer> o = response.body().getData();

                    SharedPreferences basketShared = getApplication().getSharedPreferences("basket", Context.MODE_PRIVATE);
                    listOfferFromJSON = getItemListOffersFromJSON(basketShared.getString("products", ""));
                    itemListOffers.clear();


                    for (int i = 0; i < o.size(); i++) {
                        o.get(i).setPrice(new BigDecimal(o.get(i).getPrice()).doubleValue());
                        boolean added = false;
                        if (listOfferFromJSON != null) {

                            /*
                            if (i < listOfferFromJSON.size()) {
                                if (listOfferFromJSON.get(i) != null
                                        && listOfferFromJSON.get(i).isPurchased()
                                        ) {

                                        int index = i;

                                        if(listOfferFromJSON.get(i).getPosition() == null){

                                            index = o.indexOf(listOfferFromJSON.get(i).getOffer());
                                            if(index!=-1)
                                                listOfferFromJSON.get(i).setPosition(index);
                                            else
                                                index=i;

                                        }
                                        Toast.makeText(getApplicationContext(),String.valueOf(index)+"-"+String.valueOf(i),Toast.LENGTH_LONG).show();
                                        if(index == i) {
                                            ItemListOffer item = new ItemListOffer(true, o.get(i), i);
                                            item.getOffer().setQuantity(listOfferFromJSON.get(i).getOffer().getQuantity());
                                            itemListOffers.add(item);
                                        }

                                    continue;
                                }
                            }
                            */

                            for(ItemListOffer itemList: listOfferFromJSON){

                              int index = o.indexOf(itemList.getOffer());

                              if(i == index && itemList.isPurchased()){

                                      ItemListOffer item = new ItemListOffer(true, o.get(i), i);
                                      item.getOffer().setQuantity(itemList.getOffer().getQuantity());
                                      itemListOffers.add(item);
                                      added = true;
                                  }
                            }

                            if(added){

                                  continue;

                            }
                        }


                        itemListOffers.add(new ItemListOffer(false, o.get(i), i));
                    }


                    //offerRecyklerAdapter.refresh(itemListOffers);
                    saveBasketMapToJson(itemListOffers);

                    //adapter.notifyDataSetChanged();
                    offerRecyklerAdapter.refresh(itemListOffers);
                    offerRecyklerAdapter.notifyDataSetChanged();


                } else {


                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<OfferResponse> call, Throwable t) {
                getOffers();
            }
        });
    }

    /*
    private void makeClearBasketDialog(final String title, final String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(OfferActivity.this);

        builder.setMessage(msg)
                .setTitle(title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                clearBasket();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        MenuItem menuItem = menu.findItem(R.id.item_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public void setPurchasedItem(int position, boolean value) {
        itemListOffers.get(position).setPurchased(value);

        saveBasketMapToJson(itemListOffers);
    }

    @Override
    public void removeOfferFromBasket(Offer offer) {
        basket.remove(offer);
    }

    @Override
    public void clearBasket() {

    }

    @Override
    public void setQuantityItem(int position, int value) {

        itemListOffers.get(position).getOffer().setQuantity(value);

        saveBasketMapToJson(itemListOffers);
    }



    @Override
    public ItemListOffer getOffer(int position) {
        return itemListOffers.get(position);
    }

    @Override
    public List<ItemListOffer> getAllItemListOffer() {
        return itemListOffers;
    }



    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        ArrayList<ItemListOffer> searchList = new ArrayList<>();

        for (ItemListOffer o : itemListOffers) {

            String name = o.getOffer().getName().toLowerCase();

            if (name.contains(newText)) {
                searchList.add(o);
            }

        }

        offerRecyklerAdapter.setFilter(searchList);

        return true;
    }

  /*  private int countPurchased(List<ItemListOffer> item) {

        int i = 0;
        for (ItemListOffer it : item) {

            if (it.isPurchased()) {

                i++;

            }
        }
        return i;

    }
*/
    @Override
    protected void onPostResume() {

        super.onPostResume();

        SharedPreferences basketShared = getApplication().getSharedPreferences("basket", Context.MODE_PRIVATE);

        if (basketShared != null) {
            String s = basketShared.getString("products", "");


            if (!s.equals("")) {

                listOfferFromJSON = getItemListOffersFromJSON(s);

            }

            if (listOfferFromJSON != null && listOfferFromJSON.size() > 0) {

                itemListOffers = listOfferFromJSON;

                offerRecyklerAdapter.refresh(itemListOffers);
                offerRecyklerAdapter.notifyDataSetChanged();


            }
        }

    }

    private void initRecycler() {


        recyclerView.setAdapter(offerRecyklerAdapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, 1));


    }

    private void saveBasketMapToJson(List<ItemListOffer> itemListOffer) {

        Moshi moshi = new Moshi.Builder().build();

        Type type = Types.newParameterizedType(List.class, ItemListOffer.class);
        JsonAdapter<List<ItemListOffer>> jsonAdapter = moshi.adapter(type);

        String json = jsonAdapter.toJson(itemListOffer);
        SharedPreferences basketShared = getApplication().getSharedPreferences("basket", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = basketShared.edit();

        editor.remove("products").apply();
        editor.putString("products", json).commit();


    }

    private List<ItemListOffer> getItemListOffersFromJSON(String json) {

        if (!json.equals("")) {
            List<ItemListOffer> listFromJson = new ArrayList<>();

            Moshi moshi = new Moshi.Builder().build();
            Type type = Types.newParameterizedType(List.class, ItemListOffer.class);
            JsonAdapter<List<ItemListOffer>> jsonAdapter = moshi.adapter(type);
            try {
                listFromJson = jsonAdapter.fromJson(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return listFromJson;
        } else
            return null;
    }
}