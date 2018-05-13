package com.intrix.social.fragment;


import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import com.intrix.social.R;
import com.intrix.social.common.view.SBtnView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackFragment extends Fragment implements View.OnClickListener {


    RatingBar eatRating, serviceRating, vibeRating, valueRating, drinkRating;
    SBtnView loveItButton;

    public FeedbackFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        AppCompatActivity a = (AppCompatActivity) getActivity();
        ActionBar ab = a.getSupportActionBar();
        assert ab != null;
        ab.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(a, R.color.splash_accent)));

        eatRating = (RatingBar) view.findViewById(R.id.rtb_eat);
        drinkRating = (RatingBar) view.findViewById(R.id.rtb_drink);
        vibeRating = (RatingBar) view.findViewById(R.id.rtb_vibe);
        valueRating = (RatingBar) view.findViewById(R.id.rtb_value);
        serviceRating = (RatingBar) view.findViewById(R.id.rtb_service);

        loveItButton = (SBtnView) view.findViewById(R.id.btn_loveItFeedback);
        loveItButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_loveItFeedback) {
            int eatRatingValue = (int) this.eatRating.getRating();
            int serviceRatingValue = (int) this.serviceRating.getRating();
            int vibeRatingValue = (int) this.vibeRating.getRating();
            int valueRatingValue = (int) this.valueRating.getRating();
            int drinkRatingValue = (int) this.drinkRating.getRating();
            Toast.makeText(getActivity(), "Eating:" + eatRatingValue, Toast.LENGTH_SHORT).show();
        }
    }
}
