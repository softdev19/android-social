package com.intrix.social;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionMenu;
import com.intrix.social.model.Event;

import io.realm.Realm;

/**
 * Created by yarolegovich on 29.12.2015.
 */
public class EventActivity extends AppCompatActivity {

    public static final String EVENT_ID = "event.id";

    private Realm mRealm;
    private Event mEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        mRealm = Realm.getInstance(this);
        int id = getIntent().getIntExtra(EVENT_ID, -1);
        mEvent = mRealm.where(Event.class).equalTo("id", id).findFirst();

        TextView artist = (TextView) findViewById(R.id.artist_name);
        artist.setText(mEvent.getArtist());
        TextView location = (TextView) findViewById(R.id.event_location);
        location.setText(mEvent.getLocation());
        TextView time = (TextView) findViewById(R.id.event_time);
        time.setText(mEvent.getEventTime());
        TextView amount = (TextView) findViewById(R.id.amount);
        amount.setText(mEvent.getAmount());
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(getString(R.string.about_artist, mEvent.getArtist()));
        TextView description = (TextView) findViewById(R.id.event_description);
        description.setText(mEvent.getArtistDescription());

//        final View overlay = findViewById(R.id.overlay);
//        final FloatingActionMenu fabMenu = (FloatingActionMenu) findViewById(R.id.fmenu);
//        fabMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
//            @Override
//            public void onMenuToggle(boolean opened) {
//                overlay.setVisibility(opened ? View.VISIBLE : View.GONE);
//            }
//        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
