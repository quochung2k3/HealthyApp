package com.example.healthyapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthyapp.DBConnetion.FirebaseDBConnection;
import com.example.healthyapp.PostDetailActivity;
import com.example.healthyapp.R;
import com.example.healthyapp.models.PostModel;
import com.example.healthyapp.models.UserModel;
import com.example.healthyapp.models.UserPostModel;
import com.example.healthyapp.services.FirebaseStorageService;
import com.example.healthyapp.utils.TimestampUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostAdapter.UserPostViewHolder> {
    private Context context;
    ArrayList<PostModel> postList;
    FirebaseStorageService storageService = new FirebaseStorageService();
    FirebaseDatabase db = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL);
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String currentUser = null;
    public UserPostAdapter(Context context, ArrayList<PostModel> postList) {
        this.context = context;
        this.postList = postList;
        currentUser = auth.getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public UserPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_post, parent, false);
        return new UserPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserPostViewHolder holder, int position) {
        // get user
        FirebaseFirestore.getInstance().collection("users").document(postList.get(position).getUser_id()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                UserModel userModel = task.getResult().toObject(UserModel.class);
                String userName = userModel.getFirst_name() + " " + userModel.getLast_name();
                holder.txtUserName.setText(userName);
                if (userModel.getAvatar() == null || userModel.getAvatar().isEmpty()) {
                    holder.imgUserPost.setImageResource(R.drawable.backgroundapp);
                } else {
                    Picasso.get().load(userModel.getAvatar()).into(holder.imgUserPost);
                }
            }
        });

        // load post
        PostModel postModel = postList.get(position);
        holder.txtPostTitle.setText(postModel.getTitle());

        // get likes
        int likes = postModel.getUser_likes().size();
        holder.btnLike.setText(String.valueOf(likes));
        // check if user liked post
        // get button drawable
        Drawable drawable = holder.btnLike.getCompoundDrawables()[0];
        boolean liked = postModel.getUser_likes().containsKey(currentUser);
        if (liked) {
            drawable.setTint(context.getResources().getColor(R.color.blue));
        } else {
            drawable.setTint(context.getResources().getColor(R.color.primary_color));
        }

        // btnLike onClick
        holder.btnLike.setOnClickListener(v -> {
            DatabaseReference postRef = db.getReference("Post").child(postModel.getId());
            // get newest user likes
            postRef.child("user_likes").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().getValue() == null) {
                        postModel.setUser_likes(new HashMap<>());
                    } else {
                        postModel.setUser_likes((Map<String, Integer>) task.getResult().getValue());
                    }
                    Log.d("UserPostAdapter", "user likes: " + postModel.getUser_likes());
                    if (postModel.getUser_likes().containsKey(currentUser)) {
                        // unlike post
                        postModel.getUser_likes().remove(currentUser);
                        drawable.setTint(context.getResources().getColor(R.color.primary_color));
                    } else {
                        // like post
                        postModel.getUser_likes().put(currentUser, 1);
                        drawable.setTint(context.getResources().getColor(R.color.blue));
                    }
                    postRef.child("user_likes").setValue(postModel.getUser_likes());
                    holder.btnLike.setText(String.valueOf(postModel.getUser_likes().size()));
                } else {
                    Log.d("UserPostAdapter", "Error getting user likes: ", task.getException());
                }
            });

        });

        String date = TimestampUtil.convertTimestampToDateString(postModel.getCreated_date());
        holder.txtDate.setText(date);
        if (postModel.getPostImg() == null || postModel.getPostImg().isEmpty()) {
            holder.imgPost.setVisibility(View.GONE);
        } else {
            holder.imgPost.setVisibility(View.VISIBLE);
            Picasso.get().load(postModel.getPostImg()).into(holder.imgPost);
        }

        // onItemClick
        holder.itemView.setOnClickListener(v -> {
            // go to post detail
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("post_id", postModel.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class UserPostViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUserPost, imgPost;
        TextView txtUserName, txtPostTitle, txtDate;
        Button btnLike;

        public UserPostViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUserPost = itemView.findViewById(R.id.ivAvatar);
            imgPost = itemView.findViewById(R.id.ivPostImage);
            txtUserName = itemView.findViewById(R.id.txtUsername);
            txtPostTitle = itemView.findViewById(R.id.txtPostTitle);
            txtDate = itemView.findViewById(R.id.txtDate);
            btnLike = itemView.findViewById(R.id.btnLike);
        }
    }
}
