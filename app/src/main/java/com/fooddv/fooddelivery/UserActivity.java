package com.fooddv.fooddelivery;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
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

public class UserActivity extends AppCompatActivity implements  OrderDialog.ClearBasketAdapter, OfferRecyklerAdapter.BasketAdapterListener {

    private static final String TAG = "UserActivity";

    private ApiService service;
    private TokenManager tokenManager;
    private Call<TestResponse> call;
    private Call<OfferResponse> callOffer;
    private Call<PaymentResponse> paymentService;
    private GridView gridView;

    private View v;

    private List<Offer> offers = new ArrayList<Offer>();
    private List<PayPalItem> itemPaypal = new ArrayList<PayPalItem>();
    private Set<Offer> basket = new HashSet<Offer>();
    private Map<String, Object> detailed = new HashMap<String, Object>();
    private Set<Offer> tmpSet = new HashSet<Offer>();

    private List<PayPalItem> productsInCart = new ArrayList<PayPalItem>();

    private RecyclerView recyclerView;
    private OfferRecyklerAdapter offerRecyklerAdapter;
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static PayPalConfiguration paypalConfig = new PayPalConfiguration()
            .environment(Config.PAYPAL_ENVIRONMENT).clientId(
                    Config.PAYPAL_CLIENT_ID);

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);


       tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

       if(tokenManager.getToken() == null){
            startActivity(new Intent(UserActivity.this, LoginActivity.class));
            finish();
        }

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        recyclerView = (RecyclerView)findViewById(R.id.offerRecycle);
        offerRecyklerAdapter = new OfferRecyklerAdapter(this,offers,this);
        recyclerView.setAdapter(offerRecyklerAdapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, 1));

       // gridView = (GridView)findViewById(R.id.gridOffer);
       // gridView.setChoiceMode(gridView.CHOICE_MODE_MULTIPLE);
        /*gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View gridView = view;
                new OfferItemDialog().newInstance(position).show(getSupportFragmentManager(),"Ilosc");
                v = gridView;
            }
        });
*/




        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
        startService(intent);

        new DownloadFilesTask().execute();

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

    public void getOffers(){

        callOffer =  service.offers();
        final UserActivity root = this;
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

                    Button order = (Button)findViewById(R.id.brOrder);
                    order.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Call<OfferResponse> call;
                            detailed.put("order_details", tmpSet);

                            if(basket.size() > 0){

                                OrderDialog dialog =  new OrderDialog();
                                dialog.newInstance(root).show(getSupportFragmentManager(),"");

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
                  getOffers();
            }
        });
       }

         public void makeOrder(String paymentId, String payment_client){

            OrderAddress address = new OrderAddress(true, new Adress("asdadsasdasd"));
            Payment pay = new Payment(paymentId,payment_client);

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

        Intent intent = new Intent(UserActivity.this, PaymentActivity.class);

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
    public void clear() {

        for (Iterator<Offer> it = basket.iterator(); it.hasNext();) {
            Offer offer = it.next();
            tmpSet.add(offer);
        }

        basket.clear();
    }

    @Override
    public void addOfferToBasket(Offer offer) {
        if(offer.getQuantity() > 0) {
            basket.add(offer);
            Toast.makeText(getApplicationContext(), "Dodano do koszyka:" + offer.getName(), Toast.LENGTH_SHORT).show();
        }
      }

    private class DownloadFilesTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... urls) {
            getOffers();
            return null;
        }

        protected void onProgressUpdate(Void... progress) {

        }

        protected void onPostExecute(Void result) {
            getOffers();

        }
    }

}
