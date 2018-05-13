package com.intrix.social.adapter;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.intrix.social.R;

import java.util.List;

/**
 * Created by yarolegovich on 22.12.2015.
 */
public class EventsSpinnerAdapter extends ArrayAdapter<String> {

    @DrawableRes
    private int mIcon;

    public EventsSpinnerAdapter(Context context, List<String> data) {
        super(context, 0, data);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item_event, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else vh = (ViewHolder) convertView.getTag();

        vh.text.setText(getItem(position));
        vh.icon.setImageResource(mIcon);

        return convertView;
    }

    public void setIcon(@DrawableRes int icon) {
        mIcon = icon;
    }

    private static class ViewHolder {
        ImageView icon;
        TextView text;

        public ViewHolder(View v) {
            icon = (ImageView) v.findViewById(R.id.icon);
            text = (TextView) v.findViewById(R.id.text);
        }
    }
}
