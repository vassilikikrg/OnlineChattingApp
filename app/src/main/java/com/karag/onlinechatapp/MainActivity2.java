package com.karag.onlinechatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity2 extends AppCompatActivity {
    TextView textView,allMessages;
    EditText message;
    String nickname;
    FirebaseDatabase database;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        textView = findViewById(R.id.textViewTitle);
        nickname = getIntent().getStringExtra("nickname");
        textView.setText("Hello "+nickname);
        allMessages=findViewById(R.id.textViewMessages);
        allMessages.setText("");
        message=findViewById(R.id.editTextMessage);
        database= FirebaseDatabase.getInstance();
        reference=database.getReference("message");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String previousMessages = allMessages.getText().toString();
                if (snapshot.getValue()!=null)
                    allMessages.setText(previousMessages+"\n"+snapshot.getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void send(View view){
        if(!message.getText().toString().trim().isEmpty()){
            reference.setValue(nickname+":"+message.getText().toString());
            message.setText("");
        }else {
            showMessage("Error","Please write a message first!..");
        }
    }
    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}