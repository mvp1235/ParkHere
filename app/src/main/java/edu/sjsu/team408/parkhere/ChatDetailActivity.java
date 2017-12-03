package edu.sjsu.team408.parkhere;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
    private View sendButton;
    private EditText editText;
    private TextView chatWithLabel;
    private AutoCompleteTextView toAutoCompleteTextView;
    private ArrayList<User> userList;

    boolean myMessage = true;
    private List<ChatBubble> chatBubbleList;
    private ArrayAdapter<ChatBubble> adapter;

    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private String chatKey;
    String chatKeyString;

    private DatabaseReference membersOfChatsReference;
    private DatabaseReference chatsReference;
    private DatabaseReference messagesReference;
    private DatabaseReference userChatsReference;
    private DatabaseReference userListReference;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        chatBubbleList = new ArrayList<>();
        userList = new ArrayList<>();

        listView = findViewById(R.id.chat_listView);
        sendButton = findViewById(R.id.send_message_button);
        editText = findViewById(R.id.input_message_editText);
        chatWithLabel = findViewById(R.id.friendLabel);
        toAutoCompleteTextView = findViewById(R.id.toAutoCompleteTextView);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Get chat key from intent
        chatKey = getIntent().getStringExtra(CURRENT_CHAT_KEY);
        final String[] foundChatWithName = new String[1];
        foundChatWithName[0] = "";
        if (chatKey == null) {
            // make a new chat
            chatKey = mDatabase.child(getString(R.string.chats)).push().getKey();
            setupChildReferences();
        } else {
            setupChildReferences();
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
                            sendButton.setEnabled(true);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    }
            );
        }

        final String author = user.getEmail().split("@")[0];
        setupChildReferences();

        //set ListView adapter first
        adapter = new MessageAdapter(this, R.layout.right_chat_bubble, chatBubbleList);
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
        toAutoCompleteTextView.setAdapter(autoComplete);

        toAutoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (toAutoCompleteTextFieldIsValid()) {
                        sendButton.setEnabled(true);
                    } else {
                        sendButton.setEnabled(false);
                    }
                }
            }
        });

        //event for button SEND
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().trim().equals("")) {
                    makeToast("Please enter some text...");
                } else {
                    if (toAutoCompleteTextFieldIsValid()) {
                        String chatWithName;
                        String chatWithUid;
                        if (foundChatWithName[0].equals("")) {
                            chatWithName = toAutoCompleteTextView.getText().toString();
                        } else {
                            chatWithName = foundChatWithName[0];
                        }
                        chatWithUid = findUidByName(userList, chatWithName);
                        if (chatWithUid.equals("")) {
                            makeToast("The user you want to message to cannot be found." +
                                    "Please enter another user name");
                        } else {
                            makeNewChat(author, user.getUid(), chatWithName, chatWithUid);
                            hideAutoCompleteTextViewAndShowChatWithNameLabel(chatWithName);
                            //add message to list
                            String messageContent = editText.getText().toString();
                            writeNewMessage(author, user.getUid(), chatWithName, chatWithUid
                                    , messageContent);
                        }
                    }
                }
            }
        });

        messagesReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message newMessage = dataSnapshot.getValue(Message.class);
                if (newMessage.getAuthorId().equals(user.getUid())) {
                    addMessageToList(newMessage.getContent(), true);
                } else {
                    addMessageToList(newMessage.getContent(), false);
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

    private void hideAutoCompleteTextViewAndShowChatWithNameLabel(String chatWithName) {
        if (toAutoCompleteTextView.getVisibility() == View.VISIBLE) {
            toAutoCompleteTextView.setEnabled(false);
            toAutoCompleteTextView.setVisibility(View.INVISIBLE);
        }
        chatWithLabel.setVisibility(View.VISIBLE);
        chatWithLabel.setText(chatWithName);
    }

    private boolean chatBetweenDoesNotExist(final String chatWithUser) {
        final boolean[] chatIsFound = {false};
        userListReference
                .child(user.getUid())
                .child(getString(R.string.currentlyChatWiths))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(chatWithUser)) {
                            chatIsFound[0] = true;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        return chatIsFound[0];
    }

    // must be called after chatKey has been initialized.
    private void setupChildReferences() {
        userListReference = mDatabase.child(getString(R.string.Users));
        userChatsReference = userListReference
                .child(user.getUid())
                .child(getString(R.string.chatsOfUsersDatabaseField));
        membersOfChatsReference = mDatabase
                .child(getString(R.string.membersOfChats))
                .child(chatKey);
        chatsReference = mDatabase
                .child("chats")
                .child(chatKey);
        messagesReference = mDatabase.child("messages").child(chatKey);
    }

    private void makeNewChat(String author, String authorUid, String chatWithName, String chatWithUid) {
        if (chatBetweenDoesNotExist(chatWithName)) {
            String Users_field = "/" + getString(R.string.Users) + "/";
            String Users_uid_chats_field = "/" + getString(R.string.chats) + "/";
            String Users_currentlyChatWiths_field = "/" + getString(R.string.currentlyChatWiths) + "/";
            Date timestamp = null;
            String lastMessage = "";
            Chat authorChat = new Chat(chatWithName, chatWithUid, lastMessage, timestamp);
            Chat receiverChat = new Chat(author, authorUid, lastMessage, timestamp);

//        chatsReference.setValue(newChat);

            Map<String, Object> childUpdates = new HashMap<>();
            // push to author
            childUpdates.put(Users_field + user.getUid() + Users_uid_chats_field + chatKeyString,
                    authorChat);
            childUpdates.put(Users_field + user.getUid() + Users_currentlyChatWiths_field,
                    chatWithName);
            membersOfChatsReference.child(author).setValue(true);

            // push to receiver
            childUpdates.put(Users_field + chatWithUid + Users_uid_chats_field + chatKeyString,
                    receiverChat);
            childUpdates.put(Users_field + chatWithUid + Users_currentlyChatWiths_field,
                    author);
            membersOfChatsReference.child(chatWithName).setValue(true);

            mDatabase.updateChildren(childUpdates);
        }
    }

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



    private String findUidByName(ArrayList<User> userList, String chatWithName) {
        for (User user : userList) {
            if (chatWithName.equals(user.getName()))
                return user.getId();
        }
        return "";
    }

    private boolean toAutoCompleteTextFieldIsValid() {
        boolean validity = false;
        if (!toAutoCompleteTextView.getText().toString().trim().equals("") ||
                toAutoCompleteTextView.getVisibility() == View.INVISIBLE)
            validity = true;
        return validity;
    }

    private void addMessageToList(String messageContent, Boolean isMyMessage) {
        ChatBubble chatBubble = new ChatBubble(messageContent, isMyMessage);
        if (chatBubbleList.isEmpty() ||
                !chatBubbleList.get(chatBubbleList.size() - 1).equals(chatBubble)) {
            chatBubbleList.add(chatBubble);
            adapter.notifyDataSetChanged();
        }
    }
    private void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}

