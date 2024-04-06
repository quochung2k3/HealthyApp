package com.example.healthyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.healthyapp.DBConnetion.FirebaseDBConnection;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo kết nối với Firebase Database
        FirebaseDBConnection connection = FirebaseDBConnection.getInstance();
        connection.setData(FirebaseDBConnection.MESSAGE, "Hello, Firebase, again!");

    }
}