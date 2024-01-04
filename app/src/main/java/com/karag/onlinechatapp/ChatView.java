package com.karag.onlinechatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karag.onlinechatapp.model.ChatMessage;

public class ChatView extends AppCompatActivity {

    TextView textView;
    EditText message;
    LinearLayout allMessages;
    ScrollView scrollView;
    String sender_id,sender_username, receiver_id,receiver_username,chat_id;
    FirebaseDatabase database;
    DatabaseReference messagesReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view);

        //find ui components
        textView = findViewById(R.id.textViewTitle);
        message=findViewById(R.id.editTextMessage);
        allMessages=findViewById(R.id.linearChat);
        scrollView=findViewById(R.id.scrollView2);
        //get the strings
        sender_id = getIntent().getStringExtra("sender_id");
        receiver_id = getIntent().getStringExtra("receiver_id");
        sender_username = getIntent().getStringExtra("sender_username");
        receiver_username = getIntent().getStringExtra("receiver_username");
        chat_id = getIntent().getStringExtra("chat_id");
        textView.setText(receiver_username);

        //Toolbar setup
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Online Chat app");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//back button displayed in order to return to parent activity
        //initialize connection with db
        database= FirebaseDatabase.getInstance();
        messagesReference=database.getReference("messages");
        // Clear existing messages
        allMessages.removeAllViews();
        showChatMessages(chat_id);
        }

    public void send(View view){
        if(!message.getText().toString().trim().isEmpty()){
            messagesReference.push().setValue(new ChatMessage(chat_id,sender_id,message.getText().toString().trim()));
            message.setText(""); //clean input field
        }else {
            Toast.makeText(getApplicationContext(),"Please write a message first!..",Toast.LENGTH_SHORT).show();
        }
    }
    public void showChatMessages(String chatId) {
        messagesReference.orderByChild("chat_id").equalTo(chatId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String messageSenderId = snapshot.child("sender_id").getValue(String.class);
                String messageText = snapshot.child("text").getValue(String.class);

                // Find current message sender's username
                // and determine the layout to use based on the sender
                String messageSenderUsername;
                int layoutResId;
                if (messageSenderId.equals(sender_id)) {
                    messageSenderUsername = sender_username;
                    layoutResId = R.layout.message_sent;
                } else {
                    messageSenderUsername = receiver_username;
                    layoutResId = R.layout.message_received;
                }

                // Inflate the chat bubble layout
                View chatBubbleView = getLayoutInflater().inflate(layoutResId, null);

                // Set the message text
                TextView messageTextView = chatBubbleView.findViewById(R.id.textMessageSent);
                if (messageTextView == null) {
                    messageTextView = chatBubbleView.findViewById(R.id.textMessageReceived);
                }
                messageTextView.setText(messageSenderUsername + ": " + messageText);

                // Add the chat bubble to the layout
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                // Set layout gravity for message_sent to "end"
                if (messageSenderId.equals(sender_id)) {
                    params.gravity = Gravity.END;
                }
                params.setMargins(16, 16, 16, 16); // Add margins as needed
                chatBubbleView.setLayoutParams(params);

                // Add the chat bubble to the layout
                allMessages.addView(chatBubbleView);
                //set focus to bottom
                scrollView.post(
                        () -> scrollView.fullScroll(View.FOCUS_DOWN)
                );
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error: " + error.getMessage());
            }
        });

    }
}