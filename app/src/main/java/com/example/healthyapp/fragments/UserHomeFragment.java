package com.example.healthyapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.healthyapp.ChatActivity;
import com.example.healthyapp.R;
import com.example.healthyapp.models.ListMessModel;

import java.util.ArrayList;

public class UserHomeFragment extends Fragment {
    TextView txtUsername;
    Button btnChat;
    ImageView imgBack;
    ArrayList<ListMessModel> listMess = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_home, container, false);
        txtUsername = rootView.findViewById(R.id.txtUsername);
        btnChat = rootView.findViewById(R.id.btnChat);
        imgBack = rootView.findViewById(R.id.back_button);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                }
                else {
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            }
        });

        assert getArguments() != null;
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