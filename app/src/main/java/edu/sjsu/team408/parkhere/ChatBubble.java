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

    public boolean getMyMessage() {
        return myMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatBubble that = (ChatBubble) o;

        if (myMessage != that.myMessage) return false;
        return content != null ? content.equals(that.content) : that.content == null;
    }
}
