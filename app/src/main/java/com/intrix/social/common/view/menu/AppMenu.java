package com.intrix.social.common.view.menu;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.intrix.social.R;

import java.util.List;

/**
 * Created by yarolegovich on 01.01.2016.
 */
public class AppMenu extends FrameLayout implements AdapterView.OnItemClickListener {

    private ImageView mBg;
    private View mImageOverlay;
    private ListView mNavigationPanel;

    private int mColorFilter;

    private ItemSelectedListener mItemSelectedListener;

    public AppMenu(Context context) {
        this(context, null);
    }

    public AppMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AppMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {
        inflate(getContext(), R.layout.view_menu, this);
        mBg = (ImageView) findViewById(R.id.menu_bg);
        mNavigationPanel = (ListView) findViewById(R.id.menu_list);
        mNavigationPanel.setOnItemClickListener(this);
        mImageOverlay = findViewById(R.id.image_overlay);
    }

    public void setBgTint(int color) {
        mColorFilter = color;
        mBg.setColorFilter(color);
    }

    public <T>void setBgImage(T bg) {
        Glide.with(getContext()).load(bg).into(mBg);
        mBg.setColorFilter(mColorFilter);
    }

    public void setItemSelectedListener(ItemSelectedListener itemSelectedListener) {
        mItemSelectedListener = itemSelectedListener;
    }

    public void setGradientOverlay(int overlay) {
        mImageOverlay.setBackgroundResource(overlay);
    }

    public void setSelectionAtPosition(int position) {
        if (mItemSelectedListener != null) {
            mItemSelectedListener.onItemSelected(position);
        }
    }
    
    public void addItems(List<AppMenuItem> items) {
        NavigationAdapter adapter = new NavigationAdapter(getContext(), items);
        mNavigationPanel.setAdapter(adapter);
    }

    public void addHeader(View header) {
        mNavigationPanel.addHeaderView(header);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mItemSelectedListener != null) {
            mItemSelectedListener.onItemSelected(position);
        }
    }


    public interface ItemSelectedListener {
        void onItemSelected(int position);
    }
}
