package com.intrix.social.common.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by sutharsha on 19/10/15.
 */
public class SBtnView extends Button {

    public SBtnView(Context context) {
        super(context);
        init();
    }

    public SBtnView(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    public SBtnView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SBtnView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        this.setTypeface(Typeface.createFromAsset(getContext().getAssets(),"fonts/montana-regular.otf"));
    }


}
