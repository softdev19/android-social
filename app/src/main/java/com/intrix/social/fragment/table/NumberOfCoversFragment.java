package com.intrix.social.fragment.table;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intrix.social.R;
import com.intrix.social.common.view.DotBarView;

import java.util.Arrays;

/**
 * Created by yarolegovich on 29.12.2015.
 */
public class NumberOfCoversFragment extends StepFragment<String> implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_no_of_covers, container, false);

        DotBarView dotBar = (DotBarView) v.findViewById(R.id.dot_bar);
        dotBar.setDots(Arrays.asList(new DotBarView.Dot("2", "15 mins"),
                new DotBarView.Dot("4", "30 mins"),
                new DotBarView.Dot("6", "45 mins"),
                new DotBarView.Dot("8", "1 hour"),
                new DotBarView.Dot("8+", "Waiting list")
        ));
        dotBar.setSelected(0);

        v.findViewById(R.id.next).setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        onProceed(new TimeOfArrivalFragment(), "");
    }
}
