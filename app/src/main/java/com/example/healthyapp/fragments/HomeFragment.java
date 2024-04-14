package com.example.healthyapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.healthyapp.DBConnetion.FirebaseDBConnection;
import com.example.healthyapp.R;
import com.example.healthyapp.adapter.UserPostAdapter;
import com.example.healthyapp.models.PostModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    RecyclerView rvUserPost;
    ArrayList<PostModel> postList = new ArrayList<>();
    UserPostAdapter userPostAdapter = null;
    FirebaseDBConnection db = FirebaseDBConnection.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getPosts();
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        rvUserPost = rootView.findViewById(R.id.rvUserPost);

        // set adapter
        userPostAdapter = new UserPostAdapter(getActivity(), postList);
        rvUserPost.setAdapter(userPostAdapter);

        // set layout manager
        rvUserPost.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;

    }

    private void getPosts() {
        db.readData(FirebaseDBConnection.POST, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    PostModel post = postSnapshot.getValue(PostModel.class);
                    post.setId(postSnapshot.getKey());
                    postList.add(post);
                    Log.d("Post", post.getTitle());
                }
                userPostAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}