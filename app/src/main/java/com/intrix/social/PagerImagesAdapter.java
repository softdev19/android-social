package com.intrix.social;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.intrix.social.fragment.TintedImageFragment;

import java.util.List;

/**
 * Created by yarolegovich on 29.12.2015.
 */
public class PagerImagesAdapter extends FragmentPagerAdapter {

    private List<String> mUrls;

    public PagerImagesAdapter(FragmentManager fm, List<String> urls) {
        super(fm);
        mUrls = urls;
    }

    @Override
    public Fragment getItem(int position) {
        return TintedImageFragment.create(mUrls.get(position));
    }

    @Override
    public int getCount() {
        return mUrls.size();
    }
}
