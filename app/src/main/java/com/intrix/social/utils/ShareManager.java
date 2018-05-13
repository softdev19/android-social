package com.intrix.social.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

/**
 * Created by yarolegovich on 07.01.2016.
 */
public class ShareManager {

    public static ShareManager with(Context context) {
        return new ShareManager(context);
    }

    private Context mContext;

    public ShareManager(Context context) {
        mContext = context.getApplicationContext();
    }

    public void shareWith(String app, String info) {
        if (isAppInstalled(app)) {
            Intent intent;
            if (canSend(app)) {
                intent = new Intent(Intent.ACTION_SEND);
                intent.setPackage(app);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setType("text/*");
                intent.putExtra(Intent.EXTRA_TEXT, info);
            } else {
                intent = mContext.getPackageManager().getLaunchIntentForPackage(app);
            }
            mContext.startActivity(intent);
        } else {
            Toast.makeText(mContext,
                    "Installed application first",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    private boolean canSend(String app) {
        return app.contains("facebook") || app.contains("twitter");
    }

    private boolean isAppInstalled(String app) {
        try {
            mContext.getPackageManager().getApplicationInfo(app, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
