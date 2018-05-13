package com.intrix.social.common.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intrix.social.R;
import com.intrix.social.model.Cart;
import com.intrix.social.model.Customization;
import com.intrix.social.model.Item;

import static com.intrix.social.common.AmountBarListener.*;

import java.util.List;

/**
 * Created by yarolegovich on 22.11.2015.
 */
public class IngredientsPanel extends LinearLayout implements CompoundButton.OnCheckedChangeListener {

    private CompoundButton.OnCheckedChangeListener mAfterCheckedChangeListener;

    private View.OnClickListener mAfterClickListener;

    private Item mItem;

    public IngredientsPanel(Context context) {
        super(context);
        init();
    }

    public IngredientsPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IngredientsPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IngredientsPanel(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_VERTICAL);
    }

    public void initIngredientsPanel(Item item, List<Customization> ingredients) {
        Cart cart = Cart.instance();
        mItem = item;
        for (Customization customization : ingredients) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.item_customizable_ingredient, this, false);
            TextView name = (TextView) v.findViewById(R.id.item_name);

            View amountPlus = v.findViewById(R.id.amount_plus);
            View amountMinus = v.findViewById(R.id.amount_minus);
            TextView amount = (TextView) v.findViewById(R.id.item_amount);

            AmountClickListener plusListener = new PlusClickListener(mItem.getId(), customization.getId(), amount);
            plusListener.setAfterClickListener(mAfterClickListener);
            amountPlus.setOnClickListener(plusListener);

            AmountClickListener minusListener = new MinusClickListener(mItem.getId(), customization.getId(), amount);
            minusListener.setAfterClickListener(mAfterClickListener);
            amountMinus.setOnClickListener(minusListener);

            amount.setText(String.valueOf(cart.getNoOfCustomizations(mItem.getId(), customization.getId())));

            name.setText(customization.getName()+"_"+customization.getPrice());
            addView(v);
        }
    }

    public void resetIngredients() {
        for (int i = 0; i < getChildCount(); i++) {
            //((CheckBox) getChildAt(i).findViewById(R.id.item_added)).setChecked(false);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Cart cart = Cart.instance();
        Customization customization = (Customization) buttonView.getTag();
        if (isChecked) {
            cart.addCustomization(mItem.getId(), customization.getId());
        } else {
            cart.removeCustomization(mItem.getId(), customization.getId());
        }
        if (mAfterCheckedChangeListener != null) {
            mAfterCheckedChangeListener.onCheckedChanged(buttonView, isChecked);
        }
    }

    public void setAfterCheckedChangeListener(CompoundButton.OnCheckedChangeListener afterCheckedChangeListener) {
        mAfterCheckedChangeListener = afterCheckedChangeListener;
    }

    public void setAfterClickListener(View.OnClickListener afterClickListener) {
        mAfterClickListener = afterClickListener;
    }
}
