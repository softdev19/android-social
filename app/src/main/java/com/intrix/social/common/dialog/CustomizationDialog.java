package com.intrix.social.common.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.intrix.social.R;
import com.intrix.social.common.AmountBarListener.PlusClickListener;
import com.intrix.social.model.Cart;
import com.intrix.social.model.Customization;
import com.intrix.social.model.Item;
import com.intrix.social.utils.Toaster;
import com.mikepenz.iconics.utils.Utils;

import java.util.List;

import io.realm.Realm;

import static com.intrix.social.common.AmountBarListener.MinusClickListener;

/**
 * Created by yarolegovich on 20.12.2015.
 */
public class CustomizationDialog {

    public static void showFor(Context context, final Item item, DialogInterface.OnCancelListener onDone) {
        List<Customization> customizations = Realm.getDefaultInstance()
                .where(Customization.class).equalTo("itemId", item.getId())
                .findAll();

        if (customizations.isEmpty()) {
            Toaster.showToast("No customizations available");
        } else {
            final Cart cart = Cart.instance();
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            int spaceHeight = Utils.convertDpToPx(context, 16);
            Space space = new Space(context);
            space.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, spaceHeight));
            layout.addView(space);

            int paddingLeft = Utils.convertDpToPx(context, 24);
            int paddingRight = Utils.convertDpToPx(context, 8);
            int padding = Utils.convertDpToPx(context, 4);

            for (final Customization customization : customizations) {
                View v = LayoutInflater.from(context).inflate(R.layout.item_customizable_ingredient, layout, false);
                TextView textView = (TextView) v.findViewById(R.id.item_name);
                v.setPadding(paddingLeft, padding, paddingRight, padding);

                TextView amount = (TextView) v.findViewById(R.id.item_amount);
                int noOfThisCustomization = cart.getNoOfCustomizations(item.getId(), customization.getId());
                amount.setText(String.valueOf(noOfThisCustomization));

                View plus = v.findViewById(R.id.amount_plus);
                plus.setOnClickListener(new PlusClickListener(item.getId(), customization.getId(), amount));

                View minus = v.findViewById(R.id.amount_minus);
                minus.setOnClickListener(new MinusClickListener(item.getId(), customization.getId(), amount));

                textView.setText(customization.getDescription() + "_" + customization.getPrice());
                layout.addView(v);
            }
            new AlertDialog.Builder(context)
                    .setView(layout)
                    .setTitle("Customize")
                    .setPositiveButton("Confirm", null)
                    .setOnCancelListener(onDone)
                    .show();
        }
    }
}
