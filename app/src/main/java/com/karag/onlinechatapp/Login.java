package com.karag.onlinechatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {
    EditText email,password;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Toolbar setup
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Online Chat app");

        email = findViewById(R.id.editTextEmail);
        password=findViewById(R.id.editTextTextPassword);
        //Code for Authentication
        auth=FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(user!=null){
            //is signed in
            goToChat();
        }
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

    public void  signin(View view){
        if(!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
            auth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        showMessage("Success","User signed in successfully!");
                        Intent intent=new Intent(getApplicationContext(),NewMessage.class);
                        startActivity(intent);
                        finish();
                    }else{
                        showMessage("Error",task.getException().getLocalizedMessage());
                    }
                }
            });
        }
    }
    void showMessage(String title,String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
    public void goToSignUp(View view){
        Intent intent=new Intent(getApplicationContext(),SignUp.class);
        startActivity(intent);
        finish();
    }
    public void  goToChat(){
        if (user!=null){
            Intent intent =new Intent(this,NewMessage.class);
            intent.putExtra("sender_username",user.getDisplayName());
            intent.putExtra("sender_id",user.getUid());
            startActivity(intent);
            finish();
        }else{
            showMessage("Error","Please sign-in or create an account first!");
        }
    }
}