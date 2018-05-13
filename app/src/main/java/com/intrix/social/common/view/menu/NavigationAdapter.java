package com.intrix.social.common.view.menu;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.intrix.social.R;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.List;

/**
 * Created by yarolegovich on 01.01.2016.
 */
public class NavigationAdapter extends ArrayAdapter<AppMenuItem> {

    public NavigationAdapter(Context context, List<AppMenuItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_item_menu, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else vh = (ViewHolder) convertView.getTag();

        AppMenuItem item = getItem(position);

        vh.icon.setIcon(item.getIcon());
        vh.icon.setColorFilter(Color.WHITE);
        vh.title.setText(item.getTitle());
        int notification = item.getNotifications();
        if (notification > 0) {
            vh.notifications.setVisibility(View.VISIBLE);
            vh.notifications.setText(String.valueOf(notification));
        } else {
            vh.notifications.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    private static class ViewHolder {

        private IconicsImageView icon;
        private TextView title;
        private TextView notifications;

        public ViewHolder(View v) {
            icon = (IconicsImageView) v.findViewById(R.id.item_icon);
            title = (TextView) v.findViewById(R.id.item_name);
            notifications = (TextView) v.findViewById(R.id.item_notifications);
        }
    }
}
