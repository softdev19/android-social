package com.intrix.social.common.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intrix.social.R;

import java.util.Iterator;
import java.util.List;

/**
 * Created by yarolegovich on 29.12.2015.
 */
public class DotBarView extends LinearLayout implements View.OnClickListener {

    public DotBarView(Context context) {
        this(context, null);
    }

    public DotBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DotBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DotBarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setOrientation(HORIZONTAL);
    }

    public void setDots(List<Dot> dots) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        Iterator<Dot> iter = dots.iterator();
        for (int i = 0; i < dots.size(); i++) {
            Dot dot = iter.next();
            View dotView = inflater.inflate(R.layout.view_dotbar_dot, this, false);

            TextView outer = (TextView) dotView.findViewById(R.id.outer);
            outer.setText(dot.mOuterText);
            TextView inner = (TextView) dotView.findViewById(R.id.inner);
            inner.setText(dot.mInnerText);

            dotView.setOnClickListener(this);
            addView(dotView);
        }

        removeLeftConnection(getChildAt(0));
        removeRightConnection(getChildAt(getChildCount() - 1));
    }

    public void setSelected(int dotPosition) {
        onClick(getChildAt(dotPosition));
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (v != child) {
                child.findViewById(R.id.expanded).setVisibility(GONE);
            } else {
                child.findViewById(R.id.expanded).setVisibility(VISIBLE);
            }
        }
    }

    private void removeLeftConnection(View v) {
        v.findViewById(R.id.left_connection).setVisibility(GONE);
        v.findViewById(R.id.left_space).setVisibility(VISIBLE);
    }

    private void removeRightConnection(View v) {
        v.findViewById(R.id.right_connection).setVisibility(GONE);
        v.findViewById(R.id.right_space).setVisibility(VISIBLE);
    }

    public static class Dot {
        private String mInnerText;
        private String mOuterText;

        public Dot(String innerText, String outerText) {
            mInnerText = innerText;
            mOuterText = outerText;
        }
    }
}
