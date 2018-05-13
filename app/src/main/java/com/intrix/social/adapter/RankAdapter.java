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
import com.intrix.social.model.CustomerMini;

import java.util.ArrayList;

/**
 * Created by sutharsha on 02/01/16.
 */
public class RankAdapter extends ArrayAdapter<CustomerMini> {

    Context mContext;
//    public UsersAdapter(Context context, ArrayList<Customer> users) {
//        super(context, 0, users);
//        mContext = context;
//    }


    private static class ViewHolder {
        TextView name;
        TextView info;
        TextView rankView;
        ImageView pic;
    }

    public RankAdapter(Context context, ArrayList<CustomerMini> users) {
        super(context, R.layout.item_my_people, users);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CustomerMini user = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_rank, parent, false);

            viewHolder.name = (TextView) convertView.findViewById(R.id.tv_title_item_my_people);
            viewHolder.info = (TextView) convertView.findViewById(R.id.tv_second_title_item_my_people);
            viewHolder.pic = (ImageView) convertView.findViewById(R.id.iv_avatar_item_my_people);
            viewHolder.rankView = (TextView) convertView.findViewById(R.id.rank_text);
            convertView.setTag(viewHolder);
        }else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(user.getName());
        if(user.getCity() != null && user.getCity().length() > 0)
            viewHolder.info.setText(user.getCity());
        else
            viewHolder.info.setText("");
        viewHolder.rankView.setText(""+user.getRank());
//        if(user.getInterest1() != null && user.getInterest2() != null && user.getInterest3() != null && user.getInterest1().length() > 0 && user.getInterest1().length() > 0  && user.getInterest1().length() > 0 ) {
//            String interests = "#" + user.getInterest1() + " #" + user.getInterest2() + " #" + user.getInterest3();
//            viewHolder.info.setText(interests);
//        }else
//            viewHolder.info.setText("");


        Glide.with(mContext).load(user.getPic()).error(R.drawable.people_full).into(viewHolder.pic);

        return convertView;
    }
}