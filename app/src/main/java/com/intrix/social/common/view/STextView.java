package com.intrix.social.common.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import com.intrix.social.R;

/**
 * Created by sutharsha on 19/10/15.
 */
public class STextView extends TextView {

    public STextView(Context context) {
        super(context);
        init(null);
    }

    public STextView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(attrs);
    }

    public STextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public STextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        this.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/montana-regular.otf"));

        int tintColor = -1;
        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.STextView);
            try {
                tintColor = ta.getColor(R.styleable.STextView_tint, tintColor);
            } finally {
                ta.recycle();
            }
        }
        tintDrawables(tintColor);
    }

    private void tintDrawables(int color) {
        if (color != -1) {
            Drawable[] drawables = getCompoundDrawables();
            for (Drawable drawable : drawables) {
                if (drawable != null) {
                    drawable.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                }
            }
        }
    }
}
