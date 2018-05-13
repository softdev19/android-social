package com.intrix.social.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.intrix.social.NoToolbarUniversalActivity;
import com.intrix.social.R;
import com.intrix.social.model.Item;
import com.intrix.social.model.event.ItemAddedToCartEvent;

import de.greenrobot.event.EventBus;
import io.realm.Realm;

/**
 * Created by yarolegovich on 20.12.2015.
 */
public class ItemPagerFragment extends Fragment implements
        View.OnClickListener {

    private static final String LOG_TAG = ItemPagerFragment.class.getSimpleName();
    private static final String ITEM_ID = "item_id";

    public static ItemPagerFragment create(int itemId, ViewPager outer) {
        ItemPagerFragment fragment = new ItemPagerFragment();
        Bundle args = new Bundle();
        fragment.mPager = outer;
        args.putInt(ITEM_ID, itemId);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewPager mPager;

    private TextView mAmount;

    private Realm mRealm;
    private Item mItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getInstance(getActivity());
        int id = getArguments().getInt(ITEM_ID);
        mItem = mRealm.where(Item.class).equalTo("id", id).findFirst();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_dish_pager, container, false);

        v.setOnClickListener(this);

        ImageView itemImage = (ImageView) v.findViewById(R.id.item_image);
        Glide.with(getActivity()).load(mItem.getImage()).into(itemImage);
        int tint = ContextCompat.getColor(getActivity(), R.color.item_dish_tint);
        itemImage.setColorFilter(tint);

        TextView nameInfo = (TextView) v.findViewById(R.id.item_name_info);
        TextView description = (TextView) v.findViewById(R.id.item_info);

        TextView likes = (TextView) v.findViewById(R.id.likes);
        likes.setText(mItem.getFav());

        TextView price = (TextView) v.findViewById(R.id.item_price);
        price.setText(getString(R.string.price_with_val, Integer.parseInt(mItem.getPrice())));

        description.setText(mItem.getDescription());
        nameInfo.setText(mItem.getName());

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.container:
                Intent i = new Intent(getActivity(), NoToolbarUniversalActivity.class);
                i.putExtra(NoToolbarUniversalActivity.EXTRA_TOKEN, MenuItemFragment.class);
                i.putExtra(MenuItemFragment.ITEM_ID, mItem.getId());
                startActivity(i);
                break;
        }
        EventBus.getDefault().post(new ItemAddedToCartEvent());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }


    /*
     * @Deprecated
     * If there are clickable elements on page - they somehow go in conflict with page transformer,
     * and ui isn't updated. Method was provided as temp fix, and it is super inefficient.
     * !BUT! it is still used to update ui, when user liked item from another screen.
     */
    @Deprecated
    private void fixPager() {
        mPager.getAdapter().notifyDataSetChanged();
        int current = mPager.getCurrentItem();
        FragmentStatePagerAdapter adapter = (FragmentStatePagerAdapter) mPager.getAdapter();
        View left = null, right = null;
        if (current != 0) {
            Fragment fragmentL = (Fragment) adapter.instantiateItem(mPager, current - 1);
            left = fragmentL.getView();
        }
        if (current < adapter.getCount() - 1) {
            Fragment fragmentR = (Fragment) adapter.instantiateItem(mPager, current + 1);
            right = fragmentR.getView();
        }
        if (left != null) {
            left.setScaleX(0.8f);
            left.setScaleY(0.8f);
        }
        if (right != null) {
            right.setScaleX(0.8f);
            right.setScaleY(0.8f);
        }
    }

//    private void plusAmount() {
//        Cart cart = Cart.instance();
//        cart.putItem(mItem.getId());
//        int amount = cart.getAmountFor(mItem.getId());
//        mAmount.setText(String.valueOf(amount));
//        if (amount > 0 && mHasCustomizations) {
//            mInfoPanel.setVisibility(View.GONE);
//            mCustomizePanel.setVisibility(View.VISIBLE);
//        }
//        fixPager();
//    }
//
//    private void minusAmount() {
//        Cart cart = Cart.instance();
//        cart.removeItem(mItem.getId());
//        int amount = cart.getAmountFor(mItem.getId());
//        mAmount.setText(String.valueOf(amount));
//        if (amount == 0 && mHasCustomizations) {
//            mInfoPanel.setVisibility(View.VISIBLE);
//            mCustomizePanel.setVisibility(View.GONE);
//            mIngredientsPanel.resetIngredients();
//        }
//    }
//
//    private void userLikes() {
//        int favorite = Integer.parseInt(mItem.getFav());
//        mRealm.beginTransaction();
//        mItem.setFav(String.valueOf(favorite + 1));
//        mRealm.copyToRealmOrUpdate(mItem);
//        mRealm.commitTransaction();
//        mLikes.setText(mItem.getFav());
//        EventBus.getDefault().post(new RefreshMenuPagesEvent());
//        Networker.getInstance().favorite(mItem);
//    }
}
