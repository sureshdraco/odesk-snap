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
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String BIRTHDAY = "birthday";
    private static final String PHONENUMBER = "phonenumber";
    private static final String PASSWORD = "password";

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

    public void saveAuthToken(String username) {
        sharedPreferences.edit().putString(AUTH_TOKEN, username).commit();
    }

    public String getUsername() {
        return sharedPreferences.getString(USERNAME, "");
    }


    public void saveUsername(String authToken) {
        sharedPreferences.edit().putString(USERNAME, authToken).commit();
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


    public void saveEmail(String email) {
        sharedPreferences.edit().putString(EMAIL, email).commit();
    }

    public String getEmail() {
        return sharedPreferences.getString(EMAIL, "");
    }


    public void saveBirthday(String birthday) {
        sharedPreferences.edit().putString(BIRTHDAY, birthday).commit();
    }

    public String getBirthday() {
        return sharedPreferences.getString(BIRTHDAY, "");
    }


    public void savePhoneNumber(String phoneNumber) {
        sharedPreferences.edit().putString(PHONENUMBER, phoneNumber).commit();
    }

    public String getPhoneNumber() {
        return sharedPreferences.getString(PHONENUMBER, "");
    }

    public void clearUserData() {
        sharedPreferences.edit().putString(PHONENUMBER, "").commit();
        sharedPreferences.edit().putString(USERNAME, "").commit();
        sharedPreferences.edit().putString(LOGIN_OBJECT, "").commit();
        sharedPreferences.edit().putString(EMAIL, "").commit();
        sharedPreferences.edit().putString(BIRTHDAY, "").commit();
        sharedPreferences.edit().putString(AUTH_TOKEN, "").commit();
    }

    public void savePassword(String password) {
        sharedPreferences.edit().putString(PASSWORD, password).commit();
    }

    public String getPassword() {
        return sharedPreferences.getString(PASSWORD, "");
    }

}
