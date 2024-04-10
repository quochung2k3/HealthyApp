package com.example.healthyapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.healthyapp.DBConnetion.FirebaseDBConnection;
import com.example.healthyapp.R;
import com.example.healthyapp.adapter.UserPostAdapter;
import com.example.healthyapp.models.PostModel;
import com.example.healthyapp.models.UserModel;
import com.example.healthyapp.models.UserPostModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    RecyclerView rvUserPost;
    ArrayList<UserPostModel> userPostList = new ArrayList<>();
    FirebaseDBConnection db = FirebaseDBConnection.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getPosts();
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        rvUserPost = rootView.findViewById(R.id.rvUserPost);

        // set adapter
        UserPostAdapter userPostAdapter = new UserPostAdapter(getActivity(), userPostList);
        rvUserPost.setAdapter(userPostAdapter);

        // set layout manager
        rvUserPost.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;

    }

    private void getPosts() {
        db.readData(FirebaseDBConnection.POST, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userPostList.clear();
                List<PostModel> postList = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    PostModel post = postSnapshot.getValue(PostModel.class);
                    post.setId(postSnapshot.getKey());
                    postList.add(post);
                }

                // convert post to user post
                for (PostModel post : postList) {
                    UserPostModel userPost = postToUserPost(post);
                    userPostList.add(userPost);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private UserPostModel postToUserPost(PostModel post) {
        UserPostModel userPost = new UserPostModel();
        userPost.setPostTitle(post.getTitle());
        userPost.setLikes(post.getLikes());
        // get user name
        db.readData(FirebaseDBConnection.USER, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    UserModel user = userSnapshot.getValue(UserModel.class);
                    if (user.getId().equals(post.getUser_id())) {
                        userPost.setUserName(user.getFirst_name() + " " + user.getLast_name());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return userPost;
    }
}