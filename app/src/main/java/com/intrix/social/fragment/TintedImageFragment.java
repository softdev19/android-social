package com.intrix.social.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.intrix.social.R;

/**
 * Created by yarolegovich on 29.12.2015.
 */
public class TintedImageFragment extends Fragment {

    private static final String IMAGE_URL = "arg.url";

    public static TintedImageFragment create(String imageUrl) {
        TintedImageFragment fragment = new TintedImageFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ImageView imageView = (ImageView) inflater.inflate(R.layout.item_tinted_image, container, false);

        String url = getArguments().getString(IMAGE_URL);
        Glide.with(getActivity()).load(url).into(imageView);

        imageView.setColorFilter(Color.parseColor("#78000000"));

        return imageView;
    }
}
