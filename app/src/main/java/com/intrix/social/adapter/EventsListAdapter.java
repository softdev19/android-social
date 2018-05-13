package com.intrix.social.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.intrix.social.NoToolbarUniversalActivity;
import com.intrix.social.R;
import com.intrix.social.fragment.EventFragment;
import com.intrix.social.model.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by yarolegovich on 22.12.2015.
 */
public class EventsListAdapter extends ArrayAdapter<Event> {

    private SimpleDateFormat FORMAT = new SimpleDateFormat("dd MMM", Locale.getDefault());

    private Mode mMode = Mode.SIMPLE;

    public EventsListAdapter(Context context) {
        this(context, new ArrayList<Event>());
    }

    public EventsListAdapter(Context context, List<Event> data) {
        super(context, 0, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(mMode.getLayout(), parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else vh = (ViewHolder) convertView.getTag();

        vh.mEvent = getItem(position);

        mMode.process(vh);

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return mMode.getLayout() == R.layout.item_event ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public void setMode(Mode mode) {
        mMode = mode;
        notifyDataSetChanged();
    }

    private class ViewHolder implements View.OnClickListener {

        private Event mEvent;

        TextView eventDate;
        TextView eventName;
        TextView people;
        TextView time;

        ImageView eventImage;

        public ViewHolder(View v) {
            eventDate = (TextView) v.findViewById(R.id.event_date);
            eventName = (TextView) v.findViewById(R.id.text);
            people = (TextView) v.findViewById(R.id.tickets_remaining);
            time = (TextView) v.findViewById(R.id.time);
            eventImage = (ImageView) v.findViewById(R.id.event_image);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), NoToolbarUniversalActivity.class);
            intent.putExtra(NoToolbarUniversalActivity.EXTRA_TOKEN, EventFragment.class);
            intent.putExtra(EventFragment.EXTRA_ID, mEvent.getId());
            getContext().startActivity(intent);
        }
    }

    public enum Mode {
        EXTENDED(R.layout.item_event_extended) {
            @Override
            public void process(ViewHolder vh) {
                super.process(vh);
                Glide.with(vh.eventName.getContext())
                        .load(vh.mEvent.getPoster())
                        .into(vh.eventImage);
                vh.eventImage.setColorFilter(Color.parseColor("#64000000"));
            }
        },
        SIMPLE(R.layout.item_event);

        private int mLayout;
        public void process(ViewHolder vh) {
            Event event = vh.mEvent;
            Context context = vh.eventName.getContext();
            vh.eventDate.setText(event.getEventDate());
            vh.eventName.setText(event.getName());
            vh.people.setText(context.getString(R.string.tickets_remaining, event.getNoOfPeople()));
            if (vh.time != null) {
                String time = event.getEventTime();
                vh.time.setText(time.substring(0, time.indexOf(" ")));
            }
        }

        Mode(int layout) {
            mLayout = layout;
        }

        public int getLayout() {
            return mLayout;
        }
    }
}
