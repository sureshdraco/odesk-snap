package com.snapchat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.habosa.javasnap.Friend;
import com.habosa.javasnap.ResponseModel;
import com.habosa.javasnap.Snapchat;
import com.snapchat.util.AppStorage;

public class ContactsActivity extends Activity {

    private static final String TAG = ContactsActivity.class.getSimpleName();
    int width, height;
    DisplayMetrics metrics = new DisplayMetrics();
    RelativeLayout.LayoutParams rParams;
    ArrayList<Friend> friendList = new ArrayList<Friend>();
    ListView list;
    ContactsArrayAdapter adapter;
    String path, mCurrentPhotoPath;
    private Uri mUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        if (getIntent() != null && getIntent().getExtras() != null) {
            path = getIntent().getExtras().getString("path");
            Log.d(TAG, path);
        }
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        height = metrics.heightPixels;

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Getting Friends");
        dialog.setCancelable(false);
        AsyncTask_getfriends task = new AsyncTask_getfriends(dialog, this, 1);
        task.execute("");

        /**
         * Initialize Top Bar
         */

        ((RelativeLayout) findViewById(R.id.contacts_topbar)).getLayoutParams().height = (int) (height * 0.1109);

        ImageButton camera = (ImageButton) findViewById(R.id.contacts_camera);
        rParams = new RelativeLayout.LayoutParams((int) (width * 0.1234), (int) (height * 0.0689));
        rParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rParams.leftMargin = (int) (width * 0.03);
        rParams.addRule(RelativeLayout.CENTER_VERTICAL);
        camera.setLayoutParams(rParams);
        camera.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                gotoCameraActivity();
            }
        });

        ImageButton contacts = (ImageButton) findViewById(R.id.contacts_contact);
        rParams = new RelativeLayout.LayoutParams((int) (width * 0.1031), (int) (height * 0.0689));
        rParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rParams.rightMargin = (int) (width * 0.03);
        rParams.addRule(RelativeLayout.CENTER_VERTICAL);
        contacts.setLayoutParams(rParams);
        contacts.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        });

        /**
         * ListView
         */
        list = (ListView) findViewById(R.id.contacts_listview);
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position,
                                    long arg3) {
                if (!TextUtils.isEmpty(path)) {
                    ProgressDialog dialog = new ProgressDialog(ContactsActivity.this);
                    dialog.setMessage("Sending snap...");
                    dialog.setCancelable(false);
                    new AsyncTask_SendSnap(dialog, ContactsActivity.this, friendList.get(position).getUsername()).execute();
                }
            }

        });

        EditText Search_txt = (EditText) findViewById(R.id.contacts_editText);
        Search_txt.setTypeface(InboxActivity.type1);
        Search_txt.setTextColor(Color.parseColor("#888888"));
        Search_txt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                adapter.getFilter().filter(arg0);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });

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
                Log.e("FilePath", "" + path);
                Intent intent = new Intent(ContactsActivity.this, PaintActivity.class);
                intent.putExtra("filepath", mCurrentPhotoPath);
                startActivity(intent);
            }
        }
    }

    public ArrayList<Friend> getFriends() {
        com.habosa.javasnap.Friend[] friends = Snapchat.getFriends(AppStorage.getInstance(this).getLoginObject());
        return new ArrayList<Friend>(Arrays.asList(friends));
    }

    public void show() {
        if (friendList != null && friendList.size() != 0) {
            adapter = new ContactsArrayAdapter(getApplicationContext(), getFriends());
            list.setAdapter(adapter);
        }
    }

    class AsyncTask_getfriends extends AsyncTask<String, String, String> {

        ProgressDialog P_Dialog;
        ContactsActivity Login;
        int ID;

        public AsyncTask_getfriends(ProgressDialog dialog, ContactsActivity login, int id) {
            P_Dialog = dialog;
            Login = login;
            this.ID = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            P_Dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            if (ID == 1) {
                friendList = getFriends();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (ID == 1) {
                Login.show();
            }
            P_Dialog.dismiss();
        }
    }


    class AsyncTask_SendSnap extends AsyncTask<String, String, Boolean> {

        ProgressDialog P_Dialog;
        ContactsActivity contactsActivity;
        String recepientUsername;

        public AsyncTask_SendSnap(ProgressDialog dialog, ContactsActivity contactsActivity, String recepientUsername) {
            P_Dialog = dialog;
            this.contactsActivity = contactsActivity;
            this.recepientUsername = recepientUsername;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            P_Dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            File imageFile = new File(path);
            boolean isSent = false;
            String id = "";
            try {
                ResponseModel responseModel = Snapchat.upload(imageFile, AppStorage.getInstance(getApplicationContext()).getUsername(), AppStorage.getInstance(getApplicationContext()).getAuthToken());
                if (!responseModel.isSuccess()) {
                    isSent = false;
                    Toast.makeText(getApplicationContext(), responseModel.getData(), Toast.LENGTH_LONG).show();
                } else {
                    Log.d(TAG, "id: " + id);
                    id = responseModel.getData();
                    ArrayList<String> recepientList = new ArrayList<String>();
                    isSent = Snapchat.send(id, recepientList, false, 5, AppStorage.getInstance(getApplicationContext()).getUsername(), AppStorage.getInstance(getApplicationContext()).getAuthToken());
                }
            } catch (Exception ignored) {

            }
            return isSent;
        }

        @Override
        protected void onPostExecute(Boolean isSent) {
            super.onPostExecute(isSent);
            Toast.makeText(getApplicationContext(), isSent ? "Snap sent" : "Sending failed", Toast.LENGTH_LONG).show();
            P_Dialog.dismiss();
        }
    }
}