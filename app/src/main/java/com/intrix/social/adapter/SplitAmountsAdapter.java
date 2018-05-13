package com.intrix.social.adapter;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.intrix.social.R;
import com.intrix.social.model.Cart;
import com.intrix.social.model.CustomerMini;
import com.intrix.social.model.event.NetworkEvent;

import java.util.ArrayList;
import java.util.Date;

import de.greenrobot.event.EventBus;

/**
 * Created by sutharsha on 02/01/16.
 */
public class SplitAmountsAdapter extends ArrayAdapter<CustomerMini> {

    public final String TAG = "SplitAmountsAdapter";

    Activity mContext;

    Date localDate = new Date();

    ArrayList<CustomerMini> tags;

    private static class ListViewHolder {
        TextView itmName;
        EditText itmAmount;
        ImageView pic;
        int id;
    }

    public SplitAmountsAdapter(Activity context, ArrayList<CustomerMini> customers) {
        super(context, R.layout.item_my_people, customers);
        mContext = context;
        tags = customers;
        localDate = new Date();
    }

    @Override
    public View getView(int position,View view, ViewGroup parent) {

        CustomerMini tag = getItem(position);
        ListViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ListViewHolder();
            LayoutInflater inflater = mContext.getLayoutInflater();
            view = inflater.inflate(R.layout.split_amount_item, null, true);
            viewHolder.itmName = (TextView) view.findViewById(R.id.Item_name);
            viewHolder.itmAmount = (EditText) view.findViewById(R.id.Item_price);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ListViewHolder) view.getTag();
// loadSavedValues();
        }
        viewHolder.itmName.setText(tag.getName());
        viewHolder.itmAmount.setId(position);
        viewHolder.id = position;
        if (tag.getAmount() != null) {
            viewHolder.itmAmount.setText("" + tag.getAmount());
        } else {
            viewHolder.itmAmount.setText(null);
        }
// Add listener for edit text
        viewHolder.itmAmount
                .setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
/*
 * When focus is lost save the entered value for
 * later use
 */
                        if (!hasFocus) {
                            int itemIndex = v.getId();
                            String enteredPrice = ((EditText) v).getText()
                                    .toString();
                            int amountInt = 0;
                            if(enteredPrice.length() > 0)
                                amountInt = Integer.parseInt(enteredPrice);
                            tags.get(itemIndex).setAmount(amountInt);
                            Cart.instance().saveTags();
                            EventBus.getDefault().post(new NetworkEvent("splitPaymentAmountsChanged", true));
                        }
//                        InputMethodManager imm = (InputMethodManager)v.getWindow().getContext().getSystemService(mContext.INPUT_METHOD_SERVICE);
//                        if(hasFocus)
//                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                    }
                });

        viewHolder.itmAmount.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int itemIndex = viewHolder.itmAmount.getId();
                    String enteredPrice = viewHolder.itmAmount.getText()
                            .toString();
                    int amountInt = 0;
                    if(enteredPrice.length() > 0)
                        amountInt = Integer.parseInt(enteredPrice);
                    Log.i(TAG, "current amount after change - "+ amountInt);
                    tags.get(itemIndex).setAmount(amountInt);
                    //Cart.instance().saveTags();
                    EventBus.getDefault().post(new NetworkEvent("splitPaymentAmountsChanged", true));

                } catch (Exception e) {
                    Log.v("State", e.getMessage());
                }
            }
        });


        return view;
        //Glide.with(mContext).load(user.getPic()).error(R.drawable.people_full).into(viewHolder.pic);
    }
}