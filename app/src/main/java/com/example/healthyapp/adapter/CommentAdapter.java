package com.example.healthyapp.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.healthyapp.DBConnetion.FirebaseDBConnection;
import com.example.healthyapp.PostDetailActivity;
import com.example.healthyapp.R;
import com.example.healthyapp.models.CommentModel;
import com.example.healthyapp.models.UserModel;
import com.example.healthyapp.utils.TimestampUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private Context context;
    ArrayList<CommentModel> commentList;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase db = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL);
    String currentUser = auth.getCurrentUser().getUid();
    public CommentAdapter(Context context, ArrayList<CommentModel> commentList) {
        this.context = context;
        this.commentList = commentList;
    }
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_comment, parent, false);
        return new CommentViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentModel comment = commentList.get(position);

        // get user
        firestore.collection("users").document(comment.getUser_id()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                UserModel user = documentSnapshot.toObject(UserModel.class);
                user.setId(documentSnapshot.getId());
                holder.user = user;
                holder.txtUsername.setText(user.getFirst_name() + " " + user.getLast_name());
                if (user.getImgAvatar() == null || user.getImgAvatar().isEmpty()) {
                    holder.ivAvatar.setImageResource(R.drawable.backgroundapp);
                } else {
//                    Picasso.get().load(user.getImgAvatar()).into(holder.ivAvatar);
                    Glide.with(context).load(user.getImgAvatar()).circleCrop().into(holder.ivAvatar);
                }
            }
        });
        holder.txtCommentContent.setText(comment.getContent());
        holder.txtDate.setText(TimestampUtil.convertTimestampToDateString(comment.getCreated_date()));

        // like
        if (comment.getLikes() == null) {
            comment.setLikes(new HashMap<>());
        }
        holder.btnLike.setText(String.valueOf(comment.getLikes().size()));
        // get btn drawable
        Drawable likeIcon = holder.btnLike.getCompoundDrawables()[0];
        if (comment.getLikes().containsKey(currentUser)) {
            likeIcon.setTint(context.getResources().getColor(R.color.blue));
        } else {
            likeIcon.setTint(context.getResources().getColor(R.color.black));
        }
        holder.btnLike.setOnClickListener(v -> {
            // like comment
            if (comment.getLikes() == null) {
                comment.setLikes(new HashMap<>());
            }
            if (comment.getLikes().containsKey(currentUser)) {
                comment.getLikes().remove(currentUser);
                likeIcon.setTint(context.getResources().getColor(R.color.black));
            } else {
                comment.getLikes().put(currentUser, true);
                likeIcon.setTint(context.getResources().getColor(R.color.blue));
            }
            // update likes
            DatabaseReference commentRef = null;
            if (comment.getParent_id() != null) {
                commentRef = db.getReference(FirebaseDBConnection.COMMENT).child(comment.getPost_id()).child(comment.getParent_id()).child("replies").child(comment.getId());
            } else {
                commentRef = db.getReference(FirebaseDBConnection.COMMENT).child(comment.getPost_id()).child(comment.getId());
            }
            commentRef.child("likes").setValue(comment.getLikes());
        });

        // reply
        holder.ibReply.setOnClickListener(v -> {
            PostDetailActivity activity = (PostDetailActivity) context;
            activity.replyTo = comment.getParent_id() == null ? comment.getId() : comment.getParent_id();
            activity.showReplyTo(holder.user.getFirst_name() + " " + holder.user.getLast_name());
        });
        if (comment.getParent_id() == null) {
            holder.verticalLine.setVisibility(View.GONE);
        } else {
            holder.verticalLine.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView txtUsername, txtCommentContent, txtDate;
        Button btnLike;
        ImageButton ibReply;
        ImageView ivAvatar;
        UserModel user;
        View verticalLine;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtCommentContent = itemView.findViewById(R.id.txtCommentContent);
            txtDate = itemView.findViewById(R.id.txtDate);
            btnLike = itemView.findViewById(R.id.btnLike);
            ibReply = itemView.findViewById(R.id.ibReply);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            verticalLine = itemView.findViewById(R.id.vertical_line);
        }
    }
}