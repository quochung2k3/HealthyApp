package com.example.healthyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.healthyapp.DBConnetion.FirebaseDBConnection;
import com.example.healthyapp.models.FlairModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FlairModel flairModel = new FlairModel();
        flairModel.setName("Test");
        FirebaseDBConnection connection = FirebaseDBConnection.getInstance();
        connection.setData(FirebaseDBConnection.FLAIR, flairModel);
    }
}