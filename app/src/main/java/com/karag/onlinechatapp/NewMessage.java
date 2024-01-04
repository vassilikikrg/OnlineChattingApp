package com.karag.onlinechatapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    FirebaseDatabase database;
    DatabaseReference reference;
    String sender_id,sender_username, receiver_id,receiver_username,chat_id;
    FirebaseAuth auth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        receiverUsername=findViewById(R.id.editTextReceiverUsername);

        //Toolbar setup
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Online Chat app");

        //Code for Authentication
        auth=FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        sender_id = user.getUid();
        sender_username=user.getDisplayName();
        //Code for realtime database
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        MenuItem item = menu.findItem(R.id.logoutItem);
        if(user!=null) {
            item.setVisible(true);
        }else{
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //signout process
        //hide signout button
        if (user!=null) {
            auth.signOut();
            item.setVisible(false);
            Intent intent=new Intent(getApplicationContext(),Login.class);
            startActivity(intent);
        }
        return true;
    }
    public void gotoconvo(View view){
        String r_username=receiverUsername.getText().toString().trim();
        if(!r_username.isEmpty()){
            //search for an existing user with the given username
           reference.child("users").orderByChild("username").equalTo(r_username)
                   .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //username exists in the database,get the uid
                        receiver_id=dataSnapshot.getChildren().iterator().next().getKey();
                        if(receiver_id.equals(sender_id)){
                            //can't send a message to yourself
                            Toast.makeText(getApplicationContext(), "The user you are trying to message is yourself!..", Toast.LENGTH_LONG).show();
                            return;
                        }
                        receiver_username=r_username;//for later use
                        //check if a chat already exists,else create a new one
                        checkAndCreateChat(sender_id,receiver_id);
                    } else {
                        //username doesn't exist in the database
                        Toast.makeText(getApplicationContext(), "The user doesn't exist.", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "An error occured, please try again.", Toast.LENGTH_LONG).show();
                }
            });

            }
        else{
            Toast.makeText(getApplicationContext(), "Please enter a username.", Toast.LENGTH_LONG).show();
        }
    }

    private void checkAndCreateChat(String uid1,String uid2) {
        //check if a chat already exists between the current user and the specified user
        reference.child("chats").orderByChild("users/" + uid1).equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean chatExists = false;

                        for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                            if (chatSnapshot.child("users/" + uid2).exists()) {
                                chat_id=chatSnapshot.getKey();
                                //chat already exists
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
                        Toast.makeText(getApplicationContext(), "An error occured, please try again.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void createChat(String uid1,String uid2) {
        //create a new chat with the current user and the specified user
        DatabaseReference newChatRef = reference.child("chats").push();
        newChatRef.child("users").child(uid1).setValue(true);
        newChatRef.child("users").child(uid2).setValue(true);
        //set the chat ID for further use
        chat_id = newChatRef.getKey();
    }
    private void redirectToChatView(){
        Intent intent =new Intent(getApplicationContext(),ChatView.class);
        intent.putExtra("sender_id",sender_id);
        intent.putExtra("receiver_id",receiver_id);
        intent.putExtra("sender_username",sender_username);
        intent.putExtra("receiver_username",receiver_username);
        intent.putExtra("chat_id",chat_id);
        startActivity(intent);
    }
}