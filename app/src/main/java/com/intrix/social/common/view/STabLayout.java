package com.intrix.social.common.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by yarolegovich on 16.12.2015.
 */
public class STabLayout extends TabLayout {

    private Typeface mTypeface;

    public STabLayout(Context context) {
        super(context);
        init();
    }

    public STabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public STabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/montana-regular.otf");
    }

    @Override
    public void addTab(TabLayout.Tab tab) {
        super.addTab(tab);

        ViewGroup mainView = (ViewGroup) getChildAt(0);
        ViewGroup tabView = (ViewGroup) mainView.getChildAt(tab.getPosition());

        int tabChildCount = tabView.getChildCount();
        for (int i = 0; i < tabChildCount; i++) {
            View tabViewChild = tabView.getChildAt(i);
            if (tabViewChild instanceof TextView) {
                ((TextView) tabViewChild).setTypeface(mTypeface, Typeface.NORMAL);
            }
        }
    }

}