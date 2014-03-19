package com.snapchat;

import java.util.ArrayList;

import org.apache.commons.lang.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyBaseAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    JSONArray Array;
    String USER_ID;
    ArrayList<String> ids;
    int height;
    Boolean is_checked;
    Context context;
//    JSONObject json;


    public MyBaseAdapter(Context context, JSONArray chats, String userid, int height) {
        this.context = context;
//    	mInflater = LayoutInflater.from(context);
        is_checked = false;
        this.height = height;
        Array = chats;
        USER_ID = userid;
//        ids = new ArrayList<String>();
        ProjectDatabaseHandler handler = new ProjectDatabaseHandler(context);
        ids = handler.getallids();
        handler.close();

        for (int i = 0; i < ids.size(); i++) {
            Log.d("savedids", "" + ids.size() + "---" + ids.get(i));
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return Array.length();
    }

    @Override
    public JSONObject getItem(int position) {
        try {
            return Array.getJSONObject(position);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        // TODO Auto-generated method stub
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        Boolean check = true;
        for (int i = 0; i < ids.size(); i++) {
            try {
                if (Array.getJSONObject(position).getString("id").equalsIgnoreCase(ids.get(i))) {
                    check = false;
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return check;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView txtName, time;
        ImageView bin;
        View holder = convertView;
        if (holder == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            holder = vi.inflate(R.layout.inbox_item, null);
        }
        JSONObject rssItem = (JSONObject) getItem(position);
        if (rssItem != null) {
            txtName = (TextView) holder.findViewById(R.id.item_text1);
            time = (TextView) holder.findViewById(R.id.item_text2);
            txtName.setTextSize((float) (height * 0.0306));
            txtName.setTextColor(Color.parseColor("#333333"));
            txtName.setTypeface(Inbox_Activity.type2);
            time.setTextSize((float) (height * 0.019));
            time.setTextColor(Color.parseColor("#808080"));
            time.setTypeface(Inbox_Activity.type1);
            bin = (ImageView) holder.findViewById(R.id.item_image);

            JSONObject json;
            try {
                json = Array.getJSONObject(position);
                if (txtName != null)

                    txtName.setText(WordUtils.capitalize(json.getJSONObject("from").getString("username")));
                if (time != null)
                    time.setText(WordUtils.capitalize(json.getJSONObject("from").getString("created_at")));
                if (bin != null) {
                    if (json.getJSONObject("from").getString("id").equalsIgnoreCase(USER_ID)) {
                        bin.setBackgroundResource(R.drawable.inbox_senticon);
                    } else {
                        bin.setBackgroundResource(R.drawable.inbox_received);
                    }
                }

                for (int i = 0; i < ids.size(); i++) {
                    if (json.getString("id").equalsIgnoreCase(ids.get(i))) {
                        is_checked = true;
                        Log.v("Checkid", "" + json.getString("id") + "----" + ids.get(i) + "---" + position);
                        txtName.setTypeface(Inbox_Activity.type1);
                    }
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

////		View holder;
//		if (convertView == null) {
//			LayoutInflater mInflater = LayoutInflater.from(context);
//			holder = mInflater.inflate(R.layout.inbox_item, null);
////			holder = new ViewHolder();
//			txtName = (TextView) holder.findViewById(R.id.item_text1);
//			time = (TextView) holder.findViewById(R.id.item_text2);			
//			txtName.setTextSize((float) (height*0.0306));
//			txtName.setTextColor(Color.parseColor("#333333"));
//			txtName.setTypeface(Inbox_Activity.type2);
//			time.setTextSize((float) (height*0.019));
//			time.setTextColor(Color.parseColor("#808080"));
//			time .setTypeface(Inbox_Activity.type1);
//			bin = (ImageView)holder.findViewById(R.id.item_image);
//			
//			JSONObject json;
//			try {
//				json = Array.getJSONObject(position);
//				txtName.setText(json.getJSONObject("from").getString("username"));
//				time.setText(json.getJSONObject("from").getString("updated_at"));
//				if(json.getJSONObject("from").getString("id").equalsIgnoreCase(USER_ID)){
//					bin.setBackgroundResource(R.drawable.inbox_senticon);
//				}else {
//					bin.setBackgroundResource(R.drawable.inbox_received);
//				}
//				for(int i=0;i<ids.size();i++){
//					if(json.getString("id").equalsIgnoreCase(ids.get(i))){					
//						is_checked = true;
//						Log.v("Checkid", ""+json.getString("id")+"----"+ids.get(i)+"---"+position);
//						txtName.setTypeface(Inbox_Activity.type1);
//					}
//				}
//
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//		} else {
//			holder = convertView;
//		}
        return holder;
    }

    static class ViewHolder {
        TextView txtName;
        TextView time;
        ImageView bin;
    }
}
