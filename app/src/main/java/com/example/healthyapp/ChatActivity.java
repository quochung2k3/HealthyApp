package com.example.healthyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.healthyapp.databinding.ActivityMainBinding;
import com.example.healthyapp.fragments.HomeFragment;
import com.example.healthyapp.fragments.MenuFragment;
import com.example.healthyapp.fragments.MessFragment;
import com.example.healthyapp.fragments.NotificationFragment;
import com.example.healthyapp.fragments.UpdatePassFragment;
import com.example.healthyapp.models.MessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatActivity extends AppCompatActivity {

    TextView txtUsername;
    EditText edtMess;
    ImageView imgBack, imgSendMess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Mapping();
        Intent intent = getIntent();
        if (intent != null) {
            String userName = intent.getStringExtra("userName");
            txtUsername.setText(userName);
        }
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityMainBinding binding;
                binding = ActivityMainBinding.inflate(getLayoutInflater());
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
                    if (item.getItemId() == R.id.notification) {
                        replaceFragment(new NotificationFragment());
                    }
                    if (item.getItemId() == R.id.menu) {
                        replaceFragment(new MenuFragment());
                    }
                    return true;
                });
            }
        });
        imgSendMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                MessageModel message = new MessageModel();
                message.setContent(edtMess.getText().toString());
                assert firebaseUser != null;
                message.setSender_id(firebaseUser.getUid());
                assert intent != null;
                message.setReceiver_id(intent.getStringExtra("id"));
                message.setIs_deleted(false);
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://healthyapp-bfba9-default-rtdb.asia-southeast1.firebasedatabase.app/");
                DatabaseReference databaseReference = database.getReference();
                databaseReference.child("Message").push().setValue(message);
                edtMess.setText("");
            }
        });
    }

    private void Mapping() {
        txtUsername = findViewById(R.id.txtUsername);
        edtMess = findViewById(R.id.edtMess);
        imgBack = findViewById(R.id.back_button);
        imgSendMess = findViewById(R.id.imgSendMess);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}