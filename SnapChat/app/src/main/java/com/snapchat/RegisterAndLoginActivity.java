package com.snapchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class RegisterAndLoginActivity extends Activity {

    private String TAG = RegisterAndLoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_login);
    }

    public void clickLogin(View view) {
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void clickRegister(View view) {
        finish();
        startActivity(new Intent(this, RegisterActivity.class));
    }
}
