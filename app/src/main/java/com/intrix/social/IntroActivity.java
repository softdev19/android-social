package com.intrix.social;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toanpv on 12/22/15.
 */
public class IntroActivity extends Activity {

    private ViewPager mVpIntro;
    private IntroPagerAdapter mIntroPagerAdapter;
    private List<Intro> mIntros;
    private TextView mTvTitle, mTvContent;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        mTvTitle = (TextView) findViewById(R.id.tv_title_intro);
        mTvContent = (TextView) findViewById(R.id.tv_content_intro);
        mIntros = new ArrayList<>();
        mIntros.add(new Intro("Title 0", "All your favorite dish is just one click away 0", R.drawable.intro));
        mIntros.add(new Intro("Title 1", "All your favorite dish is just one click away 1", R.drawable.intro));
        mIntros.add(new Intro("Title 2", "All your favorite dish is just one click away 2", R.drawable.intro));

        mIntroPagerAdapter = new IntroPagerAdapter(this, mIntros);

        mVpIntro = (ViewPager) findViewById(R.id.vp_intro);
        mVpIntro.setAdapter(mIntroPagerAdapter);
        mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Intro intro = mIntros.get(position);
                mTvTitle.setText(intro.title);
                mTvContent.setText(intro.content);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        mVpIntro.addOnPageChangeListener(mOnPageChangeListener);

        mVpIntro.post(new Runnable() {
            @Override
            public void run() {
                mOnPageChangeListener.onPageSelected(mVpIntro.getCurrentItem());
            }
        });
        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.vpi_intro);
        indicator.setFillColor(ContextCompat.getColor(this, R.color.s_orange));
        indicator.setPageColor(ContextCompat.getColor(this, R.color.chat_time_text));
        indicator.setViewPager(mVpIntro);
    }

    public void onClickSkip(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    class Intro {
        String title;
        String content;
        int image;

        public Intro(String title, String content, int image) {
            this.title = title;
            this.content = content;
            this.image = image;
        }
    }

    class IntroPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;
        private List<Intro> mIntros;

        public IntroPagerAdapter(Context context, List<Intro> intros) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mIntros = intros;
        }

        @Override
        public int getCount() {
            return mIntros.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.item_intro, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.iv_intro_item);
            imageView.setImageResource(mIntros.get(position).image);

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }
}
