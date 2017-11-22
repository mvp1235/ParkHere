//package edu.sjsu.team408.parkhere;
//
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by robg on 11/17/17.
// */
//
//public class ChatActivity extends AppCompatActivity {
//
//    private ListView listView;
//    private View btnSend;
//    private EditText editText;
//    boolean getMyMessage = true;
//    private List<ChatBubble> ChatBubbles;
//    private ArrayAdapter<ChatBubble> adapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        ChatBubbles = new ArrayList<>();
//
//        listView = (ListView) findViewById(R.id.list_msg);
//        btnSend = findViewById(R.id.btn_chat_send);
//        editText = (EditText) findViewById(R.id.msg_type);
//
//        //set ListView adapter first
//        adapter = new MessageAdapter(this, R.layout.left_chat_bubble, ChatBubbles);
//        listView.setAdapter(adapter);
//
//        //event for button SEND
//        btnSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (editText.getText().toString().trim().equals("")) {
//                    Toast.makeText(ChatActivity.this,
//                            "Please input some text...",
//                            Toast.LENGTH_SHORT).show();
//                } else {
//                    //add message to list
//                    ChatBubble ChatBubble = new ChatBubble(
//                            editText.getText().toString(), getMyMessage);
//                    ChatBubbles.add(ChatBubble);
//                    adapter.notifyDataSetChanged();
//                    editText.setText("");
//                    if (getMyMessage) {
//                        getMyMessage = false;
//                    } else {
//                        getMyMessage = true;
//                    }
//                }
//            }
//        });
//    }
//}