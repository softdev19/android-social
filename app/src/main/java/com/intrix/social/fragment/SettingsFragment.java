package com.intrix.social.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intrix.social.R;
import com.intrix.social.adapter.SettingsFragmentPageAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new SettingsFragmentPageAdapter(
                getActivity(), getActivity().getSupportFragmentManager()));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

}
