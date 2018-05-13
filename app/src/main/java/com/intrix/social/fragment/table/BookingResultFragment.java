package com.intrix.social.fragment.table;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intrix.social.R;
import com.intrix.social.common.AppMenu;
import com.intrix.social.model.event.ChangePageRequest;

import de.greenrobot.event.EventBus;

/**
 * Created by yarolegovich on 29.12.2015.
 */
public class BookingResultFragment extends StepFragment<String> implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_booking_result, container, false);

        v.findViewById(R.id.next).setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        EventBus.getDefault().post(new ChangePageRequest(AppMenu.FOOD));
    }
}
