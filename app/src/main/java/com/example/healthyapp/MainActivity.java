package com.example.healthyapp;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.example.healthyapp.databinding.ActivityMainBinding;
import com.example.healthyapp.fragments.HomeFragment;
import com.example.healthyapp.fragments.MenuFragment;
import com.example.healthyapp.fragments.MessFragment;
import com.example.healthyapp.fragments.NotificationFragment;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
//    String countMess = "";
//    String countNotification = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setBackground(null);

//        TextView txtCountMess = findViewById(R.id.countMess);
//        TextView txtCountNotification = findViewById(R.id.countNotification);
//        if(!countMess.isEmpty()) {
//            txtCountMess.setText(countMess);
//        }
//        else {
//            txtCountMess.setVisibility(View.GONE);
//        }
//
//        if(!countNotification.isEmpty()) {
//            txtCountNotification.setText(countNotification);
//        }
//        else {
//            txtCountNotification.setVisibility(View.GONE);
//        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
            }
            if (item.getItemId() == R.id.message) {
                replaceFragment(new MessFragment());
            }
            if (item.getItemId() == R.id.fab) {
                Intent intent = new Intent(MainActivity.this, AddPostActivity.class);
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
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            binding.bottomNavigationView.setSelectedItemId(R.id.home);
        }
    }
}