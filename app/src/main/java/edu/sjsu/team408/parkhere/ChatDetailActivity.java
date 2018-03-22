package edu.sjsu.team408.parkhere;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatDetailActivity extends AppCompatActivity {

    public static final String CURRENT_CHAT_KEY = "chat_key";
    private ListView listView;
    private Button sendButton;
    private Button deleteChatButton;
    private EditText editText;
    private TextView chatWithLabel;
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayList<User> userList;

    boolean myMessage = true;
    private List<ChatBubble> chatBubbleList;
    private ArrayAdapter<ChatBubble> adapter;

    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private String chatKey;
    private boolean chatBetweenDoesExist;

    private DatabaseReference membersOfChatsReference;
    private DatabaseReference chatsReference;
    private DatabaseReference messagesReference;
    private DatabaseReference userChatsReference;
    private DatabaseReference userListReference;
    private boolean hasListenedToMessagesRef;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        chatBubbleList = new ArrayList<>();
        userList = new ArrayList<>();

        listView = findViewById(R.id.chat_listView);
        sendButton = findViewById(R.id.send_message_button);
        editText = findViewById(R.id.input_message_editText);
        chatWithLabel = findViewById(R.id.chattingWithLabel);
        autoCompleteTextView = findViewById(R.id.toAutoCompleteTextView);

        chatKey = "";
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        setupChildReferences();

        // Get chat key from intent
        final String[] foundChatWithName = new String[1];
        if (getIntent().hasExtra(CURRENT_CHAT_KEY)) {
            chatKey = getIntent().getStringExtra(CURRENT_CHAT_KEY);
            setupChildReferencesDependentOnChatKey();
            userChatsReference
                    .child(chatKey)
                    .addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Chat chat = dataSnapshot.getValue(Chat.class);
                                    foundChatWithName[0] = chat.getChatWithName();
                                    hideAutoCompleteTextViewAndShowChatWithNameLabel(
                                            foundChatWithName[0]
                                    );
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            }
                    );
        } else {
            chatKey = "";
            foundChatWithName[0] = "";
        }

        final String author = user.getEmail().split("@")[0];

        //set ListView adapter first
        adapter = new MessageAdapter(this, 0, chatBubbleList);
        listView.setAdapter(adapter);

        // setting up the autocomplete text field
        final ArrayAdapter<String> autoComplete =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        userListReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();
                autoComplete.clear();
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    String userName = user.child("name").getValue(String.class);
                    if (!userName.equals(author)) {
                        autoComplete.add(userName);
                        userList.add(user.getValue(User.class));
                    }
//                autocompleteAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        autoCompleteTextView.setAdapter(autoComplete);

        // The "to:" field has to be filled in order to enable the sendButton.
//        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) {
//                    if (autoCompleteTextFieldIsValid()) {
//                        sendButton.setEnabled(true);
//                    } else {
//                        sendButton.setEnabled(false);
//                    }
//                }
//            }
//        });

        //event for button SEND
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if input message is empty, then shows error message.
                if (editText.getText().toString().trim().isEmpty()) {
                    makeToast("Please enter some text...");
                } else {
                    if (autoCompleteTextFieldIsValid()) {
                        String chatWithName;
                        String chatWithUid;

                        if (foundChatWithName[0].isEmpty()) {
                            chatWithName = autoCompleteTextView.getText().toString();
                        } else {
                            chatWithName = foundChatWithName[0];
                        }
                        chatWithUid = findUidByName(userList, chatWithName);
                        if (chatWithUid.isEmpty()) {
                            makeToast("The user you want to message to cannot be found." +
                                    "Please enter another user name");
                        } else {
                            hideAutoCompleteTextViewAndShowChatWithNameLabel(chatWithName);
                            //add message to list
                            String messageContent = editText.getText().toString();
                            makeANewChatIfNecessaryAndWriteNewMessage(author, user.getUid(),
                                    chatWithName, chatWithUid, messageContent);
                        }
                    }
                }
            }
        });
        deleteChatButton = findViewById(R.id.deleteChatButton);

        // messagesReference is chatKey dependent.
        // Once chatKey is defined
        if (!chatKey.isEmpty()) {
            listenToMessagesReferenceChanges();
            hasListenedToMessagesRef = true;

            userChatsReference.child(chatKey).addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Chat chat = dataSnapshot.getValue(Chat.class);
                            try {
                                final String chatWithUid = chat.getChatWithUid();
                                final String chatWithName = chat.getChatWithName();
                                deleteChatButton.setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                userListReference
                                                        .child(user.getUid())
                                                        .child(getString(R.string.chats))
                                                        .child(chatKey)
                                                        .removeValue();
                                                userListReference
                                                        .child(user.getUid())
                                                        .child(getString(R.string.currentlyChatWiths))
                                                        .child(chatWithName)
                                                        .removeValue();
                                                messagesReference.removeValue();
                                                userListReference
                                                        .child(chatWithUid)
                                                        .child(getString(R.string.chats))
                                                        .child(chatKey)
                                                        .removeValue();
                                                userListReference
                                                        .child(chatWithUid)
                                                        .child(getString(R.string.currentlyChatWiths))
                                                        .child(author)
                                                        .removeValue();
                                                membersOfChatsReference
                                                        .child(chatKey)
                                                        .removeValue();
                                                finish();
                                            }
                                        }
                                );
                            } catch (NullPointerException e) {

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    }
            );
        }
    }

    private void listenToMessagesReferenceChanges() {
        messagesReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message newMessage = dataSnapshot.getValue(Message.class);
                if (newMessage.getAuthorId().equals(user.getUid())) {
                    addMessageToList(newMessage, true);
                } else {
                    addMessageToList(newMessage, false);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Hide the autocomplete textView and show the name the user is chatting with.
     * @param chatWithName a string of another user's name the current user is chatting with.
     */
    private void hideAutoCompleteTextViewAndShowChatWithNameLabel(String chatWithName) {
        if (autoCompleteTextView.getVisibility() == View.VISIBLE) {
            autoCompleteTextView.setEnabled(false);
            autoCompleteTextView.setVisibility(View.INVISIBLE);
        }
        chatWithLabel.setVisibility(View.VISIBLE);
        chatWithLabel.setText(chatWithName);
    }

    private void chatBetweenExists(final String chatWithUser) {
        userListReference
                .child(user.getUid())
                .child(getString(R.string.currentlyChatWiths))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            String someName = data.getValue(String.class);
                            if (someName.equals(chatWithUser)) {
                                chatBetweenDoesExist = true;
                                break;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    // must be called after chatKey has been initialized.
    private void setupChildReferences() {
        userListReference = mDatabase.child(getString(R.string.Users));
        userChatsReference = userListReference
                .child(user.getUid())
                .child(getString(R.string.chatsOfUsersDatabaseField));
        membersOfChatsReference = mDatabase
                .child(getString(R.string.membersOfChats));

    }
    private void setupChildReferencesDependentOnChatKey() {
//                .child(chatKey);
        chatsReference = mDatabase
                .child("chats")
                .child(chatKey);
        messagesReference = mDatabase.child("messages").child(chatKey);
    }

    /**
     * Add a new chat entry to the database if a current chat session with the
     * another receiving user does not exist, else add the new message entry.
     * @param author the name of the author of the new message
     * @param authorUid the unique id of the author of the new message
     * @param chatWithName the name of the receiving user of the new message
     * @param chatWithUid the unique id of the receiving user of the new message
     * @param messageContent a string that contains the content of the new message
     */
    private void makeANewChatIfNecessaryAndWriteNewMessage(final String author,
                                                           final String authorUid,
                                                           final String chatWithName,
                                                           final String chatWithUid,
                                                           final String messageContent) {
        if (chatKey.isEmpty()) {
            membersOfChatsReference.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                if (data.hasChild(author) && data.hasChild(chatWithName)) {
                                    chatKey = data.getKey();
                                    break;
                                }
                            }
                            // If there is no current chat between author and chatWithName exists,
                            // make a new chat with a new key.
                            if (chatKey.isEmpty())
                                chatKey = mDatabase.child(getString(R.string.chats)).push().getKey();
                            setupChildReferencesDependentOnChatKey();
                            makeNewChat(author, authorUid, chatWithName, chatWithUid);
                            writeNewMessage(author, user.getUid(), chatWithName, chatWithUid, messageContent);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    }
            );

        } else {
            setupChildReferencesDependentOnChatKey();
            writeNewMessage(author, user.getUid(), chatWithName, chatWithUid, messageContent);
        }
        deleteChatButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userListReference
                                .child(user.getUid())
                                .child(getString(R.string.chats))
                                .child(chatKey)
                                .removeValue();
                        userListReference
                                .child(user.getUid())
                                .child(getString(R.string.currentlyChatWiths))
                                .child(chatWithName)
                                .removeValue();
                        messagesReference.removeValue();
                        userListReference
                                .child(chatWithUid)
                                .child(getString(R.string.chats))
                                .child(chatKey)
                                .removeValue();
                        userListReference
                                .child(chatWithUid)
                                .child(getString(R.string.currentlyChatWiths))
                                .child(author)
                                .removeValue();
                        membersOfChatsReference
                                .child(chatKey)
                                .removeValue();
                        finish();
                    }
                }
        );
    }

    /**
     * Make a new chat object and add it to the database.
     * @param author the name of the author of the new chat
     * @param authorUid the unique id of the author of the new chat
     * @param chatWithName the name of the receiving user of the new chat
     * @param chatWithUid the unique id of the receiving user of the new chat
     */
    private void makeNewChat(String author, String authorUid, String chatWithName, String chatWithUid) {
        String Users_field = "/" + getString(R.string.Users) + "/";
        String Users_uid_chats_field = "/" + getString(R.string.chats) + "/";
        String Users_currentlyChatWiths_field = "/" + getString(R.string.currentlyChatWiths) + "/";
        String membersOfChat_field = "/" + getString(R.string.membersOfChats) + "/";
        Date timestamp = new Date();
        String lastMessage = "";
        Chat authorChat = new Chat(chatWithName, chatWithUid, lastMessage, timestamp);
        Chat receiverChat = new Chat(author, authorUid, lastMessage, timestamp);

        Map<String, Object> childUpdates = new HashMap<>();
        final String users_uid_chats_chatKey =
                Users_field + user.getUid() + Users_uid_chats_field + chatKey;
        final String users_uid_currentlyChatWiths_chatWithName =
                Users_field + user.getUid() + Users_currentlyChatWiths_field + chatWithName;
        final String membersOfChat_chatKey_author = membersOfChat_field + chatKey + "/" + author;
        // push to author
        childUpdates.put(users_uid_chats_chatKey, authorChat);
        childUpdates.put(users_uid_currentlyChatWiths_chatWithName, chatWithName);
        childUpdates.put(membersOfChat_chatKey_author, true);

        final String receiver_uid_chats_chatKey =
                Users_field + chatWithUid + Users_uid_chats_field + chatKey;
        final String receiver_uid_currentlyChatWiths_author =
                Users_field + chatWithUid + Users_currentlyChatWiths_field + author;
        final String membersOfChat_chatKey_receiver = membersOfChat_field + chatKey + "/" + chatWithName;
        // push to receiver
        childUpdates.put(receiver_uid_chats_chatKey, receiverChat);
        childUpdates.put(receiver_uid_currentlyChatWiths_author, author);
        childUpdates.put(membersOfChat_chatKey_receiver, true);

        mDatabase.updateChildren(childUpdates);
        if (!hasListenedToMessagesRef)
            listenToMessagesReferenceChanges();
    }

    /**
     * Write a new message entry to the database.
     * @param author the name of the author of the new message
     * @param authorUid the unique id of the author of the new message
     * @param chatWithName the name of the receiving user of the new message
     * @param chatWithUid the unique id of the receiving user of the new message
     * @param messageContent a string that contains the content of the new message
     */
    private void writeNewMessage(String author, String authorUid, String chatWithName,
                                 String chatWithUid, String messageContent) {
        Date timestamp = new Date();
        messagesReference.push()
                .setValue(new Message(authorUid, author, messageContent, timestamp));
        DatabaseReference senderChatsRef = mDatabase.child(getString(R.string.Users))
                .child(user.getUid())
                .child(getString(R.string.chats))
                .child(chatKey);
        DatabaseReference receiverChatsRef = mDatabase.child(getString(R.string.Users))
                .child(chatWithUid)
                .child(getString(R.string.chats))
                .child(chatKey);
        Chat senderNewChat = new Chat(chatWithName, chatWithUid, messageContent, timestamp);
        Chat receiverNewChat = new Chat(author, authorUid, messageContent, timestamp);
        senderChatsRef.setValue(senderNewChat);
        receiverChatsRef.setValue(receiverNewChat);

//        addMessageToList(messageContent, true);
        editText.setText("");
    }

    /**
     * Find the unique id of a user by his/her name.
     * @param userList a list of user names
     * @param chatWithName the name of the user the sending user is chatting with
     * @return an empty string due to unknown usage
     */
    private String findUidByName(ArrayList<User> userList, String chatWithName) {
        for (User user : userList) {
            if (chatWithName.equals(user.getName()))
                return user.getId();
        }
        return "";
    }

    /**
     * Check if the autocomplete text field is valid.
     * @return a boolean value containing the validity of the autocomplete text field
     */
    private boolean autoCompleteTextFieldIsValid() {
        boolean validity = false;
        if (!autoCompleteTextView.getText().toString().trim().equals("") ||
                autoCompleteTextView.getVisibility() == View.INVISIBLE) {
            validity = true;
        }
        return validity;
    }

    /**
     * Add a new message to a list to be displayed.
     * @param message a string containing the message content
     * @param isMyMessage a boolean value of whether this message belongs to the sender
     */
    private void addMessageToList(Message message, Boolean isMyMessage) {
        ChatBubble chatBubble = new ChatBubble(message, isMyMessage);
        chatBubbleList.add(chatBubble);
        adapter.notifyDataSetChanged();
    }

    public void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}