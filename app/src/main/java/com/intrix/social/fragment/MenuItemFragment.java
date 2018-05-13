package com.intrix.social.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.intrix.social.PagerImagesAdapter;
import com.intrix.social.R;
import com.intrix.social.UniversalActivity;
import com.intrix.social.common.view.ShareDialog;
import com.intrix.social.model.Cart;
import com.intrix.social.model.Item;
import com.intrix.social.model.event.ItemAddedToCartEvent;
import com.intrix.social.utils.Toaster;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.Arrays;

import de.greenrobot.event.EventBus;
import io.realm.Realm;

/**
 * Created by yarolegovich on 29.12.2015.
 */
public class MenuItemFragment extends Fragment implements View.OnClickListener {

    private static final String LOG_TAG = MenuItemFragment.class.getSimpleName();

    public static final String ITEM_ID = "item_id";

    private Realm mRealm;

    private FloatingActionMenu mFabMenu;

    private TextView mLikes;
    private TextView mAmount;

    private Item mItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getInstance(getActivity());
        int id = getActivity().getIntent().getIntExtra(ITEM_ID, -1);
        mItem = mRealm.where(Item.class).equalTo("id", id).findFirst();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_menu_item, container, false);

        ViewPager pager = (ViewPager) v.findViewById(R.id.pager);
        PagerImagesAdapter adapter = new PagerImagesAdapter(getChildFragmentManager(),
                Arrays.asList(mItem.getImage(), mItem.getImage())
        );
        pager.setAdapter(adapter);

        Cart cart = Cart.instance();
        mAmount = (TextView) v.findViewById(R.id.item_amount);
        mAmount.setText(String.valueOf(cart.getAmountForNotConfirmed(mItem.getId())));

        TextView price = (TextView) v.findViewById(R.id.item_price);
        price.setText(mItem.getPrice());

        CirclePageIndicator indicator = (CirclePageIndicator) v.findViewById(R.id.pager_indicator);
        indicator.setViewPager(pager);

        TextView name = (TextView) v.findViewById(R.id.item_name);
        name.setText(mItem.getName());
        TextView descr = (TextView) v.findViewById(R.id.item_description);
        descr.setText(mItem.getDescription());
        mLikes = (TextView) v.findViewById(R.id.item_favorites);
        mLikes.setText(mItem.getFav());
        TextView comments = (TextView) v.findViewById(R.id.item_comments);
        comments.setText(mItem.getFav());

        final View overlay = v.findViewById(R.id.overlay);
        mFabMenu = (FloatingActionMenu) v.findViewById(R.id.fmenu);
        mFabMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                overlay.setVisibility(opened ? View.VISIBLE : View.GONE);
            }
        });

        mFabMenu.findViewById(R.id.fab_share).setOnClickListener(this);
        mFabMenu.findViewById(R.id.fab_order).setOnClickListener(this);
        mFabMenu.findViewById(R.id.fab_customize).setOnClickListener(this);
        //mFabMenu.findViewById(R.id.fab_favorite).setOnClickListener(this);
        mFabMenu.findViewById(R.id.fab_confirm).setOnClickListener(this);

        v.findViewById(R.id.back).setOnClickListener(this);

        v.findViewById(R.id.amount_minus).setOnClickListener(this);
        v.findViewById(R.id.amount_plus).setOnClickListener(this);

        v.findViewById(R.id.customize).setOnClickListener(this);

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_order:
            case R.id.amount_plus:
                plusAmount();
                break;
            case R.id.amount_minus:
                minusAmount();
                break;
            case R.id.fab_share:
                String toShare = mItem.getName() + '\n' +
                        mItem.getDescription() + '\n' +
                        mItem.getImage();
                ShareDialog.showToShare(getActivity(), toShare);
                break;
            case R.id.fab_customize:
                mFabMenu.close(true);
            case R.id.customize:
                customize();
                break;
//            case R.id.fab_favorite:
//                int favorite = Integer.parseInt(mItem.getFav());
//                mRealm.beginTransaction();
//                mItem.setFav(String.valueOf(favorite + 1));
//                mRealm.copyToRealmOrUpdate(mItem);
//                mRealm.commitTransaction();
//                mLikes.setText(mItem.getFav());
//                EventBus.getDefault().post(new RefreshMenuPagesEvent());
//                Networker.getInstance().favorite(mItem);
//                mFabMenu.close(true);
//                break;
            case R.id.back:
                getActivity().finish();
                break;
            case R.id.fab_confirm:
                    Intent i = new Intent(getActivity(), UniversalActivity.class);
                    i.putExtra(UniversalActivity.EXTRA_TOKEN, ConfirmFragment.class);
                    startActivity(i);
                break;
        }
    }

    private void customize() {
        Cart cart = Cart.instance();
        int items = cart.getAmountForNotConfirmed(mItem.getId());
        if (items == 0) {
            Toaster.showToast("First order an item");
            return;
        }else
            showSpecialBox();
        //CustomizationDialog.showFor(getActivity(), mItem, null);
    }

    private void plusAmount() {
        Cart cart = Cart.instance();
        cart.putItem(mItem.getId());
        int amount = cart.getAmountForNotConfirmed(mItem.getId());
        mAmount.setText(String.valueOf(amount));
        EventBus.getDefault().post(new ItemAddedToCartEvent());
        Toast.makeText(getActivity(),
                "Item added to cart",
                Toast.LENGTH_SHORT)
                .show();
    }

    private void showSpecialBox() {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.dialog_preferences, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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

    private void minusAmount() {
        Cart cart = Cart.instance();
        int amount = cart.getAmountForNotConfirmed(mItem.getId());
        if (amount != 0) {
            Toast.makeText(getActivity(),
                    "Item removed from cart",
                    Toast.LENGTH_SHORT)
                    .show();
        }
        cart.removeItem(mItem.getId());
        amount = cart.getAmountForNotConfirmed(mItem.getId());
        mAmount.setText(String.valueOf(amount));
        EventBus.getDefault().post(new ItemAddedToCartEvent());
    }
}
