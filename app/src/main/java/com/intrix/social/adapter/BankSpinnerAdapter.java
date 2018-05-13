package com.intrix.social.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Created by yarolegovich on 16.12.2015.
 */
public class BankSpinnerAdapter extends ArrayAdapter<String> {

    public BankSpinnerAdapter(Context context, int resource) {
        super(context, resource);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

}
