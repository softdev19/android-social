package com.intrix.social.chat.controllers;


import android.util.SparseArray;

import com.intrix.social.chat.abstractions.MessageProcessor;
import com.intrix.social.chat.abstractions.MessageProcessorFactory;
import com.intrix.social.chat.model.Message;


/**
 * Created by yarolegovich on 7/29/15.
 */
public class MessageProcessorPool implements MessageProcessorFactory {

    public SparseArray<MessageProcessor> mCache;

    public MessageProcessorPool() {
        mCache = new SparseArray<>();
    }

    @Override
    public MessageProcessor getMessageProcessor(int messageType) {
        MessageProcessor processor = mCache.get(messageType);
        if (processor == null) {
            switch (messageType) {
                case Message.MESSAGE_TEXT:
                    processor = new PlainTextProcessor();
                    break;
                case Message.MESSAGE_IMAGE:
                    processor = new ImageProcessor();
                    break;
            }
            mCache.put(messageType, processor);
        }
        return processor;
    }
}
