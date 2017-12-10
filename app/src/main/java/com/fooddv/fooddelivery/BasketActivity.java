package com.fooddv.fooddelivery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fooddv.fooddelivery.fragments.OrderDialog;
import com.fooddv.fooddelivery.models.Adress;
import com.fooddv.fooddelivery.models.Offer;
import com.fooddv.fooddelivery.models.Response.TestResponse;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *
 * Created by Damian Rakowski on 27.11.17.
 *
 * BasketActivity is class that inherits from BaseActivity and implements BasketListener and it is providing Activity that shows list of the products ordered by user.
 *
 * Added other RecyclerView suitable for basket's view,  modyfied on 28.11.2017
 *
 */

public class BasketActivity extends BaseActivity implements BasketListener {

    private static final String TAG = "BasketActivity";
    private List<Offer> basket = new ArrayList<Offer>();
    private BasketRecyklerAdapter basketRecyklerAdapter;
    private List<ItemListOffer> itemListOffer;
    private static final int REQUEST_CODE_PAYMENT = 1;
    private Call<TestResponse> call;
    private Map<Integer, Offer> basketItemMap= new HashMap<>();
    private Map<String, Object> detailed = new HashMap<String, Object>();
    private static PayPalConfiguration paypalConfig = new PayPalConfiguration()
            .environment(Config.PAYPAL_ENVIRONMENT).clientId(
                    Config.PAYPAL_CLIENT_ID);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);
        setTitle("Koszyk");
        Intent intent = getIntent();

        itemListOffer = (List<ItemListOffer>)intent.getSerializableExtra("itemListOffer");
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, ItemListOffer.class);
        JsonAdapter<List<ItemListOffer>> jsonAdapter = moshi.adapter(type);

        SharedPreferences basketShared = getApplication().getSharedPreferences("basket", Context.MODE_PRIVATE);
        if(basketShared!=null) {
            String s = basketShared.getString("products", "");

            List<ItemListOffer> tmpList = null;

            if (s != null && !s.equals("")) {
                try {

                    tmpList = jsonAdapter.fromJson(s);

                } catch (IOException e) {
                    e.printStackTrace();

                }
            }

            if (tmpList != null) {

                Toast.makeText(getApplicationContext(), "ala", Toast.LENGTH_SHORT).show();
                itemListOffer = new ArrayList<>();
                itemListOffer.addAll(tmpList);

            }

            if (itemListOffer != null) {

                for (ItemListOffer item : itemListOffer) {

                    if (item.isPurchased()) {
                        basket.add(item.getOffer());
                    }
                }

            }

            if (basket.size() > 0) {

                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.BasketRecyclerView);
                basketRecyklerAdapter = new BasketRecyklerAdapter(this, basket);

                recyclerView.setAdapter(basketRecyklerAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));

                Button orderButton = (Button) findViewById(R.id.btBasketOrder);
                final BasketListener listener = this;
                orderButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        detailed.put("order_details", basket);
                        OrderDialog orderDialog = new OrderDialog();
                        orderDialog.newInstance(listener).show(getSupportFragmentManager(), "");

                    }
                });
            } else {

                Toast.makeText(getApplicationContext(), "Koszyk pusty", Toast.LENGTH_SHORT).show();

            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {

                        Log.e(TAG, confirm.toJSONObject().toString(4));
                        Log.e(TAG, confirm.getPayment().toJSONObject()
                                .toString(4));

                        String paymentId = confirm.toJSONObject()
                                .getJSONObject("response").getString("id");

                        String payment_client = confirm.getPayment()
                                .toJSONObject().toString();

                        Log.e(TAG, "paymentId: " + paymentId
                                + ", payment_json: " + payment_client);


                        makeOrder(paymentId, payment_client);
                        basket.clear();
                        detailed.clear();
                        basketRecyklerAdapter.notifyDataSetChanged();
                        intentToOffers("Twoje zamówienie zostało zrealizowane");


                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ",
                                e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e(TAG, "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.e(TAG,
                        "An invalid Payment or PayPalConfiguration was submitted.");
            }
        }
    }

    private void intentToOffers(final String msg){

        if(basket.size() == 0){

            Intent offerActivity = new Intent(BasketActivity.this,OfferActivity.class);
            offerActivity.putExtra("msg",msg);
            startActivity(offerActivity);

        }

    }

    public void makeOrder(String paymentId, String payment_client){

       // OrderAddress address = new OrderAddress(true, new Adress("asdadsasdasd"));
        com.fooddv.fooddelivery.models.Payment pay = new com.fooddv.fooddelivery.models.Payment(paymentId,payment_client);


        detailed.put("payment_details", pay);
        Toast.makeText(getApplicationContext(),((Adress)detailed.get("order_address")).getOpt_address(),Toast.LENGTH_SHORT).show();
        call = service.orders(detailed);
        call.enqueue(new Callback<TestResponse>() {
            @Override
            public void onResponse(Call<TestResponse> call, Response<TestResponse> response) {
                  /*  Toast.makeText(getApplicationContext(), call.request().toString(), Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                    */
                if (response.isSuccessful()) {
                    Log.w(TAG, "succesfullResp " + response);
                }
            }
            @Override
            public void onFailure(Call<TestResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    /**
     * Launching PalPay payment activity to complete the payment
     * */
    public void launchPayPalPayment() {

        PayPalPayment thingsToBuy = prepareFinalCart();

        Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingsToBuy);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);

    }

    /**
     * Preparing final cart amount that needs to be sent to PayPal for payment
     * */
    private PayPalPayment prepareFinalCart() {

        PayPalItem[] items = new PayPalItem[basket.size()];
        int i = 0;
        for (Iterator<Offer> it = basket.iterator(); it.hasNext(); i++) {
            Offer offer = it.next();

            offer.setOffer_id(offer.getId());
            items[i] = new PayPalItem(offer.getName(), offer.getQuantity(), new BigDecimal(offer.getPrice()).setScale(2,BigDecimal.ROUND_HALF_UP), Config.DEFAULT_CURRENCY, String.valueOf(offer.getId()));
        }
        // Total amount
        BigDecimal subtotal = PayPalItem.getItemTotal(items);

        // If you have shipping cost, add it here
        BigDecimal shipping = new BigDecimal("0.0");

        // If you have tax, add it here
        BigDecimal tax = new BigDecimal("0.0");

        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(
                shipping, subtotal, tax);

        BigDecimal amount = subtotal.add(shipping).add(tax);

        PayPalPayment payment = new PayPalPayment(
                amount,
                Config.DEFAULT_CURRENCY,
                "Description about transaction. This will be displayed to the user.",
                Config.PAYMENT_INTENT);
        payment.items(items).paymentDetails(paymentDetails);
        // Custom field like invoice_number etc.,
        payment.custom("This is text that will be associated with the payment that the app can use.");

        return payment;
    }

    @Override
    public void addOfferToBasket(Offer offer) {

    }

    @Override
    public void setPurchasedItem(int position, boolean value) {

    }

    @Override
    public void removeOfferFromBasket(Offer offer) {

        basket.remove(offer);

        for(ItemListOffer items: itemListOffer) {

            if(items.isPurchased()){

                if(items.getOffer().equals(offer)){

                    items.setPurchased(false);

                }

            }

        }
        saveBasketMapToJson(itemListOffer);

    }

    @Override
    public void clearBasket() {

    }

    @Override
    public void setQuantityItem(int position, int value) {
        basket.get(position).setQuantity(value);

        for(ItemListOffer items: itemListOffer) {

            if(items.isPurchased()){

                if(items.getOffer().equals(basket.get(position))){

                    items.getOffer().setQuantity(value);

                }

            }

        }

        saveBasketMapToJson(itemListOffer);

        basketRecyklerAdapter.notifyItemChanged(position);
    }

    @Override
    public void putOfferToBasket(int position, Offer offer) {

    }

    @Override
    public ItemListOffer getOffer(int position) {
        return itemListOffer.get(position);

    }

    @Override
    public List<ItemListOffer> getAllItemListOffer() {
        return null;
    }

    @Override
    public List<Offer> getBasket() {
        return null;
    }


    private void saveBasketMapToJson(List<ItemListOffer> itemListOffer){

        Moshi moshi = new Moshi.Builder().build();

        Type type = Types.newParameterizedType(List.class,ItemListOffer.class);
        JsonAdapter<List<ItemListOffer>> jsonAdapter = moshi.adapter(type);

        String json = jsonAdapter.toJson(itemListOffer);

        SharedPreferences basketShared = getApplication().getSharedPreferences("basket",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = basketShared.edit();
        editor.remove("products").commit();
        editor.putString("products",json);
        editor.commit();

    }

    public void putDetailed(String key, Object value){

        detailed.put(key,value);
    }
}
