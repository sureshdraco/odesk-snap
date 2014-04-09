package com.snapchat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MotionEventCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import com.appcelerator.cloud.sdk.CCMeta;
import com.appcelerator.cloud.sdk.CCRequestMethod;
import com.appcelerator.cloud.sdk.CCResponse;
import com.appcelerator.cloud.sdk.Cocoafish;
import com.appcelerator.cloud.sdk.CocoafishError;
import com.habosa.javasnap.Snap;
import com.habosa.javasnap.Snapchat;
import com.snapchat.service.SnapchatInboxService;
import com.snapchat.util.AppStorage;
import com.snapchat.util.OnSwipeTouchListener;

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
    private InboxArrayAdapter inboxArrayAdapter;
    private String mCurrentPhotoPath = "";
    private String path;

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
        camera.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                gotoCameraActivity();
            }
        });

        ImageButton settings = (ImageButton) findViewById(R.id.inbox_setting);
        ImageButton contacts = (ImageButton) findViewById(R.id.inbox_contact);
        contacts.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                gotoActivity(ContactsActivity.class);
            }
        });
        settings.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                gotoActivity(SettingsActivity.class);
            }
        });

        listView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view,
                                           int position, long arg3) {
                try {
                    Snap snap = (Snap) listView.getItemAtPosition(position);
                    if (snap == null || !snap.isImage()) {
                        return false;
                    }
                    Intent intent = new Intent(InboxActivity.this, ImageDisplay.class);
                    intent.putExtra("snapId", snap.getId());
                    intent.putExtra("time", snap.getTime());
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        inboxArrayAdapter = new InboxArrayAdapter(getApplicationContext(), new ArrayList<Snap>());
        listView.setAdapter(inboxArrayAdapter);
        showChats();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            gotoActivity(SettingsActivity.class);
        }
        return false;
    }

    private void gotoActivity(Class activityClass) {
        startActivity(new Intent(this, activityClass));
    }

    private void gotoCameraActivity() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File file = createImageFile();
            if (file != null) {
                mUri = Uri.fromFile(file);
                Log.v("Path", "" + file.toString());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
                startActivityForResult(intent, 1);
            }
        } catch (Exception ignored) {
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file://" + image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Log.e("FilePath", "" + mCurrentPhotoPath);
                Intent intent = new Intent(InboxActivity.this, PaintActivity.class);
                intent.putExtra("filepath", mCurrentPhotoPath);
                startActivity(intent);
            }
        }
    }

    public void showChats() {
        Snap[] snapsList = Snapchat.getSnaps(AppStorage.getInstance(getApplicationContext()).getLoginObject());
        for (Snap snap : snapsList) {
            Log.d(TAG, snap.toString());
        }
        inboxArrayAdapter.setItems(new ArrayList<Snap>(Arrays.asList(snapsList)));
        inboxArrayAdapter.notifyDataSetChanged();
    }

    private BroadcastReceiver inboxUpdatedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                showChats();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(inboxUpdatedReceiver, new IntentFilter(SnapchatInboxService.INBOX_UPDATED));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(inboxUpdatedReceiver);
        super.onPause();
    }
}
