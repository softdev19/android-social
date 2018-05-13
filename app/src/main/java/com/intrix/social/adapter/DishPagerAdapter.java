package com.intrix.social.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.intrix.social.R;

/**
 * Created by yarolegovich on 22.11.2015.
 */
public class DishPagerAdapter extends PagerAdapter {

    private ViewPager mOuterPager;
    private Context mContext;

    public DishPagerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_dish_pager, container, false);

        ImageView imageView = (ImageView) v.findViewById(R.id.item_image);
        Glide.with(mContext).load(R.drawable.temp_dish).into(imageView);

        new ViewController(v);
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private class ViewController implements View.OnClickListener {

        private View mInfoPanel, mCustomizePanel;

        private ImageView mAmountPlus, mAmountMinus;
        private TextView mAmount;

        public ViewController(View v) {
            mInfoPanel = v.findViewById(R.id.info_panel);

            mAmountMinus = (ImageView) v.findViewById(R.id.amount_plus);
            mAmountPlus = (ImageView) v.findViewById(R.id.amount_minus);
            mAmount = (TextView) v.findViewById(R.id.item_amount);
            mAmountMinus.setOnClickListener(this);
            mAmountPlus.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.amount_minus:
                    int amount = getAmount();
                    amount--;
                    if (amount >= 0) {
                        mAmount.setText(String.valueOf(amount));
                        if (amount == 0) {
                            mInfoPanel.setVisibility(View.VISIBLE);
                            mCustomizePanel.setVisibility(View.GONE);
                        }
                    }
                    break;
                case R.id.amount_plus:
                    amount = getAmount();
                    if (amount == 0) {
                        mInfoPanel.setVisibility(View.GONE);
                        mCustomizePanel.setVisibility(View.VISIBLE);
                    }
                    amount++;
                    mAmount.setText(String.valueOf(amount));
                    break;
            }
        }

        private int getAmount() {
            return Integer.parseInt(mAmount.getText().toString());
        }
    }
}
