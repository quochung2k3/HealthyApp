package com.example.healthyapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthyapp.PostDetailActivity;
import com.example.healthyapp.R;
import com.example.healthyapp.fragments.UserHomeFragment;
import com.example.healthyapp.models.PostModel;
import com.example.healthyapp.models.UserModel;
import com.example.healthyapp.services.FirebaseStorageService;
import com.example.healthyapp.utils.TimestampUtil;
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

        holder.imgUserPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = holder.txtUserName.getText().toString();
                String id = postModel.getUser_id();
                Bundle bundle = new Bundle();
                bundle.putString("userName", userName);
                bundle.putString("id", id);
                UserHomeFragment userHomeFragment = new UserHomeFragment();
                userHomeFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, userHomeFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

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
