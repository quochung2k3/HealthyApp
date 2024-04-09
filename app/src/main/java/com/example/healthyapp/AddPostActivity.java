package com.example.healthyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;

public class AddPostActivity extends AppCompatActivity {
    ImageButton ibBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        ibBack = findViewById(R.id.ibBack);
        ibBack.setOnClickListener(v -> {
            finish();
        });
    }
}