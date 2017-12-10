package com.fooddv.fooddelivery.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fooddv.fooddelivery.BasketActivity;
import com.fooddv.fooddelivery.BasketListener;
import com.fooddv.fooddelivery.Config;
import com.fooddv.fooddelivery.PlacesAutoCompleteAdapter;
import com.fooddv.fooddelivery.R;
import com.fooddv.fooddelivery.RecyclerItemClickListener;
import com.fooddv.fooddelivery.models.Adress;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by vr on 2017-11-19.
 */

public class OrderDialog extends DialogFragment implements  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    protected GoogleApiClient mGoogleApiClient;

    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(
            new LatLng(-0, 0), new LatLng(0, 0));

    private EditText mAutocompleteView;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private boolean is_optional_address;
    ImageView delete;

    public static OrderDialog newInstance(BasketListener listener) {

        OrderDialog f = new OrderDialog();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putSerializable("listener",listener);

        f.setArguments(args);

        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        buildGoogleApiClient();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();




        final View myView = inflater.inflate(R.layout.activity_search, null);
        BasketListener listener = (BasketListener) getArguments().getSerializable("listener");

        final CheckBox checkBox = (CheckBox)myView.findViewById(R.id.checkAddressFromProfile);

        mAutocompleteView = (EditText)myView.findViewById(R.id.autocomplete_places);
        mAutocompleteView.setEnabled(false);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkBox.isChecked()) {

                    is_optional_address = false;
                    mAutocompleteView.setEnabled(false);

                }else {
                    is_optional_address = true;
                    mAutocompleteView.setEnabled(true);
                }
            }
        });

        delete=(ImageView)myView.findViewById(R.id.cross);

        mAutoCompleteAdapter =  new PlacesAutoCompleteAdapter(getActivity(), R.layout.searchview_adapter,
        mGoogleApiClient, BOUNDS_INDIA, null);


        mRecyclerView=(RecyclerView)myView.findViewById(R.id.recyclerView);
        mLinearLayoutManager=new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAutoCompleteAdapter);
        delete.setOnClickListener(this);
        mAutocompleteView.addTextChangedListener(new TextWatcher() {

            @SuppressLint("LongLogTag")
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
                    mAutoCompleteAdapter.getFilter().filter(s.toString());
                }else if(!mGoogleApiClient.isConnected()){
                    Toast.makeText(getActivity(), Config.API_NOT_CONNECTED,Toast.LENGTH_SHORT).show();
                    Log.e(Config.PlacesTag,Config.API_NOT_CONNECTED);
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        });

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);
                        final String placeId = String.valueOf(item.placeId);
                        Log.i("TAG", "Autocomplete item selected: " + item.description);
                        /*
                             Issue a request to the Places Geo Data API to retrieve a Place object with additional details about the place.
                         */

                        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                                .getPlaceById(mGoogleApiClient, placeId);
                        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                            @Override
                            public void onResult(PlaceBuffer places) {
                                if(places.getCount()==1){
                                    //Do the things here on Click.....

                                    mAutocompleteView.setText(places.getAttributions());
                                   // Toast.makeText(getActivity(),String.valueOf(places.get(0).getLatLng()),Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(getActivity(),Config.SOMETHING_WENT_WRONG,Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        Log.i("TAG", "Clicked: " + item.description);
                        Log.i("TAG", "Called getPlaceById to get Place details for " + item.placeId);
                    }
                })
        );
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setTitle("Zamawiam:");

        builder.setView(myView);
        // Add action buttons
        builder.setPositiveButton(R.string.Zaplac, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                String adr = mAutocompleteView.getText().toString();

                if (!adr.equals("")){
                     String address = "";

                    if(is_optional_address)
                        address = adr;

                     Adress a = new Adress(is_optional_address, address);

                    ((BasketActivity)getActivity()).putDetailed("order_address",a);
                    ((BasketActivity) getActivity()).launchPayPalPayment();
            }else {
                    Toast.makeText(getActivity(),"Musisz podać poprawny adres",Toast.LENGTH_SHORT).show();
                }
                //clear.clear();

            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        OrderDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v("Google API Callback", "Connection Done");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("Google API Callback", "Connection Suspended");
        Log.v("Code", String.valueOf(i));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("Google API Callback","Connection Failed");
        Log.v("Error Code", String.valueOf(connectionResult.getErrorCode()));
        Toast.makeText(getActivity(), Config.API_NOT_CONNECTED,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        if(view==delete){
            mAutocompleteView.setText("");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()){
            Log.v("Google API","Connecting");
            mGoogleApiClient.connect();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if(mGoogleApiClient.isConnected()){
            Log.v("Google API","Dis-Connecting");
            mGoogleApiClient.disconnect();
        }
    }
}
