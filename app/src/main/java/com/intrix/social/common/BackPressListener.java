package com.intrix.social.common;

/**
 * Created by yarolegovich on 05.01.2016.
 */
public interface BackPressListener {
    /*
     * @return true  - if back press was handled
     *         false - if caller should handle it
     */
    boolean onBackPressed();
}
