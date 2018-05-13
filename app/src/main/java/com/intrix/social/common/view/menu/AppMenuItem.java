package com.intrix.social.common.view.menu;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;

/**
 * Created by yarolegovich on 01.01.2016.
 */
public class AppMenuItem implements MenuItem {

    private FontAwesome.Icon mIcon;
    private String mTitle;
    private int mNotifications;

    public AppMenuItem(String title, FontAwesome.Icon icon) {
        this(title, icon, 0);
    }

    public AppMenuItem(String title, FontAwesome.Icon icon, int notifications) {
        mIcon = icon;
        mTitle = title;
        mNotifications = notifications;
    }

    public FontAwesome.Icon getIcon() {
        return mIcon;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getNotifications() {
        return mNotifications;
    }
}
