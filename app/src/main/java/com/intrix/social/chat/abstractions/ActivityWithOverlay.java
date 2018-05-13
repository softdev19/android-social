package com.intrix.social.chat.abstractions;

import android.support.v4.app.Fragment;

/**
 * Created by yarolegovich on 7/28/15.
 */
public interface ActivityWithOverlay {
    void placeFragmentToOverlay(Fragment fragment);
    boolean hideOverlay();
    void hideOverlayDelayed();
}
