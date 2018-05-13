package com.intrix.social.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intrix.social.MainActivity;
import com.intrix.social.R;
import com.intrix.social.common.AppMenu;
import com.intrix.social.model.event.ChangeFragmentEvent;
import com.intrix.social.model.event.ChangePageRequest;
import com.intrix.social.utils.Utils;

import de.greenrobot.event.EventBus;

/**
 * Created by yarolegovich on 28.01.2016.
 */
public class OrderFeedbackOrSkipFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feedback_or_skip, container, false);

        v.findViewById(R.id.btn_leave_feedback).setOnClickListener(this);
        v.findViewById(R.id.btn_skip).setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_leave_feedback:
                EventBus.getDefault().post(new ChangeFragmentEvent(MainActivity.class,
                        new OrderFeedbackFragment()
                ));
                break;
            case R.id.btn_skip:
                EventBus.getDefault().post(new ChangePageRequest(AppMenu.HOME));
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Utils.hideToolbar(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.showToolbar(getActivity());
    }
}
