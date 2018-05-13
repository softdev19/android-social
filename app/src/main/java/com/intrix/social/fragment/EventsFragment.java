package com.intrix.social.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.intrix.social.EventActivity;
import com.intrix.social.R;
import com.intrix.social.adapter.EventsListAdapter;
import com.intrix.social.adapter.EventsSpinnerAdapter;
import com.intrix.social.model.Event;
import com.intrix.social.utils.ImageUtils;
import com.intrix.social.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

/**
 * Created by yarolegovich on 22.12.2015.
 */
public class EventsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private Realm mRealm;

    private MenuItem mExtended, mSimple;

    private EventsListAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getInstance(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_events, container, false);
        Utils.showToolbar(getActivity());
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ImageView imageView = (ImageView) v.findViewById(R.id.wall_image);
        ImageUtils.setBitmapHeader(activity, imageView, R.drawable.events);

        Map<String, String> tempCats = new HashMap<>();
        Map<String, String> tempLocs = new HashMap<>();

        List<Event> events = mRealm.where(Event.class).findAll();
        List<String> cats = new ArrayList<>();
        List<String> locs = new ArrayList<>();
        for(Event event :events) {
            String key = event.getCategory();
            if (tempCats.containsKey(key)) {
            } else {
                tempCats.put(key, key);
            }
            key = event.getLocation();
            if (tempLocs.containsKey(key)) {
            } else {
                tempLocs.put(key, key);
            }
        }

        Iterator it = tempCats.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            //it.remove(); // avoids a ConcurrentModificationException
            cats.add((String)pair.getKey());
        }
//        for (String temp : tempCats)
//        {
//            cats.add(temp);
//        }

//        for (String temp : tempLocs)
//        {
//            locs.add(temp);
//        }

        it = tempLocs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            //it.remove(); // avoids a ConcurrentModificationException
            locs.add((String)pair.getKey());
        }



        Spinner location = (Spinner) v.findViewById(R.id.spinner_location);
        EventsSpinnerAdapter adapterL = new EventsSpinnerAdapter(getActivity(),locs);
        adapterL.setIcon(R.drawable.ic_location_on_white_24dp);
        location.setAdapter(adapterL);

        Spinner event = (Spinner) v.findViewById(R.id.spinner_event_category);
        EventsSpinnerAdapter adapterE = new EventsSpinnerAdapter(getActivity(),cats);
        adapterE.setIcon(R.drawable.ic_today_white_24dp);
        event.setAdapter(adapterE);

        ListView list = (ListView) v.findViewById(android.R.id.list);
        mAdapter = new EventsListAdapter(getActivity(), getData());
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(this);

        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_events, menu);
        mSimple = menu.findItem(R.id.simple);
        mExtended = menu.findItem(R.id.extended);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.extended:
                mSimple.setVisible(true);
                mExtended.setVisible(false);
                mAdapter.setMode(EventsListAdapter.Mode.EXTENDED);
                break;
            case R.id.simple:
                mSimple.setVisible(false);
                mExtended.setVisible(true);
                mAdapter.setMode(EventsListAdapter.Mode.SIMPLE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<Event> getData() {
        return mRealm.where(Event.class).findAll();
    }

    private List<Event> getData(String location, String category) {
        return mRealm.where(Event.class).findAll();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(getActivity(), EventActivity.class);
        i.putExtra(EventActivity.EVENT_ID, getData().get(position).getId());
        startActivity(i);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Utils.showToolbar(getActivity());
    }

}
