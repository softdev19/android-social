package com.intrix.social.fragment.table;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intrix.social.R;

/**
 * Created by yarolegovich on 29.12.2015.
 */
public class LaterNowFragment extends StepFragment<String> implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_later_now, container, false);

        v.findViewById(R.id.later).setOnClickListener(this);
        v.findViewById(R.id.now).setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        String selected = ((TextView) v).getText().toString();
        switch (v.getId()) {
            case R.id.later:
                onProceed(new ReserveLaterFragment(), selected);
                break;
            case R.id.now:
                onProceed(new NumberOfCoversFragment(), selected);
                break;
        }
    }
}
