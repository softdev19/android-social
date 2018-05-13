package com.intrix.social.fragment.table;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.intrix.social.R;
import com.intrix.social.model.event.StartTableSearchEvent;
import com.intrix.social.utils.Utils;

import de.greenrobot.event.EventBus;

/**
 * Created by yarolegovich on 29.12.2015.
 */
public class TimeOfArrivalFragment extends StepFragment<String> implements View.OnClickListener {

    private Spinner mTimeOfArrival;
    private Spinner mSeatingPreferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_table_preferences, container, false);

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

        v.findViewById(R.id.next).setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        onProceed(new BookingResultFragment(), "");
    }
}
