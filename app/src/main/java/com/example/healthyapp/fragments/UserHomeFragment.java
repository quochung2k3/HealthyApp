package com.example.healthyapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthyapp.ChatActivity;
import com.example.healthyapp.R;
import com.example.healthyapp.adapter.ListMessAdapter;
import com.example.healthyapp.models.ListMessModel;
import com.example.healthyapp.models.MessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserHomeFragment extends Fragment {
    TextView txtUsername;
    Button btnChat;
    ArrayList<ListMessModel> listMess = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_home, container, false);
        txtUsername = rootView.findViewById(R.id.txtUsername);
        btnChat = rootView.findViewById(R.id.btnChat);
        assert getArguments() != null;
        Log.d("abcxyz", getArguments().getString("userName"));
        txtUsername.setText(getArguments().getString("userName"));
        String id = getArguments().getString("id");
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("userName", txtUsername.getText().toString());
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        return rootView;
    }
}