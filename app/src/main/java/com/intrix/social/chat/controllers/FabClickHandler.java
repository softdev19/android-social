package com.intrix.social.chat.controllers;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import com.github.clans.fab.FloatingActionMenu;
import com.intrix.social.R;
import com.intrix.social.chat.utils.Utils;


/**
 * Created by yarolegovich on 7/29/15.
 */
public class FabClickHandler implements View.OnClickListener, FloatingActionMenu.OnMenuToggleListener {

    private ImageView mMenuIcon;

    private View mCoveringOverlay;
    private FloatingActionMenu mActionMenu;

    public FabClickHandler(FloatingActionMenu menu, final View coveringOverlay) {
        mCoveringOverlay = coveringOverlay;
        mActionMenu = menu;

        mMenuIcon = mActionMenu.getMenuIconView();

        mCoveringOverlay.setOnClickListener(this);
        mActionMenu.setOnMenuToggleListener(this);
    }

    public boolean isMenuOpened() {
        return mActionMenu.isOpened();
    }

    public void closeFabMenu() {
        if (mActionMenu.isOpened()) {
            mActionMenu.close(true);
            handleClose();
        }
    }

    @Override
    public void onClick(View v) {
        mCoveringOverlay.setVisibility(View.GONE);
        mActionMenu.close(true);
    }

    @Override
    public void onMenuToggle(boolean opened) {
        if (opened) {
            Utils.hideSoftwareKeyboard((Activity) mCoveringOverlay.getContext());
            mMenuIcon.setImageResource(R.drawable.ic_plus_grey600_24dp);
            mCoveringOverlay.setVisibility(View.VISIBLE);
        } else handleClose();
    }

    private void handleClose() {
        mMenuIcon.setImageResource(R.drawable.attach);
        mCoveringOverlay.setVisibility(View.GONE);
    }
}
