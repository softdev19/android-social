package com.intrix.social.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.List;

/**
 * Created by yarolegovich on 16.12.2015.
 */
public class PagerAdapter extends FragmentPagerAdapter {

    private List<String> mTitles;
    private List<Class<? extends Fragment>> mTokens;

    public PagerAdapter(FragmentManager fm, List<String> titles, List<Class<? extends Fragment>> tokens) {
        super(fm);
        mTitles = titles;
        mTokens = tokens;
    }

    @Override
    public Fragment getItem(int position) {
        try {
            return mTokens.get(position).newInstance();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), e.getMessage(), e);
            return new Fragment();
        }
    }

    @Override
    public int getCount() {
        return mTokens.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }
}
