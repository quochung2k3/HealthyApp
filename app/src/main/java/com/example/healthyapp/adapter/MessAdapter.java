package com.example.healthyapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.healthyapp.R;
import com.example.healthyapp.models.MessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MessAdapter extends RecyclerView.Adapter<MessAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private final Context mContext;
    private final List<MessageModel> listMessage;
    FirebaseUser firebaseUser;
    FirebaseFirestore firestore;
    public MessAdapter(Context mContext, List<MessageModel> listMessage) {
        this.mContext = mContext;
        this.listMessage = listMessage;
    }
    @NonNull
    @Override
    public MessAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
        }
        else {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
        }
        return new ViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageModel message = listMessage.get(position);
        holder.message.setText(message.getContent());

        if (getItemViewType(position) == MSG_TYPE_LEFT) {
            firestore = FirebaseFirestore.getInstance();
            DocumentReference document = firestore.collection("users").document(message.getSender_id());
            document.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        String linkImg = doc.getString("imgAvatar");
                        Log.d("TEST LINK AVATAR", linkImg);
                        assert linkImg != null;
                        if(linkImg.equals("")) {
                            holder.imgAvatar.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.baseline_account_circle_24));
                            Log.d("TESTHUNG", "Success");
                        }
                        else {
                            Glide.with(mContext)
                                    .load(linkImg)
                                    .circleCrop()
                                    .into(holder.imgAvatar);
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listMessage.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public ImageView imgAvatar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            imgAvatar = itemView.findViewById(R.id.avatar);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(listMessage.get(position).getSender_id().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        }
        return MSG_TYPE_LEFT;
    }
}
