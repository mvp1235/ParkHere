package edu.sjsu.team408.parkhere;

/**
 * Created by robg on 11/17/17.
 */

public class ChatBubble {
    private Message message;
    private boolean isMyMessage;

    public ChatBubble(Message message, boolean isMyMessage) {
        this.message = message;
        this.isMyMessage = isMyMessage;
    }

    public Message getMessage() {
        return message;
    }

    public boolean getIsMyMessage() {
        return isMyMessage;
    }
}
