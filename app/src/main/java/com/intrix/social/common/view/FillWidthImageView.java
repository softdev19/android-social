package com.intrix.social.common.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class FillWidthImageView extends ImageView {
    final int MAX_SCALE_FACTOR = 2;

    public FillWidthImageView(Context context) {
        super(context);
        init();
    }

    public FillWidthImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public FillWidthImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        this.setScaleType(ScaleType.CENTER_INSIDE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final Drawable d = this.getDrawable();

        if (d != null) {
            // ceil not round - avoid thin vertical gaps along the left/right edges
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            if (width > (d.getIntrinsicWidth() * MAX_SCALE_FACTOR))
                width = d.getIntrinsicWidth() * MAX_SCALE_FACTOR;
            final int height = (int) Math.ceil(width * (float) d.getIntrinsicHeight() / d.getIntrinsicWidth());
            this.setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

}