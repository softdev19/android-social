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
import android.widget.EditText;

import com.intrix.social.R;

/**
 * Created by yarolegovich on 21.11.2015.
 */
public class SEditText extends EditText {
    public SEditText(Context context) {
        super(context);
        init(null);
    }

    public SEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        int tintColor = ContextCompat.getColor(getContext(), R.color.text_secondary);
        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.SEditText);
            try {
                tintColor = ta.getColor(R.styleable.SEditText_drawableTint, tintColor);
            } finally {
                ta.recycle();
            }
        }
        setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/montana-regular.otf"));

        Drawable[] drawables = getCompoundDrawables();
        for (Drawable drawable : drawables) {
            if (drawable != null) {
                drawable.mutate().setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);
            }
        }
    }
}
