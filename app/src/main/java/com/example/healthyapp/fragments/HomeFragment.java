package com.example.healthyapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.healthyapp.R;
import com.example.healthyapp.adapter.UserPostAdapter;
import com.example.healthyapp.models.UserPostModel;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    RecyclerView rvUserPost;
    ArrayList<UserPostModel> userPostList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // add dummy data
        userPostList.add(new UserPostModel(R.drawable.backgroundapp, "User 1", "Post 1", R.drawable.backgroundapp));
        userPostList.add(new UserPostModel(R.drawable.backgroundapp, "User 2", "Post 2", R.drawable.backgroundapp));

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        rvUserPost = rootView.findViewById(R.id.rvUserPost);

        // set adapter
        UserPostAdapter userPostAdapter = new UserPostAdapter(getActivity(), userPostList);
        rvUserPost.setAdapter(userPostAdapter);

        // set layout manager
        rvUserPost.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;

    }
}