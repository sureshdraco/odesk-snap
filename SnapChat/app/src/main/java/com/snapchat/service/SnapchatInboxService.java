package com.snapchat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.habosa.javasnap.Snapchat;
import com.snapchat.util.AppStorage;

import org.json.JSONException;
import org.json.JSONObject;

public class SnapchatInboxService extends Service {
    public static final String INBOX_UPDATED = "inboxUpdated";
    private static final String TAG = SnapchatInboxService.class.getSimpleName();
    private final IBinder mBinder = new MyBinder();
    private ArrayList<String> list = new ArrayList<String>();

    private ExecutorService threadPool;

    @Override
    public void onCreate() {
        super.onCreate();
        threadPool = Executors.newSingleThreadExecutor();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onstartcommand");
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (AppStorage.getInstance(getApplicationContext()).isUserLoggedIn()) {
                    String username = AppStorage.getInstance(getApplicationContext()).getUsername();
                    String password = AppStorage.getInstance(getApplicationContext()).getPassword();
                    JSONObject loginResponse = Snapchat.login(username, password);
                    try {
                        if (loginResponse != null && loginResponse.getBoolean("logged")) {
                            AppStorage.getInstance(getApplicationContext()).saveLoginObject(loginResponse.toString());
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(INBOX_UPDATED));
                            return;
                        }
                    } catch (JSONException e) {
                    }
                    Log.e(TAG, "login failed");
                }
            }
        });
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        SnapchatInboxService getService() {
            return SnapchatInboxService.this;
        }
    }

    public List<String> getWordList() {
        return list;
    }

} 