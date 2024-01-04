package com.karag.onlinechatapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karag.onlinechatapp.model.User;

public class MainActivity extends AppCompatActivity {
    EditText email,password,nickname;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = findViewById(R.id.editTextEmail);
        password=findViewById(R.id.editTextTextPassword);
        nickname=findViewById(R.id.editTextNickname);
        //Code for Authentication
        auth=FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(user!=null){
            Button b =findViewById(R.id.button);
            b.setVisibility(View.GONE);
        }
        //Code for realtime database
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }
    public void  signup(View view){
        if(!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && !nickname.getText().toString().isEmpty()){
            auth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        user = auth.getCurrentUser();
                        updateUser(user,nickname.getText().toString());

                        String uid = user.getUid();
                        writeNewUser(uid,nickname.getText().toString(),email.getText().toString());
                        showMessage("Success","User profile created!");
                    }else{
                        showMessage("Error",task.getException().getLocalizedMessage());
                    }
                }
            });
        }else{
            showMessage("Error","Please provide all info");
        }
    }
    private void updateUser(FirebaseUser user,String nickname) {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(nickname)
                .build();
        user.updateProfile(request);
    }
    public void  signin(View view){
        if(!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
            auth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        showMessage("Success","User signed in successfully!");
                    }else{
                        showMessage("Error",task.getException().getLocalizedMessage());
                    }
                }
            });
        }
    }
    public void signout(View view){

        auth.signOut();
    }
    public void  chat(View view){
        if (user!=null){
            Intent intent =new Intent(this,NewMessage.class);
            intent.putExtra("sender_username",user.getDisplayName());
            intent.putExtra("sender_id",user.getUid());

            startActivity(intent);
        }else{
            showMessage("Error","Please sign-in or create an account first!");
        }
    }
    void showMessage(String title,String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
    public void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);
        reference.child("users").child(userId).setValue(user);
    }
}