package com.intrix.social.fragment.table;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.intrix.social.R;
import com.intrix.social.common.view.DotBarView;
import com.intrix.social.utils.Utils;

import java.util.Arrays;

/**
 * Created by yarolegovich on 30.12.2015.
 */
public class ReserveLaterFragment extends StepFragment<String> implements View.OnClickListener {

    private Spinner mTimeOfArrival;
    private Spinner mSeatingPreferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reserve_table_later, container, false);

        mTimeOfArrival = (Spinner) v.findViewById(R.id.spinner_time);
        ArrayAdapter<String> times = new ArrayAdapter<>(getActivity(),
                R.layout.item_spinner_fancy_arrow,
                android.R.id.text1,
                Utils.getArrivalTimes(getActivity())
        );
        times.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTimeOfArrival.setAdapter(times);

        mSeatingPreferences = (Spinner) v.findViewById(R.id.spinner_preferences);
        ArrayAdapter<String> prefs = new ArrayAdapter<>(getActivity(),
                R.layout.item_spinner_fancy_arrow,
                android.R.id.text1,
                getResources().getStringArray(R.array.tableOptions)
        );
        prefs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSeatingPreferences.setAdapter(prefs);

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
        onProceed(new BookingResultFragment()   , "");
    }
}
