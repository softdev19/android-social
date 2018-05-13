package com.intrix.social.common;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.intrix.social.model.Cart;

/**
 * Created by yarolegovich on 08.01.2016.
 */
public class AmountBarListener {

    public static abstract class AmountClickListener implements View.OnClickListener {
        protected int mItem;
        protected int mCustomization;
        protected TextView mAmount;

        protected View.OnClickListener mAfterClickListener;

        private AmountClickListener(int item, int customization, TextView amount) {
            mItem = item;
            mCustomization = customization;
            mAmount = amount;
        }

        @Override
        public final void onClick(View v) {
            onClickAction(v);
            if (mAfterClickListener != null) {
                mAfterClickListener.onClick(v);
            }
        }

        protected abstract void onClickAction(View v);

        public void setAfterClickListener(View.OnClickListener afterClickListener) {
            mAfterClickListener = afterClickListener;
        }
    }

    public static class MinusClickListener extends AmountClickListener {

        public MinusClickListener(int item, int customization, TextView amount) {
            super(item, customization, amount);
        }

        @Override
        public void onClickAction(View v) {
            Cart cart = Cart.instance();
            int noOfCust = cart.getNoOfCustomizations(mItem, mCustomization);
            if (noOfCust != 0) {
                cart.removeCustomization(mItem, mCustomization);
                mAmount.setText(String.valueOf(--noOfCust));
            }
        }
    }

    public static class PlusClickListener extends AmountClickListener {

        public PlusClickListener(int item, int customization, TextView amount) {
            super(item, customization, amount);
        }

        @Override
        public void onClickAction(View v) {
            Cart cart = Cart.instance();
            int noOfCust = cart.getNoOfCustomizations(mItem, mCustomization);
            int noOfItem = cart.getAmountForNotConfirmed(mItem);
            if (noOfCust < noOfItem) {
                cart.addCustomization(mItem, mCustomization);
                mAmount.setText(String.valueOf(++noOfCust));
            } else {
                Toast.makeText(mAmount.getContext().getApplicationContext(),
                        "Not enough items in cart",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

}
