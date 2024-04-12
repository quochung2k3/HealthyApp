package com.example.healthyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.healthyapp.DBConnetion.FirebaseDBConnection;
import com.example.healthyapp.models.PostModel;
import com.example.healthyapp.models.UserModel;
import com.example.healthyapp.utils.TimestampUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class PostDetailActivity extends AppCompatActivity {
    FirebaseDBConnection db = FirebaseDBConnection.getInstance();
    TextView txtPostTitle, txtPostContent, txtPostDate, txtFlair, txtUserName;
    ImageView imgPost, imgAvatar;
    ImageButton ibBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        txtPostTitle = findViewById(R.id.txtPostTitle);
        txtPostContent = findViewById(R.id.txtPostContent);
        txtPostDate = findViewById(R.id.txtDate);
        txtUserName = findViewById(R.id.txtUsername);
        txtFlair = findViewById(R.id.txtFlair);
        imgPost = findViewById(R.id.ivPostImage);
        imgAvatar = findViewById(R.id.ivAvatar);
        ibBack = findViewById(R.id.ibBack);
        ibBack.setOnClickListener(v -> {
            finish();

        });

        String postId = getIntent().getStringExtra("post_id");
        getPost(postId);
    }

    private void getPost(String postId) {
        DatabaseReference postRef = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL)
                        .getReference(FirebaseDBConnection.POST)
                                .child(postId);
        // get post
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PostModel post = snapshot.getValue(PostModel.class);
                txtPostTitle.setText(post.getTitle());
                txtPostContent.setText(post.getContent());
                String date = TimestampUtil.convertTimestampToDateString(post.getCreated_date());
                txtPostDate.setText(date);
                if (post.getPostImg() == null || post.getPostImg().isEmpty()) {
                    imgPost.setVisibility(View.GONE);
                } else {
                    Picasso.get().load(post.getPostImg()).into(imgPost);
                }
//                txtPostDate.setText(post.getDate());
                // get user
                Log.d("User ID", post.getUser_id());
                FirebaseFirestore.getInstance().collection("users").document(post.getUser_id()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserModel userModel = task.getResult().toObject(UserModel.class);
                        String userName = userModel.getFirst_name() + " " + userModel.getLast_name();
                        txtUserName.setText(userName);
                        if (userModel.getAvatar() == null || userModel.getAvatar().isEmpty()) {
                            imgAvatar.setImageResource(R.drawable.backgroundapp);
                        } else {
                            Picasso.get().load(userModel.getAvatar()).into(imgAvatar);
                        }
                    }
                });
                // get flair
                db.readData(FirebaseDBConnection.FLAIR, new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String flairId = post.getFlair_id();
                        String flairName = snapshot.child(flairId).child("name").getValue(String.class);
                        txtFlair.setText(flairName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}