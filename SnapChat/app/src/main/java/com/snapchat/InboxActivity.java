package com.snapchat;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.appcelerator.cloud.push.CCPushService;
import com.appcelerator.cloud.push.DeviceTokenCallback;
import com.appcelerator.cloud.push.PushServiceException;
import com.appcelerator.cloud.sdk.CCMeta;
import com.appcelerator.cloud.sdk.CCRequestMethod;
import com.appcelerator.cloud.sdk.CCResponse;
import com.appcelerator.cloud.sdk.Cocoafish;
import com.appcelerator.cloud.sdk.CocoafishError;
import com.habosa.javasnap.Snap;
import com.habosa.javasnap.Snapchat;
import com.snapchat.util.AppStorage;

public class InboxActivity extends Activity {

	private static final String TAG = InboxActivity.class.getSimpleName();
	Timer repeat_timer = new Timer();
	Uri mUri;
	int width, height;
	DisplayMetrics metrics = new DisplayMetrics();
	RelativeLayout.LayoutParams rParams;
	ListView listView;
	JSONArray chats, chats_new;
	JSONObject user;
	public static Cocoafish sdk;
	public static CountDownTimer timer;
	public static Typeface type1, type2;
	private SharedPreferences mPrefs;
	public static String USER_ID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inbox);
		listView = (ListView) findViewById(R.id.inbox_listview);

		type1 = Typeface.createFromAsset(getAssets(), "KozGoPro-Light.otf");
		type2 = Typeface.createFromAsset(getAssets(), "KozGoPro-Medium.otf");

		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		width = metrics.widthPixels;
		height = metrics.heightPixels;

		((RelativeLayout) findViewById(R.id.inbox_topbar)).getLayoutParams().height = (int) (height * 0.1109);

		ImageButton camera = (ImageButton) findViewById(R.id.inbox_camera);
		rParams = new RelativeLayout.LayoutParams((int) (width * 0.1234), (int) (height * 0.0689));
		rParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		rParams.leftMargin = (int) (width * 0.03);
		rParams.addRule(RelativeLayout.CENTER_VERTICAL);
		camera.setLayoutParams(rParams);
		camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Snap" + ".jpg");
				mUri = Uri.fromFile(file);
				Log.v("Path", "" + file.toString());
				intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
				intent.putExtra("return-data", true);
				startActivityForResult(intent, 1);
			}
		});

		ImageButton settings = (ImageButton) findViewById(R.id.inbox_setting);
		rParams = new RelativeLayout.LayoutParams((int) (width * 0.1031), (int) (height * 0.0689));
		rParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rParams.rightMargin = (int) (width * 0.03);
		rParams.addRule(RelativeLayout.CENTER_VERTICAL);
		settings.setLayoutParams(rParams);
		settings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// startActivity(new Intent(InboxActivity.this, ContactsActivity.class));
				Toast.makeText(getApplicationContext(), "To be implemented", Toast.LENGTH_LONG).show();
			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				Object o = listView.getItemAtPosition(position);
				try {
					Log.e("item", "" + position);
					JSONObject json = chats.getJSONObject(position);
					ProjectDatabaseHandler handler = new ProjectDatabaseHandler(InboxActivity.this);
					handler.addMessage(json.getString("id"));
					handler.close();
					Intent intent = new Intent(InboxActivity.this, ImageDisplay.class);
					intent.putExtra("url", json.getJSONObject("photo").getJSONObject("urls").getString("original"));
					startActivity(intent);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			}
		});

		timer = new CountDownTimer(10000, 100) {
			int secondsLeft = 0;

			public void onTick(long ms) {
				if (Math.round((float) ms / 1000.0f) != secondsLeft) {
					secondsLeft = Math.round((float) ms / 1000.0f);

				}
			}

			public void onFinish() {
				if (listView.getAdapter() != null) {
					listView.setAdapter(null);
				}
				MyBaseAdapter adapter = new MyBaseAdapter(getApplicationContext(), chats, USER_ID, height);
				listView.setAdapter(adapter);
			}
		};
		showChats();
	}

	//
	// public void toCallAsynchronous() {
	// TimerTask doAsynchronousTask;
	// final Handler handler = new Handler();
	// repeat_timer = new Timer();
	// doAsynchronousTask = new TimerTask() {
	// int count = 0;
	//
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// handler.post(new Runnable() {
	// public void run() {
	// try {
	// System.out.println("sdgdsfgadfgfd" + count);
	// ProgressDialog dialog = new ProgressDialog(InboxActivity.this);
	// dialog.setMessage("Getting Chats");
	// GetChatsTask task = new GetChatsTask(dialog, InboxActivity.this, 3);
	// task.execute("");
	// count++;
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	//
	// }
	// }
	// });
	// }
	// };
	// repeat_timer.schedule(doAsynchronousTask, 0, 20000);//execute in every 50000 ms
	//
	// }

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		repeat_timer.cancel();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		repeat_timer.cancel();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.e("Onresume", "resume");
		// if(listView.getAdapter()!=null)
		// listView.setAdapter(null);
		// MyBaseAdapter adapter = new MyBaseAdapter(getApplicationContext(),chats,LoginActivity.USER_ID,height);
		// listView.setAdapter(adapter);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				String path = mUri.getPath();
				Log.e("FilePath", "" + path);
				Intent intent = new Intent(InboxActivity.this, PaintActivity.class);
				intent.putExtra("filepath", path);
				startActivity(intent);
			}
		}
	}

	public void showChats() {
		Snap[] snapsList = Snapchat.getSnaps(AppStorage.getInstance(getApplicationContext()).getLoginObject());
		for (Snap snap : snapsList) {
			Log.d(TAG, snap.toString());
		}
		if (listView.getAdapter() != null) {
			listView.setAdapter(null);
		}
		MyBaseAdapter adapter = new MyBaseAdapter(getApplicationContext(), chats, USER_ID, height);
		listView.setAdapter(adapter);
	}

	public void update_chats() {
		// TODO Auto-generated method stub
		if (chats_new.length() > chats.length()) {
			Log.v("UPDATECHATS", "" + chats.length() + "--" + chats_new.length());
			if (listView.getAdapter() != null) {
				listView.setAdapter(null);
			}
			MyBaseAdapter adapter = new MyBaseAdapter(getApplicationContext(), chats_new, USER_ID, height);
			listView.setAdapter(adapter);
		}
	}

	public void subscribe() {

		CCPushService.getInstance().getDeviceTokenAsnyc(getApplicationContext(), "3y8Yl3hMHZhq5oewscFVLwiSmr8tUnhz", new DeviceTokenCallback() {

			@Override
			public void receivedDeviceToken(String arg0) {
				// TODO Auto-generated method stub
				// Cocoafish sdk = new Cocoafish("<YOUR APP APP KEY>"); // app key
				// Cocoafish sdk = new Cocoafish("<OAuth Key>", "<OAuth Secret>"); // OAuth key & secret

				Map<String, Object> data = new HashMap<String, Object>();
				data.put("type", "android");
				data.put("channel", "friend_request");
				data.put("device_token", arg0);
				try {
					CCResponse response = sdk.sendRequest("push_notification/subscribe.json", CCRequestMethod.POST, data, false);
					JSONObject responseJSON = response.getResponseData();
					CCMeta meta = response.getMeta();
					if ("ok".equals(meta.getStatus())
							&& meta.getCode() == 200
							&& "SubscribeNotification".equals(meta.getMethod())) {
						Log.d("Subscribed", "YES" + arg0);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CocoafishError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void failedReceiveDeviceToken(Throwable arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	public void start_pushservice() {
		try {
			CCPushService.getInstance().startService(getApplicationContext());
		} catch (PushServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("ERRORERROR", "error");
		}
	}

}
