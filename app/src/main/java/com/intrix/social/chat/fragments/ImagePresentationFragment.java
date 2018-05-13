package com.intrix.social.chat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.intrix.social.R;

/**
 * Created by yarolegovich on 7/28/15.
 */
public class ImagePresentationFragment extends Fragment implements View.OnClickListener, RequestListener<String, GlideDrawable> {
    private static final String IMAGE_TO_SHOW = "image_to_show";

    public static ImagePresentationFragment newInstance(String imageUrl) {
        Bundle bundle = new Bundle();
        bundle.putString(IMAGE_TO_SHOW, imageUrl);
        ImagePresentationFragment fragment = new ImagePresentationFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private View mLoadingMessage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image, container, false);

        Bundle args = getArguments();

        String url = args.getString(IMAGE_TO_SHOW);

        if (!"".equals(url) && url != null) {
            mLoadingMessage = v.findViewById(R.id.loading_message);
            ImageView imageFrame = (ImageView) v.findViewById(R.id.big_image);
            if (!"".equals(url))
                Glide.with(getActivity())
                        .load(url)
                        .listener(this)
                        .into(imageFrame);
        }

        v.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        getActivity().onBackPressed();
    }

    @Override
    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
        return false;
    }

    @Override
    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
        if (mLoadingMessage != null)
            mLoadingMessage.setVisibility(View.GONE);
        return false;
    }
}
