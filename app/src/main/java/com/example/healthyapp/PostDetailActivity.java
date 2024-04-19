package com.example.healthyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.healthyapp.DBConnetion.FirebaseDBConnection;
import com.example.healthyapp.adapter.CommentAdapter;
import com.example.healthyapp.models.CommentModel;
import com.example.healthyapp.models.PostModel;
import com.example.healthyapp.models.UserModel;
import com.example.healthyapp.utils.TimestampUtil;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostDetailActivity extends AppCompatActivity {
    FirebaseDBConnection db = FirebaseDBConnection.getInstance();
    TextView txtPostTitle, txtPostContent, txtPostDate, txtFlair, txtUserName;
    ImageView imgPost, imgAvatar;
    Button btnLike, btnComment;
    ImageButton ibBack, ibSendComment;
    EditText edtComment;
    RecyclerView rvComment;
    String postId;
    PostModel post;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String currentUserId = auth.getCurrentUser().getUid();
    ArrayList<CommentModel> commentList = new ArrayList<>();
    CommentAdapter commentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        edtComment = findViewById(R.id.edtComment);
        ibSendComment = findViewById(R.id.ibSendComment);
        rvComment = findViewById(R.id.rvComment);
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
        btnLike = findViewById(R.id.btnLike);
        btnComment = findViewById(R.id.btnComment);
        commentAdapter = new CommentAdapter(this, commentList);
        rvComment.setAdapter(commentAdapter);
        rvComment.setLayoutManager(new LinearLayoutManager(PostDetailActivity.this));

        postId = getIntent().getStringExtra("post_id");
        getPost(postId).addOnCompleteListener(task -> {
            post = task.getResult();
            loadPost();
        });

        // comment
        ibSendComment.setOnClickListener(v -> {
            sendComment();
        });
        loadComment();

        // btn like
        btnLike.setOnClickListener(v -> {
            FirebaseDatabase fb = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL);
            // like post
            DatabaseReference postRef = fb.getReference("Post").child(post.getId());
            // get newest user likes
            postRef.child("user_likes").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().getValue() == null) {
                        post.setUser_likes(new HashMap<>());
                    } else {
                        post.setUser_likes((Map<String, Integer>) task.getResult().getValue());
                    }
                    Log.d("UserPostAdapter", "user likes: " + post.getUser_likes());
                    Drawable likeIcon = btnLike.getCompoundDrawables()[0];
                    if (post.getUser_likes().containsKey(currentUserId)) {
                        // unlike post
                        post.getUser_likes().remove(currentUserId);
                        likeIcon.setTint(getResources().getColor(R.color.primary_color));
                    } else {
                        // like post
                        post.getUser_likes().put(currentUserId, 1);
                        likeIcon.setTint(getResources().getColor(R.color.blue));
                    }
                    postRef.child("user_likes").setValue(post.getUser_likes());
                    btnLike.setText(String.valueOf(post.getUser_likes().size()));
                } else {
                    Log.d("UserPostAdapter", "Error getting user likes: ", task.getException());
                }
            });
        });


    }

    private void loadComment() {
        DatabaseReference commentRef = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL)
                .getReference(FirebaseDBConnection.COMMENT)
                .child(postId);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                    CommentModel comment = commentSnapshot.getValue(CommentModel.class);
                    Log.d("Comment", comment.getContent());
                    comment.setId(commentSnapshot.getKey());
                    commentList.add(comment);
                }
                Log.d("Comment", "size: " + commentList.size());
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PostDetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendComment() {
        String commentContent = edtComment.getText().toString();
        if (commentContent.isEmpty()) {
            return;
        }
        DatabaseReference commentRef = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL)
                .getReference(FirebaseDBConnection.COMMENT)
                .child(post.getId())
                .push();
        CommentModel comment = new CommentModel();
        comment.setContent(commentContent);
        comment.setUser_id(currentUserId);
        comment.setPost_id(post.getId());
        Long timestamp = System.currentTimeMillis();
        comment.setCreated_date(timestamp);
        commentRef.setValue(comment);
        edtComment.setText("");
    }

    private void loadPost() {
        btnLike.setText(String.valueOf(post.getUser_likes().size()));
        Drawable likeIcon = btnLike.getCompoundDrawables()[0];
        if (post.getUser_likes().containsKey(currentUserId)) {
            likeIcon.setTint(getResources().getColor(R.color.blue));
        } else {
            likeIcon.setTint(getResources().getColor(R.color.primary_color));
        }
        txtPostTitle.setText(post.getTitle());
        txtPostContent.setText(post.getContent());
        txtPostDate.setText(TimestampUtil.convertTimestampToDateString(post.getCreated_date()));
        if (post.getPostImg() == null || post.getPostImg().isEmpty()) {
            imgPost.setVisibility(View.GONE);
        } else {
            Picasso.get().load(post.getPostImg()).into(imgPost);
        }
    }
    private Task<PostModel> getPost(String postId) {
        TaskCompletionSource<PostModel> taskCompletionSource = new TaskCompletionSource<>();
        DatabaseReference postRef = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL)
                        .getReference(FirebaseDBConnection.POST)
                                .child(postId);
        // get post
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PostModel post = snapshot.getValue(PostModel.class);
                post.setId(snapshot.getKey());
                taskCompletionSource.setResult(post);

                if (post.isAnonymous()) {
                    txtUserName.setText("Người đăng ẩn danh");
                    imgAvatar.setImageResource(R.drawable.backgroundapp);
                } else {
                    // get user
                    Log.d("User ID", post.getUser_id());
                    FirebaseFirestore.getInstance().collection("users").document(post.getUser_id()).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            UserModel userModel = task.getResult().toObject(UserModel.class);
                            String userName = userModel.getFirst_name() + " " + userModel.getLast_name();
                            txtUserName.setText(userName);
                            if (userModel.getImgAvatar() == null || userModel.getImgAvatar().isEmpty()) {
                                imgAvatar.setImageResource(R.drawable.backgroundapp);
                            } else {
//                            Picasso.get().load(userModel.getImgAvatar()).into(imgAvatar);
                                Glide.with(PostDetailActivity.this).load(userModel.getImgAvatar()).circleCrop().into(imgAvatar);

                            }
                        }
                    });
                }
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
        return taskCompletionSource.getTask();
    }
}