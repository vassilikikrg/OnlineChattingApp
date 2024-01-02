package com.karag.onlinechatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karag.onlinechatapp.model.ChatMessage;

import java.time.LocalDateTime;
import java.util.Date;

public class ChatView extends AppCompatActivity {

    TextView textView,allMessages;
    EditText message;
    String sender_id,receiver_id,chat_id;
    FirebaseDatabase database;
    DatabaseReference messagesReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        textView = findViewById(R.id.textViewTitle);
        message=findViewById(R.id.editTextMessage);
        allMessages=findViewById(R.id.textViewMessages);

        sender_id = getIntent().getStringExtra("sender_id");
        receiver_id = getIntent().getStringExtra("receiver_id");
        chat_id = getIntent().getStringExtra("chat_id");

        Log.i("chat",chat_id);
        allMessages.setText(""); //clean previous messages

        database= FirebaseDatabase.getInstance();
        messagesReference=database.getReference("messages");
        readMessages(chat_id);
    }
    public void send(View view){
        if(!message.getText().toString().trim().isEmpty()){

            messagesReference.push().setValue(new ChatMessage(chat_id,sender_id,message.getText().toString()));
            message.setText("");
        }else {
            showMessage("Error","Please write a message first!..");
        }
    }
    public void readMessages(String chatId) {
        Query query = messagesReference.orderByChild("chat_id").equalTo(chatId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StringBuilder allMessageBuilder = new StringBuilder();

                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    // Retrieve individual messages
                    String senderId = messageSnapshot.child("sender_id").getValue(String.class);
                    String messageText = messageSnapshot.child("text").getValue(String.class);

                    // Fetch sender's username
                    DatabaseReference usersRef = database.getReference("users").child(senderId).child("username");
                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            String senderUsername = userSnapshot.getValue(String.class);
                            allMessageBuilder.append(senderUsername + ": " + messageText + "\n");
                            allMessages.setText(allMessageBuilder.toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle the error
                            Log.e("FirebaseError", "Error: " + databaseError.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                Log.e("FirebaseError", "Error: " + databaseError.getMessage());
            }
        });
    }


    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}