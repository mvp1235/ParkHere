package edu.sjsu.team408.parkhere;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by robg on 11/21/17.
 */

@IgnoreExtraProperties
public class Chat {
    private String chatWithName;
    private String chatWithUid;
    private String lastMessage;
    private Date timestamp;

    public Chat() {
    }

    public Chat(String chatWithName, String chatWithUid, String lastMessage, Date timestamp) {
        this.chatWithName = chatWithName;
        this.chatWithUid = chatWithUid;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
//        result.put("chatWithName", chatWithName);
//        result.put("chatWithUid", chatWithUid);
        result.put("lastMessage", lastMessage);
        result.put("timestamp", timestamp);

        return result;
    }

    /**
     * Get the last/most recent message.
     * @return a lastMessage
     */
    public String getLastMessage() {
        return lastMessage;
    }

    /**
     * Set the content of the lastMessage
     * @param lastMessage a string to be used to set as the lastMessage
     */
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    /**
     * Get the timestamp of the chat.
     * @return timestamp of the chat
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Set the timestamp of the chat.
     * @param timestamp timestamp of the chat
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Get the name the user is chatting with.
     * @return chatWithName
     */
    public String getChatWithName() {
        return chatWithName;
    }

    /**
     * Set the name the user is chatting with.
     * @param chatWithName the name the user is chatting with.
     */
    public void setChatWithName(String chatWithName) {
        this.chatWithName = chatWithName;
    }

    /**
     * Get the unique id of another user this chat owner is chatting with.
     * @return chatWithUid
     */
    public String getChatWithUid() {
        return chatWithUid;
    }

    /**
     * Set the unique id of another user this chat owner is chatting with.
     * @param chatWithUid A unique id of another user this chat owner is chatting with.
     */
    public void setChatWithUid(String chatWithUid) {
        this.chatWithUid = chatWithUid;
    }
}
