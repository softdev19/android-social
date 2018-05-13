package com.intrix.social.common.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;

import com.intrix.social.R;
import com.mikepenz.iconics.utils.Utils;

/**
 * Created by yarolegovich on 22.12.2015.
 */
public class RateBar extends LinearLayout implements View.OnClickListener {

    private int mTintSelected;
    private int mTintNormal;

    private OnRatingChangedListener mListener;

    public RateBar(Context context) {
        super(context);
        init(null);
    }

    public RateBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RateBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RateBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        int icon = R.drawable.ic_insert_emoticon_white_24dp;
        int maxRate = 4;
        int size = -1;
        if (attrs != null) {
            Context c = getContext();
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.RateBar);
            try {
                icon = a.getResourceId(R.styleable.RateBar_rate_icon, R.drawable.ic_insert_emoticon_white_24dp);
                mTintNormal = a.getColor(R.styleable.RateBar_tint_normal,
                        ContextCompat.getColor(c, R.color.events_grey)
                );
                mTintSelected = a.getColor(R.styleable.RateBar_tint_selected,
                        ContextCompat.getColor(c, R.color.s_orange)
                );
                maxRate = a.getInt(R.styleable.RateBar_maxRating, 4);
                size = a.getDimensionPixelSize(R.styleable.RateBar_icon_size, -1);
            } finally {
                a.recycle();
            }
        }
        ViewGroup.LayoutParams iconSize = null;
        if (size != -1) {
            iconSize = new ViewGroup.LayoutParams(size, size);
        }
        int dp = Utils.convertDpToPx(getContext(), 1);
        for (int i = 0; i < maxRate; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(icon);
            imageView.setColorFilter(mTintNormal);
            imageView.setOnClickListener(this);
            if (iconSize != null) {
                imageView.setLayoutParams(iconSize);
            }
            addView(imageView);
            if (i != maxRate -1) {
                Space space = new Space(getContext());
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(dp,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                space.setLayoutParams(params);
                addView(space);
            }
        }
        setRating(1);

    }

    public void setRating(int rating) {
        int child = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v instanceof ImageView) {
                child++;
                if (child == rating) {
                    v.callOnClick();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int selected = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (!(child instanceof ImageView)) continue;;
            ImageView rate = (ImageView) getChildAt(i);
            rate.setColorFilter(mTintSelected);
            rate.animate().scaleX(1.15f)
                    .scaleY(1.15f)
                    .setDuration(300)
                    .start();
            if (v == child) {
                selected = i;
                break;
            }
        }
        for (int i = selected + 1; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (!(child instanceof ImageView)) continue;
            ImageView rate = (ImageView) getChildAt(i);
            rate.setColorFilter(mTintNormal);
            rate.animate().scaleY(1)
                    .scaleX(1)
                    .setDuration(300)
                    .start();
        }
        if (mListener != null) {
            mListener.onRatingChanged(selected + 1);
        }
    }

    public void setListener(OnRatingChangedListener listener) {
        mListener = listener;
    }

    public interface OnRatingChangedListener {
        void onRatingChanged(int newRating);
    }
}
