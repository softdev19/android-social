package com.intrix.social.chat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.intrix.social.R;
import com.intrix.social.chat.abstractions.ProgressListener;


/**
 * Created by yarolegovich on 8/6/15.
 */
public class ImageUploadingFragment extends Fragment implements ProgressListener {

    private static final String LOG_TAG = ImageUploadingFragment.class.getSimpleName();

    private static final String FILE_PREVIEW = "file_load_preview";

    public static ImageUploadingFragment newInstance(String fileUrl) {
        Bundle args = new Bundle();
        ImageUploadingFragment fragment = new ImageUploadingFragment();
        args.putString(FILE_PREVIEW, fileUrl);
        fragment.setArguments(args);
        return fragment;
    }

    private ProgressBar mProgressBar;
    private long mTotal;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //temporary onCreateView, layout also contains ImageView for previewing, but currently visibility == GONE
        return inflater.inflate(R.layout.fragment_image_loading, container, false);
    }

    @Override
    public void transferred(int num) {
        if (mProgressBar != null) {
            int progress = (int) ((num / (float) mTotal) * 100);
            Log.d(LOG_TAG, "Progress: " + progress);
            mProgressBar.setProgress(num);
        }
    }
}
