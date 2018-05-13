package com.intrix.social.chat.networking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.intrix.social.utils.Toaster;
import com.intrix.social.chat.abstractions.ActivityWithOverlay;
import com.intrix.social.chat.fragments.ImageUploadingFragment;


/**
 * Created by yarolegovich on 8/6/15.
 */
public class PhotoUploadBroadcastReceiver extends BroadcastReceiver {

    private ActivityWithOverlay mActivityWithOverlay;

    public static PhotoUploadBroadcastReceiver registerNewInstance(Context context, ActivityWithOverlay activityWithOverlay) {
        PhotoUploadBroadcastReceiver receiver = new PhotoUploadBroadcastReceiver(activityWithOverlay);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PhotoUploadService.ACTION_UPLOAD_FINISHED);
        intentFilter.addAction(PhotoUploadService.ACTION_UPLOAD_STARTED);
        context.registerReceiver(receiver, intentFilter);
        return receiver;
    }

    public PhotoUploadBroadcastReceiver() { }

    public PhotoUploadBroadcastReceiver(ActivityWithOverlay activityWithOverlay) {
        mActivityWithOverlay = activityWithOverlay;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case PhotoUploadService.ACTION_UPLOAD_STARTED:
                if (mActivityWithOverlay != null && intent.hasExtra(PhotoUploadService.FILE_EXTRA)) {
                    String imageFile = intent.getStringExtra(PhotoUploadService.FILE_EXTRA);
                    ImageUploadingFragment fragment = ImageUploadingFragment.newInstance(imageFile);
                    ProgressiveTypedFile.setListener(fragment);
                    mActivityWithOverlay.placeFragmentToOverlay(fragment);
                }
                break;
            case PhotoUploadService.ACTION_UPLOAD_FINISHED:
                boolean success = intent.getBooleanExtra(PhotoUploadService.STATUS_EXTRA, false);
                Toaster.showToast(success ? "File loaded" : "File loading failed");
                if (mActivityWithOverlay != null) {
                    mActivityWithOverlay.hideOverlayDelayed();
                }
                break;
        }
    }
}
