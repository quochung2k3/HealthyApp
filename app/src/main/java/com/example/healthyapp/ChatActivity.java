package com.example.healthyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class ChatActivity extends AppCompatActivity {

    TextView txtUsername;
    ImageView imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        txtUsername = findViewById(R.id.txtUsername);
        imgBack = findViewById(R.id.back_button);
        Intent intent = getIntent();
        if (intent != null) {
            String userName = intent.getStringExtra("userName");
            String id = intent.getStringExtra("id");
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
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}