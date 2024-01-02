package com.karag.onlinechatapp.Data;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karag.onlinechatapp.model.User;

public class DatabaseOperations {
    FirebaseDatabase database;
    DatabaseReference reference;

    public DatabaseOperations(FirebaseDatabase database, DatabaseReference reference) {
        this.database = database;
        this.reference = reference;
    }

    public void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);
        reference.child("users").child(userId).setValue(user);
    }
    public void readUser(){

    }
    public void createNewConvo(String uid1,String uid2){

    }
}
