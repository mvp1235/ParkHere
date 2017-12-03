//package edu.sjsu.team408.parkhere;
//
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.ChildEventListener;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by robg on 11/17/17.
// */
//
//public class ChatFragment extends Fragment {
//
//    private ListView listView;
//    private View sendButton;
//    private EditText editText;
//    private TextView friendLabel;
//    private AutoCompleteTextView toAutoCompleteTextView;
//    private ArrayList<User> userList;
//
//    boolean myMessage = true;
//    private List<ChatBubble> chatBubbleList;
//    private ArrayAdapter<ChatBubble> adapter;
//
//    private DatabaseReference mDatabase;
//    private FirebaseUser user;
//    private String chatKey;
//    String chatKeyString;
//
//    private DatabaseReference membersOfChatsReference;
//    private DatabaseReference chatsReference;
//    private DatabaseReference  messagesReference;
//    private DatabaseReference userChatsReference;
//    private DatabaseReference userListReference;
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.activity_messaging, container, false);
//
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        user = FirebaseAuth.getInstance().getCurrentUser();
//
//        final String author = user.getEmail().split("@")[0];
//        chatKey = mDatabase.child(getString(R.string.chatsDatabaseField)).push().getKey();
//        setupChildReference(author);
//
//        chatBubbleList = new ArrayList<>();
//        userList = new ArrayList<>();
//
//        listView = view.findViewById(R.id.chat_listView);
//        sendButton = view.findViewById(R.id.send_message_button);
//        editText = view.findViewById(R.id.input_message_editText);
//        friendLabel = view.findViewById(R.id.friendLabel);
//        toAutoCompleteTextView = view.findViewById(R.id.toAutoCompleteTextView);
//
//        //set ListView adapter first
//        adapter = new MessageAdapter(getActivity(), R.layout.right_chat_bubble, chatBubbleList);
//        listView.setAdapter(adapter);
//
//        // setting up the autocomplete text field
//        final ArrayAdapter<String> autoComplete =
//                new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
//        userListReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                userList.clear();
//                autoComplete.clear();
//                for (DataSnapshot user : dataSnapshot.getChildren()) {
//                    autoComplete.add(user.child("name").getValue(String.class));
//                    userList.add(user.getValue(User.class));
////                autocompleteAdapter.notifyDataSetChanged();
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        toAutoCompleteTextView.setAdapter(autoComplete);
//
//        toAutoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) {
//                    if (toAutoCompleteTextFieldIsValid()) {
//                        sendButton.setEnabled(true);
//                    } else {
//                        sendButton.setEnabled(false);
//                    }
//                }
//            }
//        });
//
//        //event for button SEND
//        sendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (editText.getText().toString().trim().equals("")) {
//                    Toast.makeText(getActivity(),
//                            "Please enter some text...",
//                            Toast.LENGTH_SHORT).show();
//                } else {
//                    if (toAutoCompleteTextFieldIsValid()) {
//                        String chatWithName = toAutoCompleteTextView.getText().toString();
//                        String chatWithUid = findUidByName(userList, chatWithName);
//
//                        chatsReference.setValue(new Chat("", new Date()));
//                        Map<String, Object> childUpdates = new HashMap<>();
//                        // push to sender
//                        userChatsReference.push().setValue(chatKey);
////                        childUpdates.put("/Users/"+ user.getUid() + "/chats/", chatKeyString);
////                        membersOfChatsReference.setValue(true);
//                        // push to receiver
//                        userListReference.child(chatWithUid)
//                                .child(getString(R.string.chatsOfUsersDatabaseField))
//                                .push()
//                                .setValue(chatKey);
////                        childUpdates.put("/Users/"+ chatWithUid + "/chats/", chatKeyString);
////                        mDatabase.child("membersOfChats")
////                                .child(chatKeyString)
////                                .child(chatWithName)
////                                .setValue(true);
////                        mDatabase.updateChildren(childUpdates);
//
//                        if (toAutoCompleteTextView.getVisibility() == View.VISIBLE) {
//                            toAutoCompleteTextView.setEnabled(false);
//                            toAutoCompleteTextView.setVisibility(View.INVISIBLE);
//                        }
//                        friendLabel.setVisibility(View.VISIBLE);
//                        friendLabel.setText(chatWithName);
//                        //add message to list
//                        String messageContent = editText.getText().toString();
//                        writeNewMessageToFirebase(
//                                user.getUid(), author, messageContent, chatWithName);
//                        addMessageToList(messageContent, true);
//                        editText.setText("");
////                    getMyMessage = !getMyMessage;
//                    }
//                }
//            }
//        });
//
//        messagesReference.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Message newMessage = dataSnapshot.getValue(Message.class);
//                if (!newMessage.getAuthorId().equals(user.getUid()))
//                    addMessageToList(newMessage.getContent(), false);
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//
//
//        });
//        return view;
//    }
//
//    private String findUidByName(ArrayList<User> userList, String chatWithName) {
//        for (User user : userList) {
//            if (chatWithName.equals(user.getName()))
//                return user.getId();
//        }
//        return "";
//    }
//
//    private boolean toAutoCompleteTextFieldIsValid() {
//        boolean validity = false;
//        if (!toAutoCompleteTextView.getText().toString().trim().equals(""))
//            validity = true;
//        return validity;
//    }
//
//    private void writeNewMessageToFirebase(String userId,
//                                           String author,
//                                           String messageContent,
//                                           String receiver) {
//        Date timestamp = new Date();
//        String messageKey = messagesReference.push().getKey();
//        String messageKeyString = "msg " + messageKey;
//
//        chatsReference.child("lastMessage").setValue(messageContent);
//        chatsReference.child("timestamp").setValue(timestamp);
//
////        messagesReference.child(messageKeyString).setValue(
////                new Message(userId,
////                        author,
////                        messageContent,
////                        timestamp)
////        );
//        messagesReference.push().setValue(
//                new Message(userId, author, messageContent, timestamp));
//    }
//    private void setupChildReference(String author) {
//        chatKeyString = chatKey;
//
//        userListReference = mDatabase.child("Users");
//
//        userChatsReference = userListReference
//                .child(user.getUid())
//                .child(getString(R.string.chatsOfUsersDatabaseField));
//
//        membersOfChatsReference =
//                mDatabase.child(getString(R.string.membersOfChats))
//                        .child(chatKeyString)
//                        .child(author);
//        chatsReference = mDatabase.child("chats").child(chatKeyString);
//        messagesReference =
//                mDatabase.child("messages")
//                        .child(chatKeyString);
//    }
//
//    private void addMessageToList(String messageContent, Boolean isMyMessage) {
//        ChatBubble chatBubble = new ChatBubble(messageContent, isMyMessage);
//        chatBubbleList.add(chatBubble);
//        adapter.notifyDataSetChanged();
//    }
//    private void makeToast(String text) {
//        Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
//    }
//}
//
