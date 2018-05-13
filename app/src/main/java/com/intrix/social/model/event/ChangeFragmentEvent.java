package com.intrix.social.model.event;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by yarolegovich on 16.12.2015.
 */
public class ChangeFragmentEvent {

    public final Class<? extends Activity> receiver;
    public final Fragment fragment;

    public ChangeFragmentEvent(Class<? extends Activity> receiver, Fragment fragment) {
        this.receiver = receiver;
        this.fragment = fragment;
    }
}
