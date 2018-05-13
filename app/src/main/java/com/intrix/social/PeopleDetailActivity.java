package com.intrix.social;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.intrix.social.fragment.PeopleChatListFragment;
import com.intrix.social.fragment.PeopleOthersFragment;
import com.intrix.social.fragment.PeopleRequestFragment;
import com.intrix.social.model.Customer;
import com.intrix.social.utils.FontCache;
import com.intrix.social.utils.Utils;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.view.BezelImageView;

import io.realm.Realm;

public class PeopleDetailActivity extends AppCompatActivity {
    View menuHeader;
    private Drawer mDrawer;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    Customer user;
    Data data;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private ImageView mIvPhoto;

    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_detail);
        data = MainApplication.data;

//        mRealm = Realm.getInstance(this);
//        mRealm.addChangeListener(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar Ab = getSupportActionBar();

        if(Ab != null) {
            String name = data.getUserName();
            if(name.length() == 0)
                name = "You";
            Ab.setTitle(name);
            Ab.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //((TextView) toolbar.findViewById(android.R.id.title)).setText("Deepika Padukone");

        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        Typeface tf = FontCache.get("fonts/montana-regular.otf", this);
        toolbarLayout.setCollapsedTitleTypeface(tf);
        toolbarLayout.setExpandedTitleTypeface(tf);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        Utils.applyFontedTab(this, tabLayout);

        NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.nsv_people_detail);
        nestedScrollView.setFillViewport(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.splash_accent)));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PeopleDetailActivity.this, DescriptionActivity.class));
            }
        });
/*
        TextView downVotes = (TextView)findViewById(R.id.tv_down_votes_people_detail);

        String downVotesCount = data.getDownVotes();//data.loadData("user.downvotes");//data.user.getDownvotes();
        downVotes.setText("Down Votes " + downVotesCount);
        downVotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDownVotes(v);
            }
        });

        TextView upVotes = (TextView)findViewById(R.id.tv_up_votes_people_detail);
        String upVotesCount = data.getUpVotes();//data.user.getUpvotes();
        upVotes.setText("Up Votes " + upVotesCount);
        upVotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUpVotes(v);
            }
        });
        */


        TextView socialCurrencyTView = (TextView)findViewById(R.id.social_currency);
        int socialCurrency = data.getSocialCurrency();//data.loadData("user.downvotes");//data.user.getDownvotes();
        String socialCurrencyLabel = getResources().getString(R.string.social_currency);

        socialCurrencyTView.setText(socialCurrencyLabel +": "+ socialCurrency);

        ImageView bgImg = (ImageView) findViewById(R.id.iv_people_full);

        user = MainApplication.data.user;

//        if(user == null)
//            return;

//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.people_full);
//
//        float scalingFactor = this.getBitmapScalingFactor(bitmap);
//        Bitmap newBitmap = Utils.scaleBitmap(bitmap, scalingFactor);

        mIvPhoto = (ImageView) findViewById(R.id.iv_photo_people);

        mIvPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PeopleDetailActivity.this, DiscoverSelfie2Activity.class));
            }
        });

        Log.i("TAaasfasdfasd", data.getProfilePicUrl());
                //if(user.getPic().length()>0)
        Glide.with(this).load(data.getProfilePicUrl()).error(R.drawable.header_my_people).into(mIvPhoto);
        //else
          //  mIvPhoto.setImageBitmap(newBitmap);
    }

    private View getHeader() {
        LayoutInflater inflater = getLayoutInflater();
        RelativeLayout header = (RelativeLayout) inflater.inflate(R.layout.dash_header, null);
        BezelImageView pic = (BezelImageView) header.findViewById(R.id.profile_pic);
        return header;
    }

    private float getBitmapScalingFactor(Bitmap bm) {
        int displayWidth = getWindowManager().getDefaultDisplay().getWidth();

        return ((float) displayWidth / (float) bm.getWidth());
    }

    public void onClickDownVotes(View view) {
        Snackbar.make(view, "Down Votes clicked", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void onClickUpVotes(View view) {
        Snackbar.make(view, "Up Votes clicked", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private String[] mTitles;

        public SectionsPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mTitles = context.getResources().getStringArray(R.array.people_activity_tabs);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0)
                return PeopleChatListFragment.newInstance();
            else if(position == 1)
                return PeopleRequestFragment.newInstance();

//            else if(position == 2)
//                return PeopleActivityFragment.newInstance();
            else
                return PeopleOthersFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }
}
