package com.intrix.social.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.intrix.social.R;
import com.intrix.social.model.Cart;
import com.intrix.social.model.Customization;
import com.intrix.social.model.Item;

import java.util.List;

import io.realm.Realm;

/**
 * Created by yarolegovich on 22.11.2015.
 */
public class DamagesAdapter extends ArrayAdapter<Item> implements View.OnClickListener {

    private static final String LOG_TAG = DamagesAdapter.class.getSimpleName();

    private int mLayoutRes = R.layout.item_damage;

    private boolean mViewAll;
    private View mViewMoreView;
    private boolean isMine = false;

    public DamagesAdapter(Context context, List<Item> data) {
        super(context, 0, data);
    }

    public DamagesAdapter(Context context, List<Item> data, boolean mine) {
        super(context, 0, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(mLayoutRes, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else vh = (ViewHolder) convertView.getTag();

        Item item = getItem(position);

        vh.name.setText(item.getName());
        int amount = 0;
        if(isMine)
            amount = Cart.instance().getAmountForMyOrders(item.getId());
        else
            amount = Cart.instance().getAmountForFullOrders(item.getId());
        vh.amount.setText(String.valueOf(amount));
        vh.price.setText(String.valueOf(amount * Integer.parseInt(item.getPrice())));

        Realm realm = Realm.getDefaultInstance();
        List<Customization> customizations = Cart.instance().getCustomizationsForItem(realm, item.getId());
        vh.itemsAdded.removeAllViews();
        for (Customization c : customizations) {
            TextView v = (TextView) LayoutInflater.from(getContext())
                    .inflate(R.layout.item_added_ingredient, vh.itemsAdded, false);
            v.setText(c.getDescription());
            vh.addItem(v);
        }
        realm.close();

        return convertView;
    }

    @Override
    public int getCount() {
        return mViewMoreView == null ? super.getCount() :
                mViewAll ? super.getCount() : Math.min(3, super.getCount());
    }

    public void setViewMoreView(View viewMoreView) {
        mViewMoreView = viewMoreView;
        mViewMoreView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mViewAll = true;
        mViewMoreView.setVisibility(View.GONE);
        notifyDataSetChanged();
    }

    public void setLayoutRes(int layoutRes) {
        mLayoutRes = layoutRes;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView name;
        TextView amount;
        TextView price;

        ViewGroup itemsAdded;

        public ViewHolder(View v) {
            name = (TextView) v.findViewById(R.id.item_name);
            amount = (TextView) v.findViewById(R.id.item_amount);
            price = (TextView) v.findViewById(R.id.item_price);

            itemsAdded = (ViewGroup) v.findViewById(R.id.items_added);
        }

        public void addItem(View v) {
            itemsAdded.addView(v);
        }
    }
}
