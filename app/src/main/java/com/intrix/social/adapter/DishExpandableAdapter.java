package com.intrix.social.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.intrix.social.R;
import com.intrix.social.common.dialog.CustomizationDialog;
import com.intrix.social.model.Cart;
import com.intrix.social.model.Customization;
import com.intrix.social.model.Item;
import com.intrix.social.model.event.ItemAddedToCartEvent;
import com.intrix.social.utils.Toaster;

import java.util.List;

import de.greenrobot.event.EventBus;
import io.realm.Realm;

/**
 * Created by yarolegovich on 22.11.2015.
 */
public class DishExpandableAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<Item> mData;

    public DishExpandableAdapter(Context context, List<Item> items) {
        mContext = context;
        mData = items;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ParentViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_expandable_parent, parent, false);
            vh = new ParentViewHolder(convertView);
            convertView.setTag(vh);
        } else vh = (ParentViewHolder) convertView.getTag();

        Item item = mData.get(groupPosition);

        vh.itemPrice.setText(item.getPrice());
        vh.itemName.setText(item.getName());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_expandable_child, parent, false);
            vh = new ChildViewHolder(convertView);
            convertView.setTag(vh);
        } else vh = (ChildViewHolder) convertView.getTag();

        Item item = mData.get(groupPosition);

        vh.mItem = item;

        vh.amount.setText(String.valueOf(Cart.instance().getAmountForNotConfirmed(item.getId())));
        vh.description.setText(item.getDescription());

        Realm realm = Realm.getDefaultInstance();
        List<Customization> all = realm.where(Customization.class).equalTo("itemId", item.getId()).findAll();
        if (all.isEmpty()) {
            vh.container.setVisibility(View.GONE);
            vh.customize.setVisibility(View.GONE);
        } else {
            List<Customization> customizations = Cart.instance()
                    .getCustomizationsForItem(realm, item.getId());
            vh.container.removeAllViews();
            vh.customize.setVisibility(View.VISIBLE);
            vh.container.setVisibility(View.VISIBLE);
            for (Customization c : customizations) {
                TextView v = (TextView) LayoutInflater.from(mContext)
                        .inflate(R.layout.item_added_ingredient, vh.container, false);
                v.setText(c.getDescription());
                vh.container.addView(v);
            }
        }
        realm.close();

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public int getGroupCount() {
        return mData.size();
    }

    private static class ParentViewHolder {
        TextView itemName;
        TextView itemPrice;

        public ParentViewHolder(View v) {
            itemName = (TextView) v.findViewById(R.id.item_name);
            itemPrice = (TextView) v.findViewById(R.id.item_price);
        }

    }

    private class ChildViewHolder implements View.OnClickListener {

        private Item mItem;

        TextView description;
        TextView amount;

        ViewGroup container;
        View customize;
        View preference;

        public ChildViewHolder(View v) {
            description = (TextView) v.findViewById(R.id.item_description);
            amount = (TextView) v.findViewById(R.id.item_amount);
            container = (ViewGroup) v.findViewById(R.id.container);
            customize = v.findViewById(R.id.customize);
            preference = v.findViewById(R.id.preferences);

            v.findViewById(R.id.amount_minus).setOnClickListener(this);
            v.findViewById(R.id.amount_plus).setOnClickListener(this);
            customize.setOnClickListener(this);
            preference.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Cart cart = Cart.instance();
            switch (v.getId()) {
                case R.id.amount_minus:
                    cart.removeItem(mItem.getId());
                    int items = cart.getAmountForNotConfirmed(mItem.getId());
                    amount.setText(String.valueOf(items));
                    if (items == 0) {
                        notifyDataSetChanged();
                    }
                    EventBus.getDefault().post(new ItemAddedToCartEvent());
                    break;
                case R.id.amount_plus:
                    cart.putItem(mItem.getId());
                    amount.setText(String.valueOf(cart.getAmountForNotConfirmed(mItem.getId())));
                    EventBus.getDefault().post(new ItemAddedToCartEvent());
                    break;
                case R.id.customize:
                    items = cart.getAmountForNotConfirmed(mItem.getId());
                    if (items == 0) {
                        Toaster.showToast("First add item to cart");
                        return;
                    }
                    CustomizationDialog.showFor(mContext, mItem, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            notifyDataSetChanged();
                        }
                    });
                    break;
                case R.id.preferences:
                    items = cart.getAmountForNotConfirmed(mItem.getId());
                    if (items == 0) {
                        Toaster.showToast("First add item to cart");
                        return;
                    }
                    showSpecialBox(mItem);
//                    CustomizationDialog.showFor(mContext, mItem, new DialogInterface.OnCancelListener() {
//                        @Override
//                        public void onCancel(DialogInterface dialog) {
//                            notifyDataSetChanged();
//                        }
//                    });
                    break;
            }
        }
    }


    private void showSpecialBox(Item mItem) {
        LayoutInflater li = LayoutInflater.from(mContext);
        View promptsView = li.inflate(R.layout.dialog_preferences, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.userInput);
        String oldComment = Cart.instance().getSpecial(mItem.getId());
        userInput.setText(oldComment);
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String commentText = userInput.getText().toString();
                                if (commentText.length() == 0) {
                                    Cart.instance().clearSpecial(mItem.getId());
                                    if(oldComment.length() > 0)
                                        Toaster.toast("Special instructions cleared for " + mItem.getName());
                                    return;
                                } else {
                                    //Cart.instance().clearSpecial(mItem.getId());
                                    Cart.instance().setSpecial(mItem.getId(), commentText);
                                    dialog.dismiss();
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
}
