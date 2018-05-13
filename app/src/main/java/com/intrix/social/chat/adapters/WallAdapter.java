package com.intrix.social.chat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.intrix.social.R;
import com.intrix.social.chat.model.Msg;
import com.mikepenz.materialdrawer.view.BezelImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yarolegovich on 24.12.2015.
 */
public class WallAdapter extends ArrayAdapter<Msg> {

    public WallAdapter(Context context) {
        this(context, new ArrayList<Msg>());
    }

    public WallAdapter(Context context, List<Msg> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_wall_message, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } vh = (ViewHolder) convertView.getTag();

        Msg msg = getItem(position);

        vh.content.setText(msg.getContent());
        vh.name.setText(getContext().getString(R.string.wall_username, msg.getUsername()));

        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        TextView content;
        BezelImageView image;

        public ViewHolder(View v) {
            name = (TextView) v.findViewById(R.id.user_name);
            content = (TextView) v.findViewById(R.id.content);
            image = (BezelImageView) v.findViewById(R.id.image);
        }
    }
}
