package com.intrix.social.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intrix.social.R;

/**
 * Created by yarolegovich on 22.11.2015.
 */
public class BlackFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_black, container, false);

//        AppCompatActivity activity = (AppCompatActivity) getActivity();
//        ImageView imageView = (ImageView) v.findViewById(R.id.wall_image);
//        ImageUtils.setBitmapHeader(activity, imageView, R.drawable.wall);
//
//        ListView list = (ListView) v.findViewById(android.R.id.list);
//        DamagesAdapter adapter = new DamagesAdapter(activity);
//        list.setAdapter(adapter);

        return v;
    }
}
