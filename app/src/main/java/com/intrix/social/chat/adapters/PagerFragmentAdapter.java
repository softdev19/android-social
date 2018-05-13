package com.intrix.social.chat.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.List;

/**
 * Created by yarolegovich on 7/26/15.
 */
public class PagerFragmentAdapter extends FragmentStatePagerAdapter {

    private static final String LOG_TAG = PagerFragmentAdapter.class.getSimpleName();

    private List<Class<? extends Fragment>> mTokens;
    private List<String> mTitles;

    public PagerFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        try {
            return mTokens.get(position).newInstance();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error creating fragment instance " + e.getMessage());
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

    public static class Builder {
        private PagerFragmentAdapter instance;

        public Builder(FragmentManager fm) {
            instance = new PagerFragmentAdapter(fm);
        }

        public Builder withTokens(List<Class<? extends Fragment>> tokens) {
            instance.mTokens = tokens;
            return this;
        }

        public Builder withTitles(List<String> titles) {
            instance.mTitles = titles;
            return this;
        }

        public PagerFragmentAdapter build() {
            return instance;
        }
    }
}