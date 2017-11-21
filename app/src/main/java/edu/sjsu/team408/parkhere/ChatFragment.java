package edu.sjsu.team408.parkhere;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by robg on 11/17/17.
 */

public class ChatFragment extends Fragment {

    private ListView listView;
    private View btnSend;
    private EditText editText;
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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_messaging, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        final String author = user.getEmail().split("@")[0];
        chatKey = mDatabase.child("chats").push().getKey();
        setupChildReference(author);

        chatBubbleList = new ArrayList<>();

        listView = (ListView) view.findViewById(R.id.list_msg);
        btnSend = view.findViewById(R.id.btn_chat_send);
        editText = (EditText) view.findViewById(R.id.msg_type);

        //set ListView adapter first
        adapter = new MessageAdapter(getActivity(), R.layout.right_chat_bubble, chatBubbleList);
        listView.setAdapter(adapter);

        //event for button SEND
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity(),
                            "Please input some text...",
                            Toast.LENGTH_SHORT).show();
                } else {
                    //add message to list
                    String messageContent = editText.getText().toString();
                    writeNewMessageToFirebase(
                            user.getUid(),
                            author,
                            messageContent);
                    addMessageToList(messageContent, true);
                    editText.setText("");
//                    myMessage = !myMessage;
                }
            }
        });

        messagesReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message newMessage = dataSnapshot.getValue(Message.class);
                if (newMessage.getUserId() != user.getUid())
                    addMessageToList(newMessage.getContent(), false);
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
        return view;
    }

    private void writeNewMessageToFirebase(String userId,
                                           String author,
                                           String messageContent) {
        Date timestamp = new Date();
        String messageKey =
                mDatabase.child("messages")
                        .child(chatKeyString)
                        .push()
                        .getKey();
        String messageKeyString = "msg " + messageKey;
        membersOfChatsReference.setValue(true);
        chatsReference.setValue(new Chat(messageContent, timestamp));
        messagesReference
                .child(messageKeyString)
                .setValue(
                        new Message(userId,
                                author,
                                messageContent,
                                timestamp));

    }
    private void setupChildReference(String author) {
        chatKeyString = "chat " + chatKey;

        membersOfChatsReference =
                mDatabase.child("membersOfChats")
                        .child(chatKeyString)
                        .child(author);
        chatsReference = mDatabase.child("chats").child(chatKeyString);
        messagesReference =
                mDatabase.child("messages")
                        .child(chatKeyString);
    }

    private void addMessageToList(String messageContent, Boolean isMyMessage) {
        ChatBubble chatBubble = new ChatBubble(
                messageContent, isMyMessage);
        chatBubbleList.add(chatBubble);
        adapter.notifyDataSetChanged();
    }
    private void makeToast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
    }
}

