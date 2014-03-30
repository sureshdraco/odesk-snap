package com.snapchat;

import java.util.ArrayList;
import java.util.Date;

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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.habosa.javasnap.Snap;
import com.habosa.javasnap.Snapchat;

public class InboxArrayAdapter extends ArrayAdapter<Snap> {

    private ArrayList<Snap> snapList = new ArrayList<Snap>();
    private Context context;


    public InboxArrayAdapter(Context context, ArrayList<Snap> snapList) {
        super(context, R.layout.inbox_item, snapList);
        this.context = context;
        this.snapList = snapList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.inbox_item, null);
            viewHolder = new ViewHolder();
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.item_text1);
            viewHolder.time = (TextView) convertView.findViewById(R.id.item_text2);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.item_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Snap snap = snapList.get(position);
        if (snap.isIncoming()) {
            viewHolder.txtName.setText(snap.getSender());
            viewHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.inbox_received));
        } else {
            viewHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.inbox_senticon));
            viewHolder.txtName.setText(snap.getRecipient());
        }
        viewHolder.time.setText(new Date(snap.getTimeStamp()).toString());
        return convertView;
    }

    static class ViewHolder {
        TextView txtName;
        TextView time;
        ImageView imageView;
    }
}
