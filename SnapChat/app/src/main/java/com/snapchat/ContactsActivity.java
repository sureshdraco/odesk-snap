package com.snapchat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appcelerator.cloud.sdk.CCMeta;
import com.appcelerator.cloud.sdk.CCRequestMethod;
import com.appcelerator.cloud.sdk.CCResponse;
import com.appcelerator.cloud.sdk.Cocoafish;
import com.appcelerator.cloud.sdk.CocoafishError;

public class ContactsActivity extends Activity {

    int width, height;
    DisplayMetrics metrics = new DisplayMetrics();
    RelativeLayout.LayoutParams rParams;
    Uri mUri;
    JSONArray users;
    ArrayList<Friend> fb_friend = null;
    ArrayList<Friend> com_friend = null;
    String image_path;
    private Cocoafish sdk;
    ListView list;
    private String Friend_ID;
    ContactsBaseAdapter1 adapter;
    JSONArray friends = null;
    JSONArray community = null;
    private Typeface type1;
    private Typeface type2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            image_path = extras.getString("path");
        }

        sdk = new Cocoafish("3y8Yl3hMHZhq5oewscFVLwiSmr8tUnhz");

        type1 = Typeface.createFromAsset(getAssets(), "KozGoPro-Light.otf");
        type2 = Typeface.createFromAsset(getAssets(), "KozGoPro-Medium.otf");

        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        height = metrics.heightPixels;

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Getting Friends");
        dialog.setCancelable(false);
        AsyncTask_getfriends task = new AsyncTask_getfriends(dialog, this, 1);
        task.execute("");

        /**
         * Iniatialize friends switch buttons
         */
        final TextView txt_community = (TextView) findViewById(R.id.contact_community);
        final TextView txt_friends = (TextView) findViewById(R.id.contact_facebook);
        txt_friends.setTypeface(type2);
        txt_friends.setTextSize((float) (0.03 * height));
        txt_friends.setTextColor(Color.parseColor("#808080"));
        txt_friends.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                txt_friends.setTypeface(type2);
                txt_community.setTypeface(type1);
                if (fb_friend == null) {
                    ProgressDialog dialog = new ProgressDialog(ContactsActivity.this);
                    dialog.setMessage("Getting Friends");
                    dialog.setCancelable(false);
                    AsyncTask_getfriends task = new AsyncTask_getfriends(dialog, ContactsActivity.this, 1);
                    task.execute("");
                } else {
                    if (fb_friend != null && fb_friend.size() != 0) {
                        adapter = new ContactsBaseAdapter1(getApplicationContext(), fb_friend, height);
                        list.setAdapter(adapter);
                    }
                }
            }
        });


        txt_community.setTypeface(type1);
        txt_community.setTextSize((float) (0.03 * height));
        txt_community.setTextColor(Color.parseColor("#808080"));
        txt_community.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                txt_friends.setTypeface(type1);
                txt_community.setTypeface(type2);
                if (com_friend == null) {
                    ProgressDialog dialog = new ProgressDialog(ContactsActivity.this);
                    dialog.setMessage("Getting Friends");
                    dialog.setCancelable(false);
                    AsyncTask_getfriends task = new AsyncTask_getfriends(dialog, ContactsActivity.this, 3);
                    task.execute("");
                } else {
                    if (com_friend != null && com_friend.size() != 0) {
                        adapter = new ContactsBaseAdapter1(getApplicationContext(), com_friend, height);
                        list.setAdapter(adapter);
                    }
                }
            }
        });

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
                try {
                    Log.d("Friends", "" + users.length() + "--" + users.getJSONObject(0));
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
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
                Object o = list.getItemAtPosition(position);
                Friend f = (Friend) o;
                Friend_ID = f.USER_ID;
                ProgressDialog dialog = new ProgressDialog(ContactsActivity.this);
                dialog.setMessage("Sending...");
                dialog.setCancelable(false);
                AsyncTask_getfriends task = new AsyncTask_getfriends(dialog, ContactsActivity.this, 2);
                task.execute("");

            }

        });

        /**
         * Setting the search bar panel
         */
        ((RelativeLayout) findViewById(R.id.contacts_searchbar)).getLayoutParams().height = (int) (height * 0.06875);

        ((ImageView) findViewById(R.id.contacts_search)).getLayoutParams().width = (int) (height * 0.06875);
        ((ImageView) findViewById(R.id.contacts_search)).getLayoutParams().height = (int) (height * 0.06875);

        EditText Search_txt = (EditText) findViewById(R.id.contacts_editText);
        Search_txt.setTextSize((float) (0.03 * height));
        Search_txt.setTypeface(Inbox_Activity.type1);
        Search_txt.setTextColor(Color.parseColor("#888888"));
        Search_txt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                adapter.getFilter().filter(arg0);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });

    }

    public void create_chat() {

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("to_ids",
                Friend_ID);
        data.put("message", "Hello!");
        data.put("photo", new File(image_path));
        JSONArray chats = null;

        try {
            CCResponse response = Inbox_Activity.sdk.sendRequest("chats/create.json",
                    CCRequestMethod.POST, data);

            JSONObject responseJSON = response.getResponseData();
            CCMeta meta = response.getMeta();
            if ("ok".equals(meta.getStatus()) && meta.getCode() == 200
                    && "createChatMessage".equals(meta.getMethod())) {
                Log.d("contacts", "" + meta.getStatus() + meta.getCode());
                try {
                    chats = responseJSON.getJSONArray("chats");

                    for (int i = 0; i < chats.length(); i++) {
                        JSONObject chat = chats.getJSONObject(i);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                System.out.println("message sent!");
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CocoafishError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void get_Users() {
        try {

            com_friend = new ArrayList<Friend>();
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("limit", 1000);
            CCResponse response = sdk.sendRequest("users/query.json", CCRequestMethod.GET, data);
            JSONObject responseJSON = response.getResponseData();
            CCMeta meta = response.getMeta();
            if ("ok".equals(meta.getStatus())
                    && meta.getCode() == 200
                    && "queryUsers".equals(meta.getMethod())) {
                Log.e("Contacts", "" + meta.getStatus() + "--" + meta.getCode());
                try {
                    community = responseJSON.getJSONArray("users");
                    for (int i = 0; i < community.length(); i++) {
                        com_friend.add(new Friend(community.getJSONObject(i).getString("username"), community.getJSONObject(i).getString("id")));
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CocoafishError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void get_Friends() {
        try {

            fb_friend = new ArrayList<Friend>();
            CCResponse response = Inbox_Activity.sdk.sendRequest("social/facebook/search_friends.json", CCRequestMethod.GET, null);
            JSONObject responseJSON = response.getResponseData();
            CCMeta meta = response.getMeta();
            if ("ok".equals(meta.getStatus())
                    && meta.getCode() == 200
                    && "searchFacebookFriends".equals(meta.getMethod())) {
                Log.e("Contacts", "" + meta.getStatus() + "--" + meta.getCode());
                try {
                    friends = responseJSON.getJSONArray("users");
                    for (int i = 0; i < friends.length(); i++) {
                        fb_friend.add(new Friend(friends.getJSONObject(i).getString("username"), friends.getJSONObject(i).getString("id")));
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CocoafishError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void show() {
        // TODO Auto-generated method stub
//		Log.e("Friends", ""+friends);
        if (fb_friend != null && fb_friend.size() != 0) {
            adapter = new ContactsBaseAdapter1(getApplicationContext(), fb_friend, height);
            list.setAdapter(adapter);
        }
    }

    public void show_users() {
        // TODO Auto-generated method stub
        if (com_friend != null && com_friend.size() != 0) {
            adapter = new ContactsBaseAdapter1(getApplicationContext(), com_friend, height);
            list.setAdapter(adapter);
        }
    }

    public class ContactsBaseAdapter1 extends BaseAdapter implements Filterable {

        int Height;
        //		ArrayList<Friend> Friends;
        Context context;
        ArrayList<Friend> mOriginalValues; // Original Values
        ArrayList<Friend> mDisplayedValues;    // Values to be displayed
        private Filter mFilter;

        ContactsBaseAdapter1(Context context, ArrayList<Friend> friends, int height) {
//			Friends = friends;
            Height = height;
            this.context = context;
            mOriginalValues = friends;
            mDisplayedValues = friends;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mDisplayedValues.size();
        }

        @Override
        public Friend getItem(int position) {
            // TODO Auto-generated method stub
            return mDisplayedValues.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView txtName;
            View holder = convertView;
            if (holder == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                holder = vi.inflate(R.layout.contacts_item, null);
            }
            Friend rssItem = (Friend) getItem(position);
            if (rssItem != null) {
                txtName = (TextView) holder.findViewById(R.id.contacts_item_text);
                txtName.setTextSize((float) (Height * 0.0296));
                txtName.setTextColor(Color.parseColor("#333333"));
                txtName.setTypeface(Inbox_Activity.type1);
                if (txtName != null)
                    txtName.setText(WordUtils.capitalize(mDisplayedValues.get(position).USER_NAME));

            }
            return holder;
        }

        @Override
        public void notifyDataSetInvalidated() {
            // TODO Auto-generated method stub
            super.notifyDataSetInvalidated();
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    mDisplayedValues = (ArrayList<Friend>) results.values; // has the filtered values
                    Log.d("publish", "" + mDisplayedValues.size());
                    notifyDataSetChanged();  // notifies the data with new filtered values
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                    ArrayList<Friend> FilteredArrList = new ArrayList<Friend>();

                    if (mOriginalValues == null) {
                        mOriginalValues = new ArrayList<Friend>(mDisplayedValues); // saves the original data in mOriginalValues
                    }

                    /********
                     *
                     *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                     *  else does the Filtering and returns FilteredArrList(Filtered)
                     *
                     ********/
                    if (constraint == null || constraint.length() == 0) {

                        // set the Original result to return
                        results.count = mOriginalValues.size();
                        results.values = mOriginalValues;
                    } else {
//		                constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < mOriginalValues.size(); i++) {
                            String data = mOriginalValues.get(i).USER_NAME;
                            if (data.contains(constraint)) {
                                FilteredArrList.add(new Friend(mOriginalValues.get(i).USER_NAME, mOriginalValues.get(i).USER_ID));
                                Log.e("FILTER", data + "----" + constraint);

                            }
                        }
                        // set the Filtered result to return
                        results.count = FilteredArrList.size();
                        results.values = FilteredArrList;
                    }
                    return results;
                }
            };
            return filter;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

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
        // TODO Auto-generated method stub
        super.onPreExecute();
        P_Dialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        if (ID == 1) {
            Login.get_Friends();
        } else if (ID == 2) {
            Login.create_chat();
        } else if (ID == 3) {
            Login.get_Users();
            ;
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);

        if (ID == 1) {
            Login.show();
        } else if (ID == 3) {
            Login.show_users();
        }
        P_Dialog.dismiss();
    }
} 