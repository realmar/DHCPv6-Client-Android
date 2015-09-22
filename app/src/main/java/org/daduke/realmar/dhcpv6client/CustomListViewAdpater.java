package org.daduke.realmar.dhcpv6client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Anastassios Martakos on 8/12/15.
 */
public class CustomListViewAdpater extends BaseAdapter {
    private ArrayList<Object> ip_array;
    private LayoutInflater inflater;
    private static final int TYPE_PERSON = 0;
    private static final int TYPE_DIVIDER = 1;

    public CustomListViewAdpater(Context context, ArrayList<Object> ip_array) {
        this.ip_array = ip_array;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return ip_array.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return ip_array.get(position);
    }

    @Override
    public int getViewTypeCount() {
        // TYPE_PERSON and TYPE_DIVIDER
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof IpCollector) {
            return TYPE_PERSON;
        }

        return TYPE_DIVIDER;
    }

    @Override
    public boolean isEnabled(int position) {
        return (getItemViewType(position) == TYPE_PERSON);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case TYPE_PERSON:
                    convertView = inflater.inflate(R.layout.custom_list_view_item, parent, false);
                    break;
                case TYPE_DIVIDER:
                    convertView = inflater.inflate(R.layout.custom_list_view_seperator, parent, false);
                    break;
            }
        }

        switch (type) {
            case TYPE_PERSON:
                IpCollector person = (IpCollector)getItem(position);
                TextView ip = (TextView)convertView.findViewById(R.id.list_view_item);
                ip.setText(person.getIp());
                break;
            case TYPE_DIVIDER:
                TextView title = (TextView)convertView.findViewById(R.id.list_view_seperator);
                String titleString = (String)getItem(position);
                title.setText(titleString);
                break;
        }

        return convertView;
    }
}