package com.example.healthyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.healthyapp.adapter.UserPostAdapter;
import com.example.healthyapp.models.UserPostModel;

import java.util.ArrayList;

public class TempHomeActivity extends AppCompatActivity {
    RecyclerView rvUserPost;
    ArrayList<UserPostModel> userPostList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);
        // add dummy data
//        userPostList.add(new UserPostModel(R.drawable.backgroundapp, "User 1", "Post 1", R.drawable.backgroundapp));
//        userPostList.add(new UserPostModel(R.drawable.backgroundapp, "User 2", "Post 2", R.drawable.backgroundapp));

        rvUserPost = findViewById(R.id.rvUserPost);

        // set adapter
        UserPostAdapter userPostAdapter = new UserPostAdapter(this, userPostList);
        rvUserPost.setAdapter(userPostAdapter);

        // set layout manager
        rvUserPost.setLayoutManager(new LinearLayoutManager(this));


    }
}