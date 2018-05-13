package com.intrix.social.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.intrix.social.fragment.ItemPagerFragment;
import com.intrix.social.model.Item;

import java.util.List;

/**
 * Created by yarolegovich on 20.12.2015.
 */
public class NewDishPagerAdapter extends FragmentStatePagerAdapter {

    private ViewPager mOuter;
    private List<Item> mItems;

    public NewDishPagerAdapter(FragmentManager fm, ViewPager outer, List<Item> items) {
        super(fm);
        mOuter = outer;
        mItems = items;
    }

    @Override
    public Fragment getItem(int position) {
        return ItemPagerFragment.create(mItems.get(position).getId(), mOuter);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
