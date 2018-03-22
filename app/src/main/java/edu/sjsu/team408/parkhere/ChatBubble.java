package edu.sjsu.team408.parkhere;

/**
 * Created by robg on 11/17/17.
 */

public class ChatBubble {
    private Message message;
    private boolean isMyMessage;

    /**
     * Construct a ChatBubble object.
     * @param message a message object
     * @param isMyMessage a boolean value of whether the message belongs to this chat owner.
     */
    public ChatBubble(Message message, boolean isMyMessage) {
        this.message = message;
        this.isMyMessage = isMyMessage;
    }

    /**
     * Get the message object.
     * @return message
     */
    public Message getMessage() {
        return message;
    }

    /**
     * Get the boolean value isMyMessage.
     * @return isMyMessage
     */
    public boolean getIsMyMessage() {
        return isMyMessage;
    }
}
