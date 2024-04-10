package com.example.healthyapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthyapp.R;
import com.example.healthyapp.models.UserPostModel;

import java.util.ArrayList;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostAdapter.UserPostViewHolder> {
    private Context context;
    private ArrayList<UserPostModel> userPostList;

    public UserPostAdapter(Context context, ArrayList<UserPostModel> userPostList) {
        this.context = context;
        this.userPostList = userPostList;
    }

    @NonNull
    @Override
    public UserPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_post, parent, false);
        return new UserPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserPostViewHolder holder, int position) {
        UserPostModel userPostModel = userPostList.get(position);
//        holder.imgUserPost.setImageResource(userPostModel.getAvatar());
        if (userPostModel.getAvatar().isEmpty()) {
            holder.imgUserPost.setImageResource(R.drawable.backgroundapp);
        } else {
            holder.imgUserPost.setImageResource(R.drawable.backgroundapp);
            // TODO: set image from url
        }
        holder.txtUserName.setText(userPostModel.getUserName());
        holder.txtPostTitle.setText(userPostModel.getPostTitle());
//        holder.imgPost.setImageResource(userPostModel.getPostImg());
        if (userPostModel.getPostImg().isEmpty()) {
            // hide image view
            holder.imgPost.setVisibility(View.GONE);
        } else {
            holder.imgPost.setImageResource(R.drawable.backgroundapp);
            // TODO: set image from url
        }
        holder.btnLike.setText(String.valueOf(userPostModel.getLikes()));
    }

    @Override
    public int getItemCount() {
        return userPostList.size();
    }

    public static class UserPostViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUserPost, imgPost;
        TextView txtUserName, txtPostTitle;
        Button btnLike;

        public UserPostViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUserPost = itemView.findViewById(R.id.ivAvatar);
            imgPost = itemView.findViewById(R.id.ivPostImage);
            txtUserName = itemView.findViewById(R.id.txtUsername);
            txtPostTitle = itemView.findViewById(R.id.txtPostTitle);
            btnLike = itemView.findViewById(R.id.btnLike);

        }
    }
}
