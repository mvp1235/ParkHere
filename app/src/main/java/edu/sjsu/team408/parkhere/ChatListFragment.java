package edu.sjsu.team408.parkhere;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by robg on 11/21/17.
 */

public class ChatListFragment extends Fragment {
    private Button newMessageButton;
    private RecyclerView chatRecyclerView;
//    private RecyclerView.Adapter chatAdapter;
//    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseRecyclerAdapter<Chat, ChatAdapter.ViewHolder> mAdapter;
    private LinearLayoutManager mLayoutManager;

    private List<Chat> chatList;

    private DatabaseReference mDatabase;
//    private FirebaseDatabase mDatabase;
    private String thisUser;
    private DatabaseReference chatOfThisUserDatabaseRef;
    private DatabaseReference chatsDatabaseRef;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_messaging_list, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        thisUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        chatRecyclerView = view.findViewById(R.id.chat_recyclerView);
        chatRecyclerView.setHasFixedSize(true);

        newMessageButton = view.findViewById(R.id.new_message_button);
        newMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Fragment fragment = new ChatFragment();
//                FragmentManager fragmentManager =
//                        getActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction =
//                        fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.content, fragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
                Intent intent = new Intent(getContext(), ChatDetailActivity.class);
                startActivity(intent);

            }
        });

//        observeDatabaseChangesAndUpdate();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(mLayoutManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query chatsQuery = getQuery(mDatabase);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Chat>()
                .setQuery(chatsQuery, Chat.class)
                .build();
        mAdapter = new FirebaseRecyclerAdapter<Chat, ChatAdapter.ViewHolder>(options) {
            @Override
            public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                return new ChatAdapter.ViewHolder(inflater.inflate(R.layout.chat_item_layout, parent, false));
            }

            @Override
            protected void onBindViewHolder(ChatAdapter.ViewHolder holder, int position, Chat model) {
                final DatabaseReference chatRef = getRef(position);

                // Set click listener for the whole chat view
                final String chatKey = chatRef.getKey();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch ChatDetailActivity
                        Intent intent = new Intent(getActivity(), ChatDetailActivity.class);
                        intent.putExtra(ChatDetailActivity.CURRENT_CHAT_KEY, chatKey);
                        startActivity(intent);
                    }
                });

                holder.bindToPost(model);
            }
        };
        chatRecyclerView.setAdapter(mAdapter);
    }

    private Query getQuery(DatabaseReference databaseReference) {

        Query chatsQuery = databaseReference
                .child("Users")
                .child(getUid())
                .child("chats")
                .limitToFirst(100);



        return chatsQuery;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    public String getUid() { return FirebaseAuth.getInstance().getCurrentUser().getUid(); }

    private void observeDatabaseChangesAndUpdate() {
        chatOfThisUserDatabaseRef.addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        getTheNewlyAddedChatAndNotifyRecyclerView(dataSnapshot);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        getTheNewlyAddedChatAndNotifyRecyclerView(dataSnapshot);
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
                }
        );
    }

    private void getTheNewlyAddedChatAndNotifyRecyclerView(
            DataSnapshot dataSnapshot
    ) {
        if (dataSnapshot != null && dataSnapshot.getValue() != null) {
            String chatId = dataSnapshot.getValue(String.class);
            chatsDatabaseRef
                    .child(chatId)
                    .addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot != null
                                            && dataSnapshot.getValue() != null) {
                                        Chat chat = dataSnapshot.getValue(Chat.class);
                                        chatList.add(chat);
                                        chatRecyclerView.scrollToPosition(chatList.size() - 1);
                                        mAdapter.notifyItemInserted(chatList.size() - 1);

                                    } else {
                                        makeToast("dataSnapshot to get Chat object is null");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            }
                    );
        } else {
            makeToast("dataSnapshot to get chatID is null");
        }
    }

    private void makeToast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
    }

}
