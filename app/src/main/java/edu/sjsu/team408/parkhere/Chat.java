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
    private String lastMessage;
    private Date timestamp;

    public Chat() {
    }

    public Chat(String lastMessage, Date timestamp) {
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
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
}
