package com.intrix.social.common.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.intrix.social.R;
import com.intrix.social.adapter.SplitAmountsAdapter;
import com.intrix.social.model.Cart;
import com.intrix.social.model.Contact;
import com.intrix.social.model.CustomerMini;
import com.intrix.social.model.Split;
import com.intrix.social.model.event.NetworkEvent;
import com.intrix.social.utils.Callback;
import com.intrix.social.utils.Toaster;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.realm.Realm;

/**
 * Created by yarolegovich on 15.01.2016.
 */
public class SplitDialog extends Object{

    private static final String  TAG = "SplitDialog";

    public static View fullView = null;
    static TextView myAmount;
    static Activity mContext;
    public  void show(Activity context, List<Contact> splitBetween, Callback<Split> onSplitCreated) {
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_split_type, null);

        mContext = context;
        fullView = v;
        EventBus.getDefault().register(this);
        CheckBox equally = (CheckBox) v.findViewById(R.id.cb_equally);
        CheckBox specific = (CheckBox) v.findViewById(R.id.cb_you_pay);
        myAmount = (TextView) v.findViewById(R.id.amount);
        ListView amountsList = (ListView) v.findViewById(R.id.amount_list);

        SplitAmountsAdapter adapter = new SplitAmountsAdapter(context, (ArrayList)Cart.instance().getTags());
        amountsList.setAdapter(adapter);

        CheckBoxGroup group = new CheckBoxGroup(equally, specific);

        group.setWrapped((buttonView, isChecked) -> {
            myAmount.setVisibility(specific.isChecked() ? View.VISIBLE : View.GONE);
            amountsList.setVisibility(specific.isChecked() ? View.VISIBLE : View.GONE);
        });

        TextView amountLabel = (TextView) v.findViewById(R.id.bill_amount);
        Cart cart = Cart.instance();
        Realm realm = Realm.getInstance(context);
        //int amountValue = cart.calculateFullOrderAmount(realm); // old one

        int subtotal = Cart.instance().calculateFullOrderAmount(realm);
        int taxes = (int) ((double) subtotal * 0.26);
        int amountValue = subtotal + taxes;

        amountLabel.setText(context.getString(R.string.bill_amount, amountValue));

        int currentTagTotal = Cart.instance().getTagTotal();
        myAmount.setText("Your amount: " + (amountValue - currentTagTotal));
        realm.close();

        AlertDialog d = new AlertDialog.Builder(context)
                .setView(v)
                .setTitle("Split type")
                .setPositiveButton("Split", (dialog, which) -> {})
                .create();
        d.show();
        d.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
            if (!equally.isChecked() && !specific.isChecked()) {
                Toaster.showToast("Please select something");
                return;
            }

            double selectedAmount;
            if (specific.isChecked()) {
                try {
                    int currentTagTotalTemp = Cart.instance().getTagTotal();
                    myAmount.setText("Your amount: " + (amountValue - currentTagTotalTemp));

//                    if (selectedAmount > amountValue || selectedAmount < 0) {
//                        Toaster.showToast("Wrong amount specified");
//                        return;
//                    }else
//                    {
//
//                    }
                    if (currentTagTotalTemp == 0) {
                        Toaster.showToast("Please specify amount for users to Split");
                        return;
                    }else if((amountValue - currentTagTotalTemp)  < 0)
                    {
                        Toaster.showToast("Please re-adjust amounts to be not more than total");
                        return;
                    }

                } catch (NumberFormatException e) {
                    Toaster.showToast("Wrong amount specified");
                    return;
                }
            } else {
                selectedAmount =  ((double) amountValue / (splitBetween.size()));
                int floorVal = (int)Math.floor(selectedAmount);

                List<CustomerMini> tags = Cart.instance().getTags();
                for(CustomerMini tag: tags)
                {
                    tag.setAmount(floorVal);
                }

            }

//            List<String> phones = new ArrayList<>();
//            for (Contact contact : splitBetween) {
//                phones.add(contact.phoneNum);
//            }

            List<String> phones = new ArrayList<>();
            List<CustomerMini> tags = Cart.instance().getTags();
            for (CustomerMini tag : tags) {
                phones.add(tag.getMobileno());
            }

            Split split = new Split();
            split.setNoOfPeople(String.valueOf(splitBetween.size() + 1));
            split.setAmount(amountValue);
            split.setOrderId(cart.getOrderId());
            split.setPhones(phones);

            cart.setSettlementType("split");
            int tagTotalNow = Cart.instance().getTagTotal();
            cart.setSplitAmount(amountValue - tagTotalNow);

            onSplitCreated.onResult(split);

            d.dismiss();
        });

        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                EventBus.getDefault().unregister(this);
            }
        });
    }

    public void onEvent(NetworkEvent event) {
        if (event.event.contains("splitPaymentAmountsChanged")) {
            if (event.status) {
                updateMyAmount();
                List<CustomerMini> tags = Cart.instance().getTags();
                for(CustomerMini tag: tags) {
                    Log.i(TAG, "tag amount in splitDiaog " + tag.getAmount() + " - " + tag.getName());
                }
                Log.i(TAG, "splitPaymentAmountsChanged success");
            } else {
                Log.i(TAG, "splitPaymentAmountsChanged failed");
            }
        }
    }

    public static void updateMyAmount()
    {
        Realm realm = Realm.getInstance(mContext);
        int subtotal = Cart.instance().calculateFullOrderAmount(realm);
        int taxes = (int) ((double) subtotal * 0.26);
        int amountValue = subtotal + taxes;

        int currentTagTotal = Cart.instance().getTagTotal();
        myAmount.setText("Your amount: " + (amountValue - currentTagTotal));
        realm.close();
    }


    private static class CheckBoxGroup implements CompoundButton.OnCheckedChangeListener {

        private CheckBox[] mCheckBoxes;
        private CompoundButton.OnCheckedChangeListener mWrapped;

        private CheckBoxGroup(CheckBox... checkBoxes) {
            mCheckBoxes = checkBoxes;
            for (CheckBox checkBox : checkBoxes) {
                checkBox.setOnCheckedChangeListener(this);
            }
            onCheckedChanged(mCheckBoxes[0], true);
        }


        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mWrapped != null) {
                mWrapped.onCheckedChanged(buttonView, isChecked);
            }
            if (!isChecked) {
                return;
            }
            for (CheckBox checkBox : mCheckBoxes) {
                if (checkBox != buttonView) {
                    checkBox.setChecked(false);
                } else {
                    checkBox.setChecked(true);
                }
            }
        }

        public int getCheckedId() {
            for (CheckBox checkBox : mCheckBoxes) {
                if (checkBox.isChecked()) {
                    return checkBox.getId();
                }
            }
            return -1;
        }

        public void setWrapped(CompoundButton.OnCheckedChangeListener wrapped) {
            mWrapped = wrapped;
        }
    }
}
