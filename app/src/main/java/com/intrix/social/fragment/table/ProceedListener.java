package com.intrix.social.fragment.table;

import java.util.List;

/**
 * Created by yarolegovich on 29.12.2015.
 */
public interface ProceedListener<T> {
    void onProceed(StepFragment<T> nextFragment);
}
