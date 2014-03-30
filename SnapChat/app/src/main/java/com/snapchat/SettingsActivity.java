package com.snapchat;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.habosa.javasnap.Snapchat;
import com.snapchat.util.AppStorage;


public class SettingsActivity extends ActionBarActivity {

    private TextView email, birthday, phoneNumber, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        email = (TextView) findViewById(R.id.email);
        birthday = (TextView) findViewById(R.id.birthday);
        phoneNumber = (TextView) findViewById(R.id.phoneNumber);
        username = (TextView) findViewById(R.id.username);
        email.setText(AppStorage.getInstance(getApplicationContext()).getEmail());
        birthday.setText(AppStorage.getInstance(getApplicationContext()).getBirthday());
        phoneNumber.setText(AppStorage.getInstance(getApplicationContext()).getPhoneNumber());
        username.setText(AppStorage.getInstance(getApplicationContext()).getUsername());
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickLogout(view);
            }
        });
    }

    public void clickLogout(View view) {
        finish();
        AppStorage.getInstance(getApplicationContext()).setUserLoggedIn(false);
        AppStorage.getInstance(getApplicationContext()).clearUserData();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
