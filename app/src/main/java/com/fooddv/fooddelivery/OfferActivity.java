package com.fooddv.fooddelivery;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fooddv.fooddelivery.models.Offer;
import com.fooddv.fooddelivery.models.Response.OfferResponse;
import com.fooddv.fooddelivery.models.Response.TestResponse;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalService;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfferActivity extends BaseActivity implements SearchView.OnQueryTextListener, BasketListener {

    private static final String TAG = "OfferActivity";

    private Call<TestResponse> call;
    private Call<OfferResponse> callOffer;

    private List<Offer> offers = new ArrayList<Offer>();
    private List<ItemListOffer> itemListOffers = new ArrayList<>();
    private List<PayPalItem> itemPaypal = new ArrayList<PayPalItem>();
    private Set<Offer> basket = new HashSet<Offer>();
    private Map<String, Object> detailed = new HashMap<String, Object>();
    private Set<Offer> tmpSet = new HashSet<Offer>();
    private BasketActivity basketActivity;
    private List<PayPalItem> productsInCart = new ArrayList<PayPalItem>();
    private Handler mHandler;
     private SharedPreferences basketShared;
    private Map<Integer, Offer> basketItemMap = new HashMap<>();
    private List<ItemListOffer> listOfferFromJSON;

    private RecyclerView recyclerView;
    private OfferRecyklerAdapter offerRecyklerAdapter;

    private static final int REQUEST_CODE_PAYMENT = 1;

    private static PayPalConfiguration paypalConfig = new PayPalConfiguration()
            .environment(Config.PAYPAL_ENVIRONMENT).clientId(
                    Config.PAYPAL_CLIENT_ID);


    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        setTitle("Oferty");
        recyclerView = (RecyclerView) findViewById(R.id.offerRecycle);
        offerRecyklerAdapter = new OfferRecyklerAdapter(this, itemListOffers);
        initRecycler();




        Button order = (Button) findViewById(R.id.btGoToBasket);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Call<OfferResponse> call;
                detailed.put("order_details", basket);

                if (countPurchased(itemListOffers) > 0) {
                   /*OrderDialog dialog =  new OrderDialog();
                    dialog.newInstance(OfferActivity.this).show(getSupportFragmentManager(),"");
              */
                   Intent basketActivity = new Intent(OfferActivity.this, BasketActivity.class);
                    //basketActivity.putExtra("basket", (Serializable) getBasket());
                   // basketActivity.putExtra("basketMap", (Serializable)basketItemMap);
                    basketActivity.putExtra("itemListOffer", (Serializable) itemListOffers);
                    startActivity(basketActivity);


                } else {
                    Toast.makeText(getApplicationContext(), "Musisz wybrać conajmniej 1 produkt", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
        startService(intent);

        Intent basketAcrivity = getIntent();

        if (basketActivity != null) {

            String msg = basketAcrivity.getStringExtra("msg");


            if (!msg.equals(" ")) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

                builder.setTitle("Potwierdzenie");
                builder.setMessage(msg);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        }



            getOffers();


    }


    //Method getOffers using Retrofit to getting offers from database
    public void getOffers() {

        callOffer = service.offers();
        final OfferActivity root = this;
        callOffer.enqueue(new Callback<OfferResponse>() {
            @Override
            public void onResponse(Call<OfferResponse> call, Response<OfferResponse> response) {

                if (response.isSuccessful()) {


                    final OfferResponse rsp = response.body();
                    final List<Offer> o = response.body().getData();

                    SharedPreferences basketShared = getApplication().getSharedPreferences("basket", Context.MODE_PRIVATE);
                   listOfferFromJSON = getItemListOffersFromJSON(basketShared.getString("products",""));
                    itemListOffers.clear();


                        for (int i = 0; i < o.size(); i++) {
                            o.get(i).setPrice(new BigDecimal(o.get(i).getPrice()).doubleValue());
                            if (listOfferFromJSON != null) {
                                Toast.makeText(getApplicationContext(),"Nie null",Toast.LENGTH_SHORT).show();
                                if (i < listOfferFromJSON.size()) {
                                    if (listOfferFromJSON.get(i) != null && listOfferFromJSON.get(i).isPurchased()) {

                                        ItemListOffer item = new ItemListOffer(true, o.get(i), i);
                                        item.getOffer().setQuantity(listOfferFromJSON.get(i).getOffer().getQuantity());
                                        itemListOffers.add(item);

                                        continue;
                                    }
                                }
                            }

                            itemListOffers.add(new ItemListOffer(false, o.get(i), i));

                        }

                        //offerRecyklerAdapter.refresh(itemListOffers);
                     saveBasketMapToJson(itemListOffers);
                    //adapter.notifyDataSetChanged();
                    offerRecyklerAdapter.refresh(getItemListOffersFromJSON(basketShared.getString("products","")));
                    offerRecyklerAdapter.notifyDataSetChanged();




                } else {


                }
            }

            @Override
            public void onFailure(Call<OfferResponse> call, Throwable t) {
                getOffers();
            }
        });
    }




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
    public void addOfferToBasket(Offer offer) {
        if (offer.getQuantity() > 0) {

           // itemListOffers.r(new ItemListOffer(true, offer));

            Toast.makeText(getApplicationContext(), "Dodano do koszyka:" + offer.getName(), Toast.LENGTH_SHORT).show();
        }
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
    public void putOfferToBasket(int position, Offer offer) {

        basketItemMap.put(position, offer);

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
    public List<Offer> getBasket() {
        List<Offer> o = new ArrayList<>();
        for (Iterator<Offer> it = basket.iterator(); it.hasNext(); ) {
            Offer offer = it.next();
            o.add(offer);
        }

        return o;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        ArrayList<ItemListOffer> searchList = new ArrayList<>();
        int licznik = 0;
        for (ItemListOffer o : itemListOffers) {

            String name = o.getOffer().getName().toLowerCase();

            if (name.contains(newText)) {
                //Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();
                searchList.add(o);
            }

        }

        offerRecyklerAdapter.setFilter(searchList);

        return true;
    }

    private int countPurchased(List<ItemListOffer> item){

        int i=0;
        for(ItemListOffer it:item){

            if(it.isPurchased()){

                i++;

            }
        }
        return i;

    }

    @Override
    protected void onPostResume() {

        super.onPostResume();

        SharedPreferences basketShared = getApplication().getSharedPreferences("basket", Context.MODE_PRIVATE);

        if(basketShared != null) {
            String s = basketShared.getString("products", "");



            if (s != null && !s.equals("")) {


                listOfferFromJSON = getItemListOffersFromJSON(s);
                Toast.makeText(getApplicationContext(),"s",Toast.LENGTH_SHORT).show();

            }

            if (listOfferFromJSON != null && listOfferFromJSON.size()>0) {

                itemListOffers = listOfferFromJSON;



                //initRecycler();
                offerRecyklerAdapter.refresh(itemListOffers);
                offerRecyklerAdapter.notifyDataSetChanged();



            }
        }

    }

    private void initRecycler(){


        recyclerView.setAdapter(offerRecyklerAdapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, 1));


    }

    private void saveBasketMapToJson(List<ItemListOffer> itemListOffer){

        Moshi moshi = new Moshi.Builder().build();

        Type type = Types.newParameterizedType(List.class,ItemListOffer.class);
        JsonAdapter<List<ItemListOffer>> jsonAdapter = moshi.adapter(type);

       String json = jsonAdapter.toJson(itemListOffer);
       SharedPreferences basketShared = getApplication().getSharedPreferences("basket", Context.MODE_PRIVATE);
       SharedPreferences.Editor editor = basketShared.edit();

      editor.remove("products").commit();
      editor.putString("products",json).commit();


    }

    private List<ItemListOffer> getItemListOffersFromJSON(String json){

        if(!json.equals("")) {
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
        }else
            return null;
    }
}