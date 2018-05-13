package com.intrix.social.chat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.intrix.social.R;
import com.intrix.social.chat.abstractions.ActivityWithOverlay;
import com.intrix.social.chat.abstractions.MessageProcessorFactory;
import com.intrix.social.chat.model.Msg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by yarolegovich on 7/14/15.
 */
public class ChatAdapter extends ArrayAdapter<Msg> {

    private final int[] mViewTypes = {R.layout.item_chat_message, R.layout.item_chat_response};

    private ActivityWithOverlay mWithOverlay;

    private MessageProcessorFactory mMessageProcessors;

    private Context mContext;
    private List<Msg> mData;

    public ChatAdapter(Context context, List<Msg> objects) {
        super(context, 0);
        mData = objects == null ? new ArrayList<Msg>() : new ArrayList<>(objects);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ChatViewHolder();

            convertView = inflateItem(position, parent);

            viewHolder.mMessageContent = (TextView) convertView.findViewById(R.id.message_content);
            viewHolder.mUserImage = (ImageView) convertView.findViewById(R.id.user_chat_image);
            viewHolder.mMessageImage = (ImageView) convertView.findViewById(R.id.message_image);
            viewHolder.mTimeStampText = (TextView) convertView.findViewById(R.id.timestamp);

            viewHolder.setActivityWithOverlay(mWithOverlay);

            convertView.setTag(viewHolder);
        } else viewHolder = (ChatViewHolder) convertView.getTag();

        Msg message = mData.get(position);

        viewHolder.setMessage(message);

        mMessageProcessors.getMessageProcessor(message.getMsgType()).displayMessage(mContext, viewHolder);

        return convertView;
    }

    private View inflateItem(int position, ViewGroup container) {
        int viewType = getItemViewType(position);
        return LayoutInflater.from(mContext).inflate(mViewTypes[viewType], container, false);
    }

    @Override
    public Msg getItem(int position) {
        return mData.get(position);
    }

    @Override
    public boolean isEmpty() {
        return mData.isEmpty();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).getSender();
    }

    @Override
    public void addAll(Collection<? extends Msg> collection) {
        for (Msg msg : collection) add(msg);
        notifyDataSetChanged();
    }

    @Override
    public void add(Msg object) {
        mData.add(object);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }


    public void addMessage(Msg message) {
        mData.add(message);
        notifyDataSetChanged();
    }

    @Override
    public void clear() {
        mData.clear();
    }

    public static class ChatAdapterBuilder {
        private ChatAdapter instance;
        public ChatAdapterBuilder(Context context) {
            instance = new ChatAdapter(context, null);
        }
        public ChatAdapterBuilder withData(List<Msg> messages) {
            instance.mData = new ArrayList<>(messages);
            return this;
        }
        public ChatAdapterBuilder withOverlay(ActivityWithOverlay overlay) {
            instance.mWithOverlay = overlay;
            return this;
        }
        public ChatAdapterBuilder withProcessorFactory(MessageProcessorFactory factory) {
            instance.mMessageProcessors = factory;
            return this;
        }
        public ChatAdapter build() {
            return instance;
        }
    }
}
