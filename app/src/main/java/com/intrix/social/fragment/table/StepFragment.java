package com.intrix.social.fragment.table;

import android.support.v4.app.Fragment;

import java.util.List;

/**
 * Created by yarolegovich on 29.12.2015.
 */
public abstract class StepFragment<T> extends Fragment {

    private ProceedListener<T> mProceedListener;
    protected List<T> mPreviousResults;

    public void onProceed(StepFragment<T> next, T result) {
        mPreviousResults.add(result);
        if (next != null) {
            next.setPreviousResults(mPreviousResults);
            next.setProceedListener(mProceedListener);
        }
        mProceedListener.onProceed(next);
    }

    public void setProceedListener(ProceedListener<T> proceedListener) {
        mProceedListener = proceedListener;
    }

    public void setPreviousResults(List<T> previousResults) {
        mPreviousResults = previousResults;
    }
}
