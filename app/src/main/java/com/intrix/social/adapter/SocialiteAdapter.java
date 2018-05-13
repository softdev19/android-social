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
import java.util.TreeSet;

/**
 * Created by sutharsha on 02/01/16.
 */
public class SocialiteAdapter extends ArrayAdapter<CustomerMini> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    final ArrayList<CustomerMini> mData = new ArrayList<CustomerMini>();
    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

    Context mContext;
    LayoutInflater inflater;
//    public UsersAdapter(Context context, ArrayList<Customer> users) {
//        super(context, 0, users);
//        mContext = context;
//    }

    private static class ViewHolder {
        TextView name;
        TextView info;
        ImageView pic;
    }

    public SocialiteAdapter(Context context, ArrayList<CustomerMini> users) {
        super(context, R.layout.item_my_people, users);
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    public SocialiteAdapter(Context context) {
        super(context, R.layout.item_my_people);
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CustomerMini user = getItem(position);
        int rowType = getItemViewType(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();

            switch (rowType) {
                case TYPE_ITEM:
                    convertView = inflater.inflate(R.layout.item_my_people, parent, false);
                    viewHolder.name = (TextView) convertView.findViewById(R.id.tv_title_item_my_people);
                    viewHolder.info = (TextView) convertView.findViewById(R.id.tv_second_title_item_my_people);
                    viewHolder.pic = (ImageView) convertView.findViewById(R.id.iv_avatar_item_my_people);
                    break;
                case TYPE_SEPARATOR:
                    convertView = inflater.inflate(R.layout.item_socialite_header, null);
                    viewHolder.name = (TextView) convertView.findViewById(R.id.textSeparator);
                    break;
            }

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        switch (rowType) {
            case TYPE_ITEM:
                viewHolder.name.setText(user.getName());
                if (user.getInterest1() != null && user.getInterest2() != null && user.getInterest3() != null && user.getInterest1().length() > 0 && user.getInterest1().length() > 0 && user.getInterest1().length() > 0) {
                    String interests = "#" + user.getInterest1() + " #" + user.getInterest2() + " #" + user.getInterest3();
                    viewHolder.info.setText(interests);
                } else
                    viewHolder.info.setText("");
                Glide.with(mContext).load(user.getPic()).error(R.drawable.people_full).into(viewHolder.pic);
                break;
            case TYPE_SEPARATOR:
                viewHolder.name.setText(user.getName());
//                if (position == 0)
//                    viewHolder.name.setText("Around you at SOCIAL");
//                else
//                    viewHolder.name.setText("SOCIALITES");
                break;
        }

        return convertView;
    }

    public void addItem(final CustomerMini customerMini) {
        mData.add(customerMini);
        notifyDataSetChanged();
    }


    public void addSectionHeaderItem(final CustomerMini item) {
        //add(item);
        mData.add(item);
        sectionHeader.add(getCount() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }


    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public CustomerMini getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}