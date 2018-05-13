package com.intrix.social.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.clans.fab.FloatingActionMenu;
import com.intrix.social.R;
import com.intrix.social.common.view.ShareDialog;
import com.intrix.social.model.Event;
import com.intrix.social.utils.Utils;

import io.realm.Realm;

/**
 * Created by yarolegovich on 23.12.2015.
 */
public class EventFragment extends Fragment implements View.OnClickListener {

    public static final String EXTRA_ID = "event.id";

    private Event mEvent;
    private Realm mRealm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getInstance(getActivity());
        int id = getActivity().getIntent().getIntExtra(EXTRA_ID, -1);
        mEvent = mRealm.where(Event.class).equalTo("id", id).findFirst();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event, container, false);
        Utils.showToolbar(getActivity());
        TextView artist = (TextView) v.findViewById(R.id.artist_name);
        artist.setText(mEvent.getArtist());
        TextView time = (TextView) v.findViewById(R.id.event_time);
        time.setText(mEvent.getEventTime());
        TextView amount = (TextView) v.findViewById(R.id.amount);
        amount.setText(mEvent.getAmount());
        TextView title = (TextView) v.findViewById(R.id.title);
        title.setText(getString(R.string.about_artist, mEvent.getArtist()));
        TextView description = (TextView) v.findViewById(R.id.event_description);
        description.setText(mEvent.getArtistDescription());

        TextView hashtagsTitle = (TextView) v.findViewById(R.id.hashtags_title);
        hashtagsTitle.setText(getString(R.string.events_hashtags, mEvent.getName(), mEvent.getArtist()));

        Button rsvpBtn = (Button) v.findViewById(R.id.rsvp_now);
        rsvpBtn.setOnClickListener(v1 -> {
            Snackbar.make(v, "App is not live yet. Please submit app for approval after publishing on playstore.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        });

        final View overlay = v.findViewById(R.id.overlay);
        final FloatingActionMenu fabMenu = (FloatingActionMenu) v.findViewById(R.id.fmenu);
        fabMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                overlay.setVisibility(opened ? View.VISIBLE : View.GONE);
            }
        });

        fabMenu.findViewById(R.id.fab_share).setOnClickListener(this);
        fabMenu.findViewById(R.id.fab_favorite).setOnClickListener(this);
        fabMenu.findViewById(R.id.fab_pay).setOnClickListener(this);
        fabMenu.findViewById(R.id.fab_rspv).setOnClickListener(this);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        final ImageView imageView = (ImageView) v.findViewById(R.id.wall_image);
        Glide.with(activity).load(mEvent.getPoster())
                .placeholder(R.drawable.events)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        imageView.setColorFilter(Color.parseColor("#78000000"));
                        return false;
                    }
                })
                .into(imageView);

        v.findViewById(R.id.back).setOnClickListener(this);

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_share:
                String toShare = mEvent.getName() + '\n' +
                        mEvent.getArtist() + '\n' +
                        mEvent.getEventDate() + '\n' +
                        mEvent.getArtistDescription();
                ShareDialog.showToShare(getActivity(), toShare);
                break;
            case R.id.back:
                getActivity().finish();
                break;
            default:
                Snackbar.make(v, "App is not live yet. Please submit app for approval after publishing on playstore.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
        }
    }
}
