package com.fooddv.fooddelivery;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.fooddv.fooddelivery.models.Response.ProfileResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends BaseActivity {
    private Call<ProfileResponse> getProfileCall;
    private Call<ProfileResponse> putProfileCall;

    private AwesomeValidation validator;
    private EditText firstName;
    private EditText lastName;
    private EditText address;
    private EditText postCode;
    private EditText city;
    private EditText phoneNumber;
    private TextInputLayout til_phonenumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        setTitle("Profil");

        firstName = (EditText)findViewById(R.id.editTextProfileFirstName);
        lastName = (EditText)findViewById(R.id.editTextProfileLastName);
        address = (EditText)findViewById(R.id.editTextProfileAddress);
        postCode = (EditText)findViewById(R.id.editTextProfilePostCode);
        city = (EditText)findViewById(R.id.editTextProfileCity);
        phoneNumber = (EditText)findViewById(R.id.editTextProfilePhoneNumber);
        til_phonenumber = (TextInputLayout)findViewById(R.id.til_phonenumber);
        validator.addValidation(this, R.id.til_phonenumber, RegexTemplate.TELEPHONE, R.string.wrong_phonenumber);
        Button btEdit = (Button)findViewById(R.id.btProfileEdit);
        btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                til_phonenumber.setError(null);

                if(validator.validate()) {
                    putProfile();
                }
            }
        });
        getProfile();
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
                       address.setText(response.body().getData().get(0).getAddress());
                       postCode.setText(response.body().getData().get(0).getPostCode());
                       city.setText(response.body().getData().get(0).getCity());
                       phoneNumber.setText(response.body().getData().get(0).getPhonenumber());
                   }catch(NullPointerException ex){


                   }
                }

                @Override
                public void onFailure(Call<ProfileResponse> call, Throwable t) {

                }
            });
        }else {


        }

    }

    private void putProfile(){

        putProfileCall = service.putProfile(firstName.getText().toString(),lastName.getText().toString(),address.getText().toString(),postCode.getText().toString(), city.getText().toString(), phoneNumber.getText().toString());

        if(putProfileCall!=null){

            putProfileCall.enqueue(new Callback<ProfileResponse>() {
                @Override
                public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                    if(response.isSuccessful()){

                        refresh();

                    }
                }

                @Override
                public void onFailure(Call<ProfileResponse> call, Throwable t) {

                }
            });


        }else {


        }

    }

    private void refresh(){

        Intent refresh = new Intent(this, ProfileActivity.class);
        startActivity(refresh);
        this.finish();

    }
}
