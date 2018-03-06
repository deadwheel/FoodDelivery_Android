package com.fooddv.fooddelivery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.fooddv.fooddelivery.models.AccessToken;
import com.fooddv.fooddelivery.network.ApiService;
import com.fooddv.fooddelivery.network.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vr on 2017-11-24.
 */

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private  ActionBarDrawerToggle actionBarDrawerToggle;
    protected ApiService service;
    protected TokenManager tokenManager;
   // private TabLayout tabLayout;
    //private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DrawerLayout fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);

        //mViewPager = (ViewPager)fullView.findViewById(R.id.container);
       // tabLayout = (TabLayout)fullView.findViewById(R.id.tabs);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

       /* NavigationView navigationView = (NavigationView) fullView.findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        setContentView(fullView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,fullView,toolbar,R.string.drawer_open,R.string.drawer_close);

        fullView.addDrawerListener(actionBarDrawerToggle);
    */
        if(tokenManager.getToken().getAccessToken() == null){
            startActivity(new Intent(BaseActivity.this, LoginActivity.class));
            finish();
        }


        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        DrawerLayout fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = (FrameLayout) fullView.findViewById(R.id.activity_content);
        NavigationView navigationView = (NavigationView) fullView.findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);

        super.setContentView(fullView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,fullView,toolbar,R.string.drawer_open,R.string.drawer_close);

        fullView.addDrawerListener(actionBarDrawerToggle);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id=item.getItemId();

        switch(id){

            case R.id.action_logout: {
                makeLogoutDialog("Wylogowywanie");
              }
                break;

            case R.id.action_orders:
                Intent myIntent = new Intent(BaseActivity.this, DriverOrdersActivity.class);
                BaseActivity.this.startActivity(myIntent);
                break;

            case R.id.action_active_orders:
                Intent myIntent_2 = new Intent(BaseActivity.this, DriverOrderActiveActivity.class);
                BaseActivity.this.startActivity(myIntent_2);
                break;
	   case R.id.action_basket:
                Intent basketActivity = new Intent(BaseActivity.this, BasketActivity.class);
                BaseActivity.this.startActivity(basketActivity);
                break;
            case R.id.action_offer:
                Intent offerActivity = new Intent(BaseActivity.this, OfferActivity.class);
                BaseActivity.this.startActivity(offerActivity);
                break;
            case R.id.user_order:
                Intent orderUserActivity = new Intent(BaseActivity.this, OrderUserActivity.class);
                BaseActivity.this.startActivity(orderUserActivity);
                break;
            case R.id.action_profile:
                Intent profileActivity = new Intent(BaseActivity.this, ProfileActivity.class);
                BaseActivity.this.startActivity(profileActivity);
                break;


        }

        return true;
    }

    public void setTitle(final String title){

        getSupportActionBar().setTitle(title);

    }

    private void logout(){

        Call<AccessToken> log;
        log = service.logout();
        log.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {

                if (response.isSuccessful()) {
                    tokenManager.deleteToken();
                    startActivity(new Intent(BaseActivity.this, LoginActivity.class));
                }
            }
            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {

            }
        });

    }

    private void makeLogoutDialog(final String title){

        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
        builder.setMessage(R.string.logout_message)
                .setTitle(title)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        logout();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

 /*   public ViewPager getPager(){

        return mViewPager;
    }
*/
  /*  public TabLayout getTabLayout(){

        return  tabLayout;
    }*/
}
