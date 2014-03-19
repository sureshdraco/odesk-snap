package com.snapchat;

import java.util.ArrayList;

import org.apache.commons.lang.WordUtils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class ContactsBaseAdapter extends BaseAdapter implements Filterable {

    int Height;
    ArrayList<Friend> Friends;
    Context context;
    ArrayList<Friend> mOriginalValues; // Original Values
    ArrayList<Friend> mDisplayedValues;    // Values to be displayed
    private Filter mFilter;

    ContactsBaseAdapter(Context context, ArrayList<Friend> friends, int height) {
        Friends = friends;
        Height = height;
        this.context = context;
        mOriginalValues = friends;
        mDisplayedValues = friends;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return Friends.size();
    }

    @Override
    public Friend getItem(int position) {
        // TODO Auto-generated method stub
        return Friends.get(position);
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
            txtName.setTypeface(InboxActivity.type1);
            if (txtName != null)
                txtName.setText(WordUtils.capitalize(Friends.get(position).USER_NAME));

        }
        return holder;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                mDisplayedValues = (ArrayList<Friend>) results.values; // has the filtered values
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
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        String data = mOriginalValues.get(i).USER_NAME;
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrList.add(new Friend(mOriginalValues.get(i).USER_NAME, mOriginalValues.get(i).USER_ID));
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
