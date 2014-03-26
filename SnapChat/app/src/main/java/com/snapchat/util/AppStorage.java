package com.snapchat.util;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by suresh on 15/03/14.
 */
public class AppStorage {
    private static final String AUTH_TOKEN = "authToken";
    private static final String IS_USER_LOGGED_IN = "isUserLoggedIn";
    private static final String LOGIN_OBJECT = "loginObject";
    private static AppStorage appStorage;
    private final SharedPreferences sharedPreferences;

    private AppStorage(Context context) {
        sharedPreferences = context.getSharedPreferences(
                "com.snapchat.app", Context.MODE_PRIVATE);
    }

    public static AppStorage getInstance(Context context) {
        if (appStorage == null) {
            appStorage = new AppStorage(context);
        }
        return appStorage;
    }

    public void saveAuthToken(String authToken) {
        sharedPreferences.edit().putString(AUTH_TOKEN, authToken).commit();
    }

    public String getAuthToken() {
        return sharedPreferences.getString(AUTH_TOKEN, "");
    }

    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean(IS_USER_LOGGED_IN, false);
    }

    public void setUserLoggedIn(boolean userLoggedIn) {
        sharedPreferences.edit().putBoolean(IS_USER_LOGGED_IN, userLoggedIn).commit();
    }

    public void saveLoginObject(String loginJsonResponse) {
        sharedPreferences.edit().putString(LOGIN_OBJECT, loginJsonResponse).commit();
    }

    public JSONObject getLoginObject() {
        try {
            JSONObject jsonObject = new JSONObject(sharedPreferences.getString(LOGIN_OBJECT, ""));
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
