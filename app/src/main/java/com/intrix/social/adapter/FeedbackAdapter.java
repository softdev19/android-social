package com.intrix.social.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.intrix.social.MainApplication;
import com.intrix.social.R;
import com.intrix.social.common.view.RateBar;
import com.intrix.social.model.FeedbackItem;
import com.intrix.social.model.Item;

import java.util.Collections;
import java.util.List;

/**
 * Created by yarolegovich on 22.12.2015.
 */
public class FeedbackAdapter extends ArrayAdapter<Item> {

    private boolean mRated;

    public FeedbackAdapter(Context context) {
        this(context, Collections.<Item>emptyList());
    }

    public FeedbackAdapter(Context context, List<Item> data) {
        super(context, 0, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_feedback, parent, false);
            vh = new ViewHolder(convertView, getItem(position).getId());
            convertView.setTag(vh);
        } else vh = (ViewHolder) convertView.getTag();

        Item item = getItem(position);
        vh.name.setText(item.getName());

        return convertView;
    }

    public boolean isRated() {
        return mRated;
    }

    private class ViewHolder implements RateBar.OnRatingChangedListener {

        RateBar rateBar;
        TextView name;

        public ViewHolder(View v, int itemId) {
            name = (TextView) v.findViewById(R.id.item_name);
            rateBar = (RateBar) v.findViewById(R.id.rate_bar);

            rateBar.setListener(this);
            rateBar.setTag(itemId);
        }

        @Override
        public void onRatingChanged(int newRating) {
            mRated = true;

            List<FeedbackItem> feedbackItems = MainApplication.data.feedbackItems;

            for(FeedbackItem fBItem : feedbackItems) {
                if (fBItem.getItemId() == (int)rateBar.getTag() ){//MainApplication.data.currentSelectedItemFeedback) {
                    fBItem.setRatings("" + newRating);
                    return;
                }
            }
            //feedbackItems.get((int)rateBar.getTag()).setRatings(""+newRating);
        }
    }
}
