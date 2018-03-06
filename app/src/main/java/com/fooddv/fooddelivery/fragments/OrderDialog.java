package com.fooddv.fooddelivery.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.fooddv.fooddelivery.BasketActivity;
import com.fooddv.fooddelivery.BasketListener;
import com.fooddv.fooddelivery.LoginActivity;
import com.fooddv.fooddelivery.R;
import com.fooddv.fooddelivery.TokenManager;
import com.fooddv.fooddelivery.models.AddressContener;
import com.fooddv.fooddelivery.models.Adress;
import com.fooddv.fooddelivery.models.Response.ProfileResponse;
import com.fooddv.fooddelivery.network.ApiService;
import com.fooddv.fooddelivery.network.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by vr on 2017-11-19.
 */

public class OrderDialog extends DialogFragment{


    private EditText mAutocompleteView;
    private RecyclerView mRecyclerView;
    private boolean is_optional_address;
    private Call<ProfileResponse> getProfileCall;
    protected ApiService service;
    protected TokenManager tokenManager;
    private AwesomeValidation validator;
    private EditText firstName;
    private EditText lastName;
    private EditText address2;
    private EditText postCode;
    private EditText city;
    private EditText phoneNumber;
    private TextInputLayout til_phonenumber;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        tokenManager = TokenManager.getInstance(getContext().getSharedPreferences("prefs", MODE_PRIVATE));

        if(tokenManager.getToken() == null){
            startActivity(new Intent(getContext(), LoginActivity.class));
          }


        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        getProfile();

        final View myView = inflater.inflate(R.layout.order_dialog, null);



        firstName = (EditText)myView.findViewById(R.id.editTextProfileFirstName);
        lastName = (EditText)myView.findViewById(R.id.editTextProfileLastName);
        address2 = (EditText)myView.findViewById(R.id.editTextProfileAddress);
        postCode = (EditText)myView.findViewById(R.id.editTextProfilePostCode);
        city = (EditText)myView.findViewById(R.id.editTextProfileCity);
        phoneNumber = (EditText)myView.findViewById(R.id.editTextProfilePhoneNumber);

        til_phonenumber = (TextInputLayout)myView.findViewById(R.id.til_phonenumber);

        final AddressContener addressContener = new AddressContener();

        addressContener.setFirstName(firstName.getText().toString());
        addressContener.setLastName(lastName.getText().toString());
        addressContener.setPostCode(postCode.getText().toString());
        addressContener.setAddress2(address2.getText().toString());
        addressContener.setCity(city.getText().toString());
        addressContener.setPhoneNumber(phoneNumber.getText().toString());

        BasketListener listener = (BasketListener) getArguments().getSerializable("listener");

        final LinearLayout linearLayout = (LinearLayout)myView.findViewById(R.id.userDetailsInOrder);
        linearLayout.setVisibility(View.GONE);

        final CheckBox checkBox = (CheckBox)myView.findViewById(R.id.checkBoxProfileIS);
        checkBox.setChecked(true);
        is_optional_address = true;
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkBox.isChecked()) {

                    is_optional_address = true;
                    linearLayout.setVisibility(View.GONE);


                }else {
                    is_optional_address = false;
                    linearLayout.setVisibility(View.VISIBLE);

                }

            }
        });

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setTitle("Zamawiam:");

        builder.setView(myView);
        // Add action buttons


        builder.setPositiveButton(R.string.Zaplac, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                String adr = "jjjjjjjjjg";

                if (!adr.equals("")){
                     String address = city.getText().toString()+" "+address2.getText().toString();

                    if(is_optional_address)
                        address = adr;


                    Adress a = new Adress(is_optional_address, address);

                    BasketActivity basketActivity = (BasketActivity)getActivity();
                    basketActivity.putDetailed("order_address",a);

                    if(firstName.getText().length() > 0
                       && lastName.getText().length() > 0
                       && address2.getText().length() > 0
                       && postCode.getText().length() > 0
                       && city.getText().length() > 0
                       && phoneNumber.getText().length() > 0

                            ) {
                        ((BasketActivity) getActivity()).launchPayPalPayment();
                    }else {
                        Toast.makeText(getActivity(),"Dane adresowe są niekompletne",Toast.LENGTH_SHORT).show();
                    }
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


    private void getProfile(){

        getProfileCall = service.getProfile();
        if(getProfileCall!=null) {
            getProfileCall.enqueue(new Callback<ProfileResponse>() {
                @Override
                public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {

                    try {
                        firstName.setText(response.body().getData().get(0).getFirstName());
                        lastName.setText(response.body().getData().get(0).getLastName());
                        address2.setText(response.body().getData().get(0).getAddress());
                        postCode.setText(response.body().getData().get(0).getPostCode());
                        city.setText(response.body().getData().get(0).getCity());
                        phoneNumber.setText(response.body().getData().get(0).getPhonenumber());
                    }catch(NullPointerException ex){
                        Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onFailure(Call<ProfileResponse> call, Throwable t) {


                }
            });
        }else {

            Toast.makeText(getContext(), "null", Toast.LENGTH_SHORT).show();
        }

    }



}
