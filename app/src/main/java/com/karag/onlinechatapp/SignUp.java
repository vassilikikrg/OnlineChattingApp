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
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karag.onlinechatapp.model.User;

public class SignUp extends AppCompatActivity {
    EditText email,password,nickname;
    Button buttonSignUp;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        buttonSignUp =findViewById(R.id.buttonSignUp1);
        email = findViewById(R.id.editTextEmail1);
        password=findViewById(R.id.editTextTextPassword1);
        nickname=findViewById(R.id.editTextUsername1);
        //Toolbar setup
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Online Chat app");

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
                        goToLogin();
                    }else{
                        showMessage("Error",task.getException().getLocalizedMessage());
                    }
                }
            });
        }else{
            showMessage("Error","Please provide all info");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        MenuItem item = menu.findItem(R.id.logoutItem);
        item.setVisible(false);
        return true;
    }


    private void updateUser(FirebaseUser user,String nickname) {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(nickname)
                .build();
        user.updateProfile(request);
    }
    public void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);
        reference.child("users").child(userId).setValue(user);
    }
    void showMessage(String title,String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
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
    public void goToLogin(){
            Intent intent =new Intent(this,Login.class);
            startActivity(intent);
    }
}