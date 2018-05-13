package com.intrix.social.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.intrix.social.R;
import com.intrix.social.model.Customer;
import com.intrix.social.networking.model.Chat;
import com.intrix.social.utils.Utils;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sutharsha on 02/01/16.
 */
public class ChatsAdapter extends ArrayAdapter<Chat> {

    Context mContext;

    Date localDate = new Date();

    ArrayList<Customer> customers;

    private static class ViewHolder {
        TextView name;
        TextView info;
        ImageView pic;
    }

    public ChatsAdapter(Context context, ArrayList<Chat> chats, ArrayList<Customer> customers) {
        super(context, R.layout.item_my_people, chats);
        this.customers = customers;
        mContext = context;
        localDate = new Date();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Chat chat = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_my_people, parent, false);

            viewHolder.name = (TextView) convertView.findViewById(R.id.tv_title_item_my_people);
            viewHolder.info = (TextView) convertView.findViewById(R.id.tv_second_title_item_my_people);
            viewHolder.pic = (ImageView) convertView.findViewById(R.id.iv_avatar_item_my_people);
            convertView.setTag(viewHolder);
        }else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Customer user = customers.get(position);

        if(user.getName() != null && user.getName().length() > 0)
            viewHolder.name.setText(user.getName());
        else
            viewHolder.name.setText("User");

        Date itemDate = Utils.dateConverter(chat.getUpdated_at());
        PrettyTime t = new PrettyTime(localDate);
        viewHolder.info.setText(t.format(itemDate));
        Glide.with(mContext).load(user.getPic()).error(R.drawable.people_full).into(viewHolder.pic);

        return convertView;
    }
}