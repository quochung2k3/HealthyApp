package com.example.healthyapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.healthyapp.databinding.ActivityMainBinding;
import com.example.healthyapp.fragments.HomeFragment;
import com.example.healthyapp.fragments.MenuFragment;
import com.example.healthyapp.fragments.MessFragment;
import com.example.healthyapp.fragments.NotificationFragment;
import com.example.healthyapp.models.MessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    TextView txtCountMess, txtCountNotification;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    boolean isSeen = true;
    int countMess = 0;
//    int countNotification = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setBackground(null);

        txtCountMess = findViewById(R.id.countMess);
        txtCountNotification = findViewById(R.id.countNotification);
        txtCountMess.setVisibility(View.GONE);
        txtCountNotification.setVisibility(View.GONE);
        reloadCountMess();

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

    private void reloadCountMess() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://healthyapp-bfba9-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference databaseReferenceMess = database.getReference().child("Message");
        usersRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String userId = documentSnapshot.getId();
                        Log.d("TEST UID", userId);
                        if(!userId.equals(firebaseUser.getUid())) {
                            databaseReferenceMess.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        MessageModel messageModel = snapshot.getValue(MessageModel.class);
                                        Log.d("TEST THU", "1");
                                        if (messageModel != null) {
                                            if ((messageModel.getSender_id().equals(firebaseUser.getUid()) && messageModel.getReceiver_id().equals(userId)) ||
                                                    (messageModel.getSender_id().equals(userId) && messageModel.getReceiver_id().equals(firebaseUser.getUid()))) {
                                                if(messageModel.getReceiver_id().equals(firebaseUser.getUid()) && !messageModel.isIs_seen()) {
                                                    isSeen = false;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if(!isSeen) {
                                        countMess++;
                                        Log.d("TEST COUNT", String.valueOf(countMess));
                                        txtCountMess.setVisibility(View.VISIBLE);
                                        txtCountMess.setText(String.valueOf(countMess));
                                        isSeen = true;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(e -> {

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