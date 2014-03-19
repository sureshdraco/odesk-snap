package com.snapchat;

import org.json.JSONException;
import org.json.JSONObject;

import com.appcelerator.cloud.push.PushService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CustomReciever extends BroadcastReceiver {

	@Override
	  public void onReceive(Context context, Intent intent) {
	    if(intent == null || context == null)
	      return;   
	    if (intent.getAction().equals(PushService.ACTION_MSG_ARRIVAL)) {
	      String payloadString = intent.getStringExtra("payload");
	      // Covert payload from String to JSONObject
	      JSONObject payload = null;
	      try {
	        payload = new JSONObject(payloadString);
	      } catch (JSONException ex) {
	        //error
	      }
	    }
	  }
	
}
