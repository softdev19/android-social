package com.intrix.social.common.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.intrix.social.adapter.ShareAdapter;
import com.intrix.social.utils.ShareManager;

/**
 * Created by yarolegovich on 02.01.2016.
 */
public class ShareDialog {

    public static void showToShare(final Context context, final String whatToShare) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setAdapter(new ShareAdapter(context), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ShareAdapter.ShareOption option = ShareAdapter.SHARE_OPTIONS.get(which);
                        ShareManager.with(context).shareWith(option.appPackage, whatToShare);
                    }
                })
                .create();
        dialog.getListView().setDivider(null);
        dialog.show();
    }
}
