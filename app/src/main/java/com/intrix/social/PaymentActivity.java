package com.intrix.social;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.intrix.social.common.view.STabLayout;
import com.intrix.social.fragment.payment.PaymentCardFragment;
import com.intrix.social.fragment.payment.PaymentNetFragment;
import com.intrix.social.fragment.payment.PaymentWalletFragment;

import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private STabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        toolbar = (Toolbar)findViewById(R.id.toolbar_transparent);
        tabLayout = (STabLayout)findViewById(R.id.tabs);
        viewPager = (ViewPager)findViewById(R.id.pager);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PaymentWalletFragment(), "Wallet");
        adapter.addFragment(new PaymentCardFragment(), "Card");
        adapter.addFragment(new PaymentNetFragment(), "Net banking");
        viewPager.setAdapter(adapter);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
