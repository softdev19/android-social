package com.intrix.social.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.intrix.social.fragment.AlertFragment;
import com.intrix.social.fragment.GeneralSettingsFragment;
import com.intrix.social.fragment.SocialFragment;

/**
 * Created by wahib on 12/16/15.
 */
public class SettingsFragmentPageAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[]{"General", "Alert", "Social"};
    private Context context;

    public SettingsFragmentPageAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new GeneralSettingsFragment();
            case 1:
                return new AlertFragment();
            case 2:
                return new SocialFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
