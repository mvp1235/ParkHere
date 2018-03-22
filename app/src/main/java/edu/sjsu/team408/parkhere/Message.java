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

    /**
     * Default constructor required for calls to DataSnapshot.getValue(Post.class)
     */
    public Message() {
    }

    /**
     * Construct a new Message object.
     * @param authorId the unique id of the author of a message
     * @param author the name of the author of a message
     * @param content a string containing the content of a message
     * @param timestamp a data containing the timestamp of a message
     */
    public Message(String authorId,
                   String author,
                   String content,
                   Date timestamp) {
        this.authorId = authorId;
        this.author = author;
        this.content = content;
        this.timestamp = timestamp;
    }

    /**
     * Create a HashMap object.
     * @return a result HashMap object
     */
    @Exclude //Marks a field as excluded from the Database.
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("authorId", authorId);
        result.put("author", author);
        result.put("content", content);
        result.put("timestamp", timestamp);

        return result;
    }

    /**
     * Get the unique id of the author of a message.
     * @return author's unique id
     */
    public String getAuthorId() {
        return authorId;
    }

    /**
     * Get the content of a message.
     * @return a string containing the content of a message
     */
    public String getContent() {
        return content;
    }

    /**
     * Set the content of a message.
     * @param content a string containing a message
     */
    public void setContent(String content) {
        this.content = content;
    }
}
