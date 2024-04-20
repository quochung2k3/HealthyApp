package com.example.healthyapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.healthyapp.DBConnetion.FirebaseDBConnection;
import com.example.healthyapp.PostDetailActivity;
import com.example.healthyapp.R;
import com.example.healthyapp.fragments.UserHomeFragment;
import com.example.healthyapp.models.PostModel;
import com.example.healthyapp.models.UserModel;
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
        // load post
        PostModel postModel = postList.get(position);
        holder.txtPostTitle.setText(postModel.getTitle());


        if (postModel.isAnonymous()) {
            holder.txtUserName.setText("Người đăng ẩn danh");
            holder.imgUserPost.setImageResource(R.drawable.backgroundapp);
        } else { // get user
            FirebaseFirestore.getInstance().collection("users").document(postList.get(position).getUser_id()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    UserModel userModel = task.getResult().toObject(UserModel.class);
                    String userName = userModel.getFirst_name() + " " + userModel.getLast_name();

                    holder.txtUserName.setText(userName);
                    if (userModel.getImgAvatar() == null || userModel.getImgAvatar().isEmpty()) {
                        holder.imgUserPost.setImageResource(R.drawable.backgroundapp);
                    } else {
//                    Picasso.get().load(userModel.getImgAvatar()).into(holder.imgUserPost);
                        Glide.with(context).load(userModel.getImgAvatar()).circleCrop().into(holder.imgUserPost);
                    }
                }
            });
        }



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

        // btn Comment
        holder.btnComment.setOnClickListener(v -> {
            // go to post detail
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("post_id", postModel.getId());
            intent.putExtra("isComment", true);
            context.startActivity(intent);
        });

        // menu item
        holder.ibMore.setOnClickListener(v -> {
            // show menu
            showPopupMenu(v, postModel);
        });
    }

    private void showPopupMenu(View v, PostModel postModel) {
        PopupMenu popupMenu = new androidx.appcompat.widget.PopupMenu(context, v);
        popupMenu.inflate(R.menu.post_menu);
        // hide delete option if post is not created by current user
        if (!postModel.getUser_id().equals(currentUser)) {
            popupMenu.getMenu().findItem(R.id.post_delete).setVisible(false);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.post_delete) {
                // set post is_deleted = true
                DatabaseReference postRef = db.getReference("Post").child(postModel.getId());
                postRef.child("is_deleted").setValue(true);
                postList.remove(postModel);
                notifyDataSetChanged();
                return true;
            } else if (id == R.id.post_save) {
                // save post
            }
            return false;
        });
        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class UserPostViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUserPost, imgPost;
        TextView txtUserName, txtPostTitle, txtDate;
        Button btnLike, btnComment;
        ImageButton ibMore;

        public UserPostViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUserPost = itemView.findViewById(R.id.ivAvatar);
            imgPost = itemView.findViewById(R.id.ivPostImage);
            txtUserName = itemView.findViewById(R.id.txtUsername);
            txtPostTitle = itemView.findViewById(R.id.txtPostTitle);
            txtDate = itemView.findViewById(R.id.txtDate);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);
            ibMore = itemView.findViewById(R.id.ibMore);
        }
    }
}
