package edu.sjsu.team408.parkhere;

/**
 * Created by robg on 11/17/17.
 */

public class ChatBubble {
    private String content;
    private boolean myMessage;

    public ChatBubble(String content, boolean myMessage) {
        this.content = content;
        this.myMessage = myMessage;
    }

    public String getContent() {
        return content;
    }

    public boolean myMessage() {
        return myMessage;
    }
}
