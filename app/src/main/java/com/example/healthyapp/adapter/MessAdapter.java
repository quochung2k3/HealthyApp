package com.example.healthyapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.healthyapp.R;
import com.example.healthyapp.models.MessageModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageModel message = listMessage.get(position);
        holder.message.setText(message.getContent());
        DatabaseReference messageRef = FirebaseDatabase.getInstance("https://healthyapp-bfba9-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Message").child(message.getId());
        Map<String, Object> updateData = new HashMap<>();
        if(message.getReceiver_id().equals(firebaseUser.getUid())) {
            updateData.put("is_seen", true);
        }
        messageRef.updateChildren(updateData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        Log.d("TEST LINK DB", FirebaseDatabase.getInstance().toString());
        if (getItemViewType(position) == MSG_TYPE_LEFT) {
            firestore = FirebaseFirestore.getInstance();
            DocumentReference document = firestore.collection("users").document(message.getSender_id());
            document.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        String linkImg = doc.getString("imgAvatar");
                        assert linkImg != null;
                        if(linkImg.isEmpty()) {
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
        holder.itemView.setOnLongClickListener(view -> {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View bottomSheetView = inflater.inflate(R.layout.bottom_sheet_logout, null);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
            bottomSheetDialog.setContentView(bottomSheetView);
            Button btnConfirm = bottomSheetView.findViewById(R.id.btnConfirm);
            btnConfirm.setText("Delete This Mess");
            Button btnCancel = bottomSheetView.findViewById(R.id.btnCancel);
            btnCancel.setText("Cancel");
            bottomSheetDialog.show();
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAnnouncementDialog(message.getId(), message.getSender_id());
                    Log.d("TEST UID MESS", message.getId());
                    bottomSheetDialog.dismiss();
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.dismiss();
                }
            });
            return true;
        });
    }

    private void showAnnouncementDialog(String id, String sender_id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Thông báo");
        builder.setMessage("Tin nhắn sau khi xoá không thể phục hồi được, bạn có chắc chắn muốn xoá?");
        builder.setPositiveButton("Xoá", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference messageRef = FirebaseDatabase.getInstance("https://healthyapp-bfba9-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Message").child(id);
                Log.d("TEST LINK DB", FirebaseDatabase.getInstance().toString());
                Map<String, Object> updateData = new HashMap<>();
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if(sender_id.equals(firebaseUser.getUid())) {
                    updateData.put("is_deleted_by_me", true);
                }
                else {
                    updateData.put("is_deleted_by_other", true);
                }
                messageRef.updateChildren(updateData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Xử lý thành công
                                Toast.makeText(mContext, "Đã xoá tin nhắn", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Xử lý khi thất bại
                                Toast.makeText(mContext, "Xoá tin nhắn thất bại", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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