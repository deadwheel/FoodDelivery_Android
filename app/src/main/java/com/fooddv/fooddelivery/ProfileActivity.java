package com.fooddv.fooddelivery;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fooddv.fooddelivery.models.Response.ProfileResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends BaseActivity {
    private Call<ProfileResponse> getProfileCall;
    private Call<ProfileResponse> putProfileCall;

    private EditText firstName;
    private EditText lastName;
    private EditText address;
    private EditText postCode;
    private EditText city;
    private EditText phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firstName = (EditText)findViewById(R.id.editTextProfileFirstName);
        lastName = (EditText)findViewById(R.id.editTextProfileLastName);
        address = (EditText)findViewById(R.id.editTextProfileAddress);
        postCode = (EditText)findViewById(R.id.editTextProfilePostCode);
        city = (EditText)findViewById(R.id.editTextProfileCity);
        phoneNumber = (EditText)findViewById(R.id.editTextProfilePhoneNumber);

        Button btEdit = (Button)findViewById(R.id.btProfileEdit);
        btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                putProfile();
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
                    if (response.isSuccessful()) {

                   try {
                       Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                       firstName.setText(response.body().getData().get(0).getFirstName());
                       lastName.setText(response.body().getData().get(0).getLastName());
                       address.setText(response.body().getData().get(0).getAddress());
                       postCode.setText(response.body().getData().get(0).getPostCode());
                       city.setText(response.body().getData().get(0).getCity());
                       phoneNumber.setText(response.body().getData().get(0).getPhonenumber());
                   }catch(NullPointerException ex){
                       Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();

                   }
                    }
                }

                @Override
                public void onFailure(Call<ProfileResponse> call, Throwable t) {

                    Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();

                }
            });
        }else {

            Toast.makeText(getApplicationContext(), "null", Toast.LENGTH_SHORT).show();
        }

    }

    private void putProfile(){

        putProfileCall = service.putProfile(firstName.getText().toString(),lastName.getText().toString(),address.getText().toString(),postCode.getText().toString(), city.getText().toString(), phoneNumber.getText().toString());

        if(putProfileCall!=null){

            putProfileCall.enqueue(new Callback<ProfileResponse>() {
                @Override
                public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                    if(response.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onFailure(Call<ProfileResponse> call, Throwable t) {
                    Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                }
            });


        }else {

            Toast.makeText(getApplicationContext(),"Null",Toast.LENGTH_SHORT).show();
        }

    }


}
