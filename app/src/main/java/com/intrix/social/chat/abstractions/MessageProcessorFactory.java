package com.intrix.social.chat.abstractions;

/**
 * Created by yarolegovich on 7/29/15.
 */
public interface MessageProcessorFactory {
    MessageProcessor getMessageProcessor(int messageType);
}
