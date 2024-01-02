package com.karag.onlinechatapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karag.onlinechatapp.model.Chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewMessage extends AppCompatActivity {
    EditText receiverUsername;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference,chat_reference;
    String sender_id, receiver_id,chat_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        receiverUsername=findViewById(R.id.editTextReceiverUsername);
        sender_id = getIntent().getStringExtra("sender_id");

        //Code for Authentication
        auth=FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        //Code for realtime database
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }
    public void gotoconvo(View view){
        String r_username=receiverUsername.getText().toString();
        if(!r_username.trim().isEmpty()){
           reference.child("users").orderByChild("username").equalTo(r_username)
                   .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Username exists in the database
                        receiver_id=dataSnapshot.getChildren().iterator().next().getKey();
                        // Check if a chat already exists
                        checkAndCreateChat(sender_id,receiver_id);
                    } else {
                        // Username doesn't exist in the database
                        Toast.makeText(getApplicationContext(), "The user doesn't exist", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error
                }
            });

            }
        else{
            Toast.makeText(getApplicationContext(), "Please enter the user to whom you want to send a message!", Toast.LENGTH_LONG).show();
        }
    }

    private void checkAndCreateChat(String uid1,String uid2) {
        // Check if a chat already exists with the current user and the specified user
        reference.child("chats").orderByChild("users/" + uid1).equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean chatExists = false;

                        for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                            if (chatSnapshot.child("users/" + uid2).exists()) {
                                chat_id=chatSnapshot.getKey();
                                // Chat already exists
                                chatExists = true;
                                redirectToChatView();
                                break;
                            }
                        }

                        if (!chatExists) {
                            createChat(uid1,uid2);
                            redirectToChatView();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void createChat(String uid1,String uid2) {
        // Create a new chat with the current user and the specified user
        DatabaseReference newChatRef = reference.child("chats").push();
        newChatRef.child("users").child(uid1).setValue(true);
        newChatRef.child("users").child(uid2).setValue(true);

        // Get the chat ID for further use
       chat_id = newChatRef.getKey();
    }
    private void redirectToChatView(){
        Intent intent =new Intent(getApplicationContext(),ChatView.class);
        intent.putExtra("sender_id",sender_id);
        intent.putExtra("receiver_id",receiver_id);
        intent.putExtra("chat_id",chat_id);
        startActivity(intent);
    }
}