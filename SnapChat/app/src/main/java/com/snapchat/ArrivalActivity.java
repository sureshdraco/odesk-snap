package com.snapchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class ArrivalActivity extends Activity {
	@Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_splash);
	    Intent intent = getIntent();
	    String payload = intent.getExtras().getString("payload");
	    Toast.makeText(getApplicationContext(), "PUSH TEXT: "+payload, Toast.LENGTH_LONG).show();
	    
	  }

}
