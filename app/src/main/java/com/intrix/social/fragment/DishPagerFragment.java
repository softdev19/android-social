package com.intrix.social.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.intrix.social.R;
import com.intrix.social.UniversalActivity;
import com.intrix.social.adapter.NewDishPagerAdapter;
import com.intrix.social.common.PagerPageTransformer;
import com.intrix.social.model.Cart;
import com.intrix.social.model.Category;
import com.intrix.social.model.Item;
import com.intrix.social.model.event.ChangeFragmentEvent;
import com.intrix.social.model.event.ItemAddedToCartEvent;
import com.intrix.social.utils.ImageUtils;

import java.util.List;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.Sort;

/**
 * Created by yarolegovich on 22.11.2015.
 */
public class DishPagerFragment extends Fragment implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private static final String TAG = "DishPagerFragment";

    public static final String EXTRA_CATEGORY_ID = "catId";

    private Realm mRealm;
    private Category mCategory;
    private TextView mItemsAdded;
    private ViewPager mPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getInstance(getActivity());
        int id = getActivity().getIntent().getIntExtra(EXTRA_CATEGORY_ID, -1);
        mCategory = mRealm.where(Category.class).equalTo("id", id).findFirst();
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dish_pager, container, false);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ImageView imageView = (ImageView) v.findViewById(R.id.wood_image);
        ImageUtils.setBitmapHeader(activity, imageView, R.drawable.wood);

        mPager = (ViewPager) v.findViewById(R.id.pager);
        NewDishPagerAdapter adapter = new NewDishPagerAdapter(getChildFragmentManager(), mPager, getData());
        mPager.setAdapter(adapter);
        mPager.setOffscreenPageLimit(adapter.getCount());
        mPager.setClipChildren(false);
        mPager.addOnPageChangeListener(this);
        mPager.setPageTransformer(false, PagerPageTransformer.INSTANCE);

        mItemsAdded = (TextView) v.findViewById(R.id.items_added);
        mItemsAdded.setText(getString(R.string.items_added, Cart.instance().itemsInCartNotConfirmed()));

        AppCompatActivity a = (AppCompatActivity) getActivity();
        a.setTitle(mCategory.getName());

        v.findViewById(R.id.confirm).setOnClickListener(this);

        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        fixPager();
    }

    private List<Item> getData() {
        return mRealm.where(Item.class)
                .equalTo("categoryId", mCategory.getId())
                .findAllSorted("id", Sort.ASCENDING);
    }

    public void onEvent(ItemAddedToCartEvent event) {
        mItemsAdded.setText(getString(R.string.items_added, Cart.instance().itemsInCartNotConfirmed()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_pager, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.view_as_list:
                EventBus.getDefault().post(new ChangeFragmentEvent(UniversalActivity.class,
                                new DishExpandableListFragment())
                );
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(getActivity(), UniversalActivity.class);
        i.putExtra(UniversalActivity.EXTRA_TOKEN, ConfirmFragment.class);
        startActivity(i);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state != ViewPager.SCROLL_STATE_IDLE) {
            final int childCount = mPager.getChildCount();
            for (int i = 0; i < childCount; i++) {
                mPager.getChildAt(i).setLayerType(View.LAYER_TYPE_NONE, null);
            }
        }
    }

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
}
