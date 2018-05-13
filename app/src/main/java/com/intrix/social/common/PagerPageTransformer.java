package com.intrix.social.common;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by yarolegovich on 25.12.2015.
 */
public class PagerPageTransformer implements ViewPager.PageTransformer {

    public static final PagerPageTransformer INSTANCE = new PagerPageTransformer();

    @Override
    public void transformPage(View page, float position) {
        final float normalizedPosition = Math.abs(Math.abs(position) - 1);
        page.setScaleX(normalizedPosition / 5 + 0.8f);
        page.setScaleY(normalizedPosition / 5 + 0.8f);
    }
}
