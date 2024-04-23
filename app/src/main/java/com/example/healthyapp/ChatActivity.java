package com.example.healthyapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.healthyapp.adapter.MessAdapter;
import com.example.healthyapp.databinding.ActivityMainBinding;
import com.example.healthyapp.fragments.HomeFragment;
import com.example.healthyapp.fragments.MenuFragment;
import com.example.healthyapp.fragments.MessFragment;
import com.example.healthyapp.fragments.NotificationFragment;
import com.example.healthyapp.fragments.UserHomeFragment;
import com.example.healthyapp.models.MessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    TextView txtUsername;
    EditText edtMess;
    ImageView imgBack, imgSendMess, profile_image;
    MessAdapter messAdapter;
    List<MessageModel> listMessage;
    RecyclerView recyclerView;
    Intent intent;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://healthyapp-bfba9-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ActivityMainBinding binding;
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.countMess.setVisibility(View.GONE);
        binding.countNotification.setVisibility(View.GONE);
//        updateCount();
        Mapping();
        intent = getIntent();
        if (intent != null) {
            String userName = intent.getStringExtra("userName");
            String linkImg = intent.getStringExtra("linkImg");
            assert linkImg != null;
            if(linkImg.isEmpty()) {
                profile_image.setImageDrawable(getResources().getDrawable(R.drawable.baseline_account_circle_24));
            }
            else {
                Glide.with(this)
                        .load(linkImg)
                        .circleCrop()
                        .into(profile_image);
            }
            txtUsername.setText(userName);
        }
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        readMess(firebaseUser.getUid(), intent.getStringExtra("id"));
        profile_image.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("userName", intent.getStringExtra("userName"));
            bundle.putString("id", intent.getStringExtra("id"));
            UserHomeFragment userHomeFragment = new UserHomeFragment();
            userHomeFragment.setArguments(bundle);
            setContentView(binding.getRoot());
            replaceFragment(userHomeFragment);
            binding.bottomNavigationView.setBackground(null);
            binding.bottomNavigationView.setSelectedItemId(R.id.message);
            binding.bottomNavigationView.setOnItemSelectedListener(item -> {
                if (item.getItemId() == R.id.home) {
                    replaceFragment(new HomeFragment());
                }
                if (item.getItemId() == R.id.message) {
                    replaceFragment(new MessFragment());
                }
                if (item.getItemId() == R.id.notification) {
                    replaceFragment(new NotificationFragment());
                }
                if (item.getItemId() == R.id.menu) {
                    replaceFragment(new MenuFragment());
                }
                return true;
            });
        });
        imgBack.setOnClickListener(v -> {
            setContentView(binding.getRoot());
            replaceFragment(new MessFragment());
            binding.bottomNavigationView.setBackground(null);
            binding.bottomNavigationView.setSelectedItemId(R.id.message);
            binding.bottomNavigationView.setOnItemSelectedListener(item -> {
                if (item.getItemId() == R.id.home) {
                    replaceFragment(new HomeFragment());
                }
                if (item.getItemId() == R.id.message) {
                    replaceFragment(new MessFragment());
                }
                if (item.getItemId() == R.id.fab) {
                    Intent intent = new Intent(ChatActivity.this, AddPostActivity.class);
                    startActivityForResult(intent, 1);
                }
                if (item.getItemId() == R.id.notification) {
                    replaceFragment(new NotificationFragment());
                }
                if (item.getItemId() == R.id.menu) {
                    replaceFragment(new MenuFragment());
                }
                return true;
            });
        });
        imgSendMess.setOnClickListener(v -> {
            if(!edtMess.getText().toString().isEmpty()) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                MessageModel message = new MessageModel();
                message.setContent(edtMess.getText().toString());
                assert firebaseUser != null;
                message.setSender_id(firebaseUser.getUid());
                assert intent != null;
                message.setReceiver_id(intent.getStringExtra("id"));
                message.setIs_deleted_by_me(false);
                message.setIs_deleted_by_other(false);
                message.setIs_seen(false);
                database.getReference().child("Message").push().setValue(message);
                edtMess.setText("");
            }
            else {
                Toast.makeText(ChatActivity.this, "Cannot send messages with empty content", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Mapping() {
        txtUsername = findViewById(R.id.txtUsername);
        edtMess = findViewById(R.id.edtMess);
        imgBack = findViewById(R.id.back_button);
        imgSendMess = findViewById(R.id.imgSendMess);
        profile_image = findViewById(R.id.profile_image);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public void readMess(String myId, String userId) {
        listMessage = new ArrayList<>();
        databaseReference = database.getReference("Message");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listMessage.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessageModel message = dataSnapshot.getValue(MessageModel.class);
                    assert message != null;
                    if((message.getReceiver_id().equals(myId) && message.getSender_id().equals(userId))
                            || (message.getReceiver_id().equals(userId) && message.getSender_id().equals(myId))) {
                        if((!message.isIs_deleted_by_me() && message.getSender_id().equals(myId))
                            || (message.getReceiver_id().equals(myId) && !message.isIs_deleted_by_other())) {
                            message.setId(dataSnapshot.getKey());
                            listMessage.add(message);
                        }
                    }
                    messAdapter = new MessAdapter(ChatActivity.this, listMessage);
                    recyclerView.setAdapter(messAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}