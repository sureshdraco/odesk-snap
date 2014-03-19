package com.snapchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.snapchat.util.AppStorage;

public class SplashActivity extends Activity {

    private long delayMillis = 3000;
    private String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if(AppStorage.getInstance(getApplicationContext()).isUserLoggedIn()) {
                    startActivity(new Intent(SplashActivity.this, InboxActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, RegisterAndLoginActivity.class));
                }
                finish();
            }
        }, delayMillis);
    }

}
