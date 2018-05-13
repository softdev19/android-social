package com.intrix.social.chat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.intrix.social.R;
import com.intrix.social.chat.model.Msg;

import java.util.List;

/**
 * Created by yarolegovich on 04.01.2016.
 */
public class NewChatAdapter extends ArrayAdapter<Msg> {


    public NewChatAdapter(Context context, List<Msg> messages) {
        super(context, 0, messages);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MsgHolder mh;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(getLayout(position), parent, false);
            mh = new MsgHolder(convertView);
            convertView.setTag(mh);
        } else mh = (MsgHolder) convertView.getTag();

        Msg msg = getItem(position);

        mh.message.setText(msg.getContent());

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        Msg msg = getItem(position);
        return msg.getSender();
        //return position % 2 == 0 ? 0 : 1;
    }

    public int getLayout(int position) {
        return getItemViewType(position) == 0 ? R.layout.item_chat_blue : R.layout.item_chat_grey;
    }

    private static class MsgHolder {
        TextView message;

        public MsgHolder(View v) {
            message = (TextView) v.findViewById(R.id.message_text);
        }
    }
}
