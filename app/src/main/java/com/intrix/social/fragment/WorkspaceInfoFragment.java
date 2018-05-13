package com.intrix.social.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.intrix.social.MainActivity;
import com.intrix.social.R;
import com.intrix.social.model.event.ChangeFragmentEvent;
import com.intrix.social.utils.Utils;

import de.greenrobot.event.EventBus;

/**
 * Created by yarolegovich on 02.01.2016.
 */
public class WorkspaceInfoFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_work_info, container, false);

        ImageView bg = (ImageView) v.findViewById(R.id.bg);
        Glide.with(getActivity()).load(R.drawable.ws_info_bg).into(bg);

        //v.findViewById(R.id.skip).setOnClickListener(this);
        v.findViewById(R.id.next).setOnClickListener(this);
        Utils.hideToolbar(getActivity());

        return v;
    }

    @Override
    public void onClick(View v) {
        EventBus.getDefault().post(new ChangeFragmentEvent(MainActivity.class,
                new NewWorkspaceFragment()
        ));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Utils.hideToolbar(context);
    }
}
