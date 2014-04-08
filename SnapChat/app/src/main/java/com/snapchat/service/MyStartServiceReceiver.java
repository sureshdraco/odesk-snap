package com.snapchat.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyStartServiceReceiver extends BroadcastReceiver {

    private static final String TAG = MyStartServiceReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "MyStartServiceReceiver");
        Intent service = new Intent(context, SnapchatInboxService.class);
        context.startService(service);
    }
}