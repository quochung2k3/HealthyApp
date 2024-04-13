package com.example.healthyapp.adapter;

import android.content.Context;
import android.content.Intent;
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

import com.example.healthyapp.PostDetailActivity;
import com.example.healthyapp.R;
import com.example.healthyapp.models.PostModel;
import com.example.healthyapp.models.UserModel;
import com.example.healthyapp.models.UserPostModel;
import com.example.healthyapp.services.FirebaseStorageService;
import com.example.healthyapp.utils.TimestampUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostAdapter.UserPostViewHolder> {
    private Context context;
    ArrayList<PostModel> postList;
    FirebaseStorageService storageService = new FirebaseStorageService();

    public UserPostAdapter(Context context, ArrayList<PostModel> postList) {
        this.context = context;
        this.postList = postList;
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
        holder.btnLike.setText(String.valueOf(postModel.getLikes()));
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
