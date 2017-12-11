package com.fooddv.fooddelivery;

import android.content.SharedPreferences;

import com.fooddv.fooddelivery.models.AccessToken;

public class TokenManager {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private static TokenManager INSTANCE = null;

    private TokenManager(SharedPreferences prefs){
        this.prefs = prefs;
        this.editor = prefs.edit();
    }

    public static synchronized TokenManager getInstance(SharedPreferences prefs){
        if(INSTANCE == null){
            INSTANCE = new TokenManager(prefs);
        }
        return INSTANCE;
    }

    public void saveToken(AccessToken token){
        editor.putString("ACCESS_TOKEN", token.getAccessToken()).commit();
        editor.putString("REFRESH_TOKEN", token.getRefreshToken()).commit();
        editor.putString("ROLE_ID", token.getRoleID()).commit();
        editor.putInt("EXPIRES_IN", token.getExpiresIn()).commit();
    }

    public void deleteToken(){
        editor.remove("ACCESS_TOKEN").commit();
        editor.remove("REFRESH_TOKEN").commit();
        editor.remove("ROLE_ID").commit();
        editor.remove("EXPIRES_IN").commit();
    }

    public AccessToken getToken(){
        AccessToken token = new AccessToken();
        token.setAccessToken(prefs.getString("ACCESS_TOKEN", null));
        token.setRefreshToken(prefs.getString("REFRESH_TOKEN", null));
        token.setRoleID(prefs.getString("ROLE_ID", null));
        token.setExpiresIn(prefs.getInt("EXPIRES_IN", 0));
        return token;
    }



}
