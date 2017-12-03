package edu.sjsu.team408.parkhere;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by robg on 11/20/17.
 */

//Properties that don't map to class fields are ignored
//when serializing to a class annotated with this annotation.
@IgnoreExtraProperties
public class Message {
    private String authorId;
    private String author;
    private String content;
    private Date timestamp;

    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Message(String authorId,
                   String author,
                   String content,
                   Date timestamp) {
        this.authorId = authorId;
        this.author = author;
        this.content = content;
        this.timestamp = timestamp;
    }

    @Exclude //Marks a field as excluded from the Database.
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("authorId", authorId);
        result.put("author", author);
        result.put("content", content);
        result.put("timestamp", timestamp);

        return result;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
