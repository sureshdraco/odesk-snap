package com.snapchat;

import java.util.ArrayList;

import org.apache.commons.lang.WordUtils;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.habosa.javasnap.Friend;

public class ContactsArrayAdapter extends ArrayAdapter<Friend> implements Filterable {

    Context context;
    ArrayList<Friend> friendsList = new ArrayList<Friend>();
    ArrayList<Friend> originalFriendsList = new ArrayList<Friend>();

    ContactsArrayAdapter(Context context, ArrayList<Friend> friends) {
        super(context, R.layout.contacts_item, friends);
        friendsList = friends;
        originalFriendsList = friends;
        this.context = context;
    }

    @Override
    public int getCount() {
        return friendsList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.contacts_item, null);
            viewHolder = new ViewHolder();
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.contacts_item_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Friend friend = friendsList.get(position);
        if (friend != null) {
            viewHolder.txtName.setText(friend.getDisplayName());
            viewHolder.txtName.setTextColor(Color.parseColor("#333333"));
            viewHolder.txtName.setTypeface(InboxActivity.type1);

        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                friendsList = (ArrayList<Friend>) results.values;
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (!TextUtils.isEmpty(constraint)) {
                    ArrayList<Friend> searchResult = new ArrayList<Friend>();
                    for (Friend friend : friendsList) {
                        if (friend.getDisplayName().toLowerCase().contains(String.valueOf(constraint).toLowerCase())) {
                            searchResult.add(friend);
                        }
                    }
                    filterResults.values = searchResult;
                    filterResults.count = searchResult.size();
                } else {
                    filterResults.values = originalFriendsList;
                    filterResults.count = originalFriendsList.size();
                }
                return filterResults;
            }
        };
        return filter;
    }

    class ViewHolder {
        TextView txtName;
    }

}