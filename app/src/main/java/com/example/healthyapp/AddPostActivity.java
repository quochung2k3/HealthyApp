package com.example.healthyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthyapp.DBConnetion.FirebaseDBConnection;
import com.example.healthyapp.models.FlairModel;
import com.example.healthyapp.models.PostModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AddPostActivity extends AppCompatActivity {
    ImageButton ibBack;
    Button btnAddPost;
    TextView tvTitle, tvContent;
    Switch swAnonymous;
    Spinner spFlair;
    List<FlairModel> flairs = new ArrayList<>();
    ArrayAdapter<FlairModel> adapter = null;
    FirebaseDBConnection db = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseDBConnection.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        btnAddPost = findViewById(R.id.btnPost);
        tvTitle = findViewById(R.id.edtTitle);
        tvContent = findViewById(R.id.edtContent);
        ibBack = findViewById(R.id.ibBack);
        swAnonymous = findViewById(R.id.swAnonymous);
        spFlair = findViewById(R.id.spFlair);
        getFlairs();

        ibBack.setOnClickListener(v -> {
            finish();
        });
        btnAddPost.setOnClickListener(v -> {
            String title = tvTitle.getText().toString();
            String content = tvContent.getText().toString();
            if (title.isEmpty()) {
                return;
            }
            boolean anonymous = swAnonymous.isChecked();
            String flairId = flairs.get(spFlair.getSelectedItemPosition()).getId();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            // Add post to database
            PostModel post = new PostModel(title, content, 0, anonymous, "1", flairId, timestamp);
            db.setData(FirebaseDBConnection.POST, post);
            finish();
        });
    }

    private void getFlairs() {
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        spFlair.setAdapter(adapter);

        db.readData(FirebaseDBConnection.FLAIR, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adapter.clear();
                Object value = snapshot.getValue();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FlairModel flair = dataSnapshot.getValue(FlairModel.class);
                    flair.setId(dataSnapshot.getKey());
                    // log flairs
                    Log.d("flair", flair.getName());
                    flairs.add(flair);
                }
                // add flairs to spinner
                adapter.addAll(flairs);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddPostActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}