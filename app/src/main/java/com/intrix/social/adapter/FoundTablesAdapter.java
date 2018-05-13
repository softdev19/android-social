package com.intrix.social.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.intrix.social.R;
import com.intrix.social.networking.model.TablesResponse;

import java.util.List;

/**
 * Created by yarolegovich on 27.01.2016.
 */
public class FoundTablesAdapter extends ArrayAdapter<TablesResponse> {

    public FoundTablesAdapter(Context context, List<TablesResponse> data) {
        super(context, 0, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = getContext();
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_table, parent, false);
        }

        TablesResponse table = getItem(position);

        TextView capacity = (TextView) convertView.findViewById(R.id.table_capacity);
        //capacity.setText(context.getString(R.string.table_capacity, table.getTableCapacity()));
        capacity.setText("");

        TextView number = (TextView) convertView.findViewById(R.id.table_number);
        number.setText(context.getString(R.string.table_number, table.getTableId()));

        return convertView;
    }
}
