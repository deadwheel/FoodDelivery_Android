package com.fooddv.fooddelivery;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fooddv.fooddelivery.fragments.OrderDialog;
import com.fooddv.fooddelivery.models.*;
import com.fooddv.fooddelivery.models.Response.OfferResponse;
import com.fooddv.fooddelivery.models.Response.TestResponse;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

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

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.fooddv.fooddelivery.fragments.OfferItemDialog;
import com.fooddv.fooddelivery.fragments.OrderDialog;
import com.fooddv.fooddelivery.models.*;
import com.fooddv.fooddelivery.models.Payment;
import com.fooddv.fooddelivery.models.Response.OfferResponse;
import com.fooddv.fooddelivery.models.Response.PaymentResponse;
import com.fooddv.fooddelivery.models.Response.TestResponse;
import com.fooddv.fooddelivery.network.ApiService;
import com.fooddv.fooddelivery.network.RetrofitBuilder;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;

import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.support.v4.app.DialogFragment;

import org.json.JSONException;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class OfferActivity extends BaseActivity implements BasketListener {

    private static final String TAG = "OfferActivity";


    private Call<TestResponse> call;
    private Call<OfferResponse> callOffer;

    private List<Offer> offers = new ArrayList<Offer>();
    private List<PayPalItem> itemPaypal = new ArrayList<PayPalItem>();
    private Set<Offer> basket = new HashSet<Offer>();
    private Map<String, Object> detailed = new HashMap<String, Object>();
    private Set<Offer> tmpSet = new HashSet<Offer>();

    private List<PayPalItem> productsInCart = new ArrayList<PayPalItem>();
    private Handler mHandler;
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

        recyclerView = (RecyclerView)findViewById(R.id.offerRecycle);
        offerRecyklerAdapter = new OfferRecyklerAdapter(this,offers,this);
        recyclerView.setAdapter(offerRecyklerAdapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, 1));

        Button btBasketClear = (Button)findViewById(R.id.btBasketClear);
        btBasketClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(basket.size() > 0) {
                    makeClearBasketDialog("Czyszczenie koszyka", "Czy chcesz usunać wszystkie:" + String.valueOf(basket.size()) + " z koszyka?");
                }else
                    Toast.makeText(getApplicationContext(),"Koszyk jest pusty",Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
        startService(intent);

        this.mHandler = new Handler();

        this.mHandler.postDelayed(m_Runnable,1000000);

        getOffers();
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
                        tmpSet.clear();
                        offerRecyklerAdapter.notifyDataSetChanged();


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

    //Method getOffers using Retrofit to getting offers from database
    public void getOffers(){

        callOffer =  service.offers();
        final OfferActivity root = this;
        callOffer.enqueue(new Callback<OfferResponse>() {
            @Override
            public void onResponse(Call<OfferResponse> call, Response<OfferResponse> response) {

                if(response.isSuccessful()){

                    offers.clear();
                    final OfferResponse rsp = response.body();
                    final List<Offer> o = response.body().getData();

                    for(int i=0;i<o.size();i++){
                        o.get(i).setPrice(new BigDecimal(o.get(i).getPrice()).doubleValue());
                        offers.add(o.get(i));
                    }
                    //adapter.notifyDataSetChanged();
                    offerRecyklerAdapter.notifyDataSetChanged();

                    Button order = (Button)findViewById(R.id.brOrder);
                    order.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Call<OfferResponse> call;
                            detailed.put("order_details", basket);

                            if(basket.size() > 0){

                                OrderDialog dialog =  new OrderDialog();
                                OrderDialog.newInstance(OfferActivity.this).show(getSupportFragmentManager(),"");

                            }else {
                                Toast.makeText(getApplicationContext(),"Musisz wybrać conajmniej 1 produkt",Toast.LENGTH_SHORT).show();
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

    public void makeOrder(String paymentId, String payment_client){

        OrderAddress address = new OrderAddress(true, new Adress("asdadsasdasd"));
        com.fooddv.fooddelivery.models.Payment pay = new com.fooddv.fooddelivery.models.Payment(paymentId,payment_client);

        detailed.put("order_address", address);
        detailed.put("payment_details", pay);

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

    private void makeClearBasketDialog(final String title, final String msg){

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

    private final Runnable m_Runnable = new Runnable()
    {
        public void run()

        {

            getOffers();

            OfferActivity.this.mHandler.postDelayed(m_Runnable, 100000);
        }

    };//runnable

    @Override
    public void addOfferToBasket(Offer offer) {
        if(offer.getQuantity() > 0) {
            basket.add(offer);
            Toast.makeText(getApplicationContext(), "Dodano do koszyka:" + offer.getName(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void removeOfferFromBasket(Offer offer) {
        basket.remove(offer);
    }


    @Override
    public void clearBasket() {
        basket.clear();
    }


    @Override
    public List<Offer> getBasket() {
        List<Offer> o = new ArrayList<>();
        for (Iterator<Offer> it = basket.iterator(); it.hasNext();) {
            Offer offer = it.next();
            o.add(offer);
        }

        return o;
    }


}
