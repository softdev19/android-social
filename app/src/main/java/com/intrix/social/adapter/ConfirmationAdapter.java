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
public class ConfirmationAdapter extends ArrayAdapter<Item> {

    public ConfirmationAdapter(Context context, List<Item> data) {
        super(context, 0, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_confirmation, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else vh = (ViewHolder) convertView.getTag();


        Item item = getItem(position);

        vh.mItem = item;

        vh.name.setText(item.getName());
        vh.amount.setText(String.valueOf(Cart.instance().getAmountForNotConfirmed(item.getId())));

        vh.clearPreviousItems();
        Realm realm = Realm.getDefaultInstance();
        for (Customization c : Cart.instance().getCustomizationsForItem(realm, item.getId())) {
            TextView v = (TextView) LayoutInflater.from(getContext())
                    .inflate(R.layout.item_added_ingredient, vh.itemsAdded, false);
            v.setText(c.getDescription());
            vh.addItem(v);
        }
        realm.close();

        return convertView;
    }

    private static class ViewHolder implements View.OnClickListener {

        private Item mItem;

        TextView amount;
        TextView name;

        ViewGroup itemsAdded;

        public ViewHolder(View view) {
            amount = (TextView) view.findViewById(R.id.item_amount);
            name = (TextView) view.findViewById(R.id.item_name);
            itemsAdded = (ViewGroup) view.findViewById(R.id.items_added);

            view.findViewById(R.id.amount_minus).setOnClickListener(this);
            view.findViewById(R.id.amount_plus).setOnClickListener(this);
        }

        public void addItem(View v) {
            itemsAdded.addView(v);
        }

        public void clearPreviousItems()
        {
            itemsAdded.removeAllViews();
        }

        @Override
        public void onClick(View v) {
            Cart cart = Cart.instance();
            int am;
            switch (v.getId()) {
                case R.id.amount_minus:
                    cart.removeItem(mItem.getId());
                    am = cart.getAmountForNotConfirmed(mItem.getId());
                    amount.setText(String.valueOf(am));
                    break;
                case R.id.amount_plus:
                    cart.putItem(mItem.getId());
                    am = cart.getAmountForNotConfirmed(mItem.getId());
                    amount.setText(String.valueOf(am));
                    break;
            }
        }
    }
}
