package com.intrix.social.chat.adapters;

import android.text.Spannable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.intrix.social.R;
import com.intrix.social.chat.abstractions.ActivityWithOverlay;
import com.intrix.social.chat.fragments.ImagePresentationFragment;
import com.intrix.social.chat.model.Msg;

/**
 * Created by yarolegovich on 7/29/15.
 */
public class ChatViewHolder implements View.OnClickListener {

    private Msg mMessage;
    private ActivityWithOverlay mActivityWithOverlay;

    ImageView mUserImage;
    ImageView mMessageImage;
    TextView mMessageContent;
    TextView mTimeStampText;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.message_image:
                mActivityWithOverlay.placeFragmentToOverlay
                        (ImagePresentationFragment.newInstance(mMessage.getContent()));
                break;
        }
    }

    public ImageView getMessageImage() {
        return mMessageImage;
    }

    public TextView getMessageContent() {
        return mMessageContent;
    }

    public ImageView getUserImage() {
        return mUserImage;
    }

    public void setMessageContent(String text) {
        mMessageContent.setText(text);
    }

    public void setMessageContent(Spannable spannable) {
        mMessageContent.setText(spannable);
    }

    public void setTimeStampText(String text) {
        mTimeStampText.setText(text);
    }

    public Msg getMessage() {
        return mMessage;
    }

    public void setMessage(Msg message) {
        mMessage = message;
    }

    public void setActivityWithOverlay(ActivityWithOverlay activityWithOverlay) {
        mActivityWithOverlay = activityWithOverlay;
    }
}
