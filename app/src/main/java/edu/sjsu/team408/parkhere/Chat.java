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

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getChatWithName() {
        return chatWithName;
    }

    public void setChatWithName(String chatWithName) {
        this.chatWithName = chatWithName;
    }

    public String getChatWithUid() {
        return chatWithUid;
    }

    public void setChatWithUid(String chatWithUid) {
        this.chatWithUid = chatWithUid;
    }
}
