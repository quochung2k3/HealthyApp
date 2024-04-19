package com.example.healthyapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.healthyapp.R;
import com.example.healthyapp.models.ListMessModel;
import com.example.healthyapp.models.MessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListMessAdapter extends ArrayAdapter<ListMessModel> {
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    boolean isSeen = true;

    public ListMessAdapter(@NonNull Context context, ArrayList<ListMessModel> listMessView) {
        super(context, 0, listMessView);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @NonNull
    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_mess_custom, parent, false);
        }
        ListMessModel currentMess = getItem(position);
        ImageView imageButton = listItemView.findViewById(R.id.imgAvatar);
        ImageView imgTick = listItemView.findViewById(R.id.imgTick);
        TextView userName = listItemView.findViewById(R.id.txtUsername);
        TextView mess = listItemView.findViewById(R.id.txtMess);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://healthyapp-bfba9-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference databaseReferenceMess = database.getReference().child("Message");
        databaseReferenceMess.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    if (messageModel != null) {
                        if ((messageModel.getSender_id().equals(firebaseUser.getUid()) && messageModel.getReceiver_id().equals(currentMess.getId())) ||
                                (messageModel.getSender_id().equals(currentMess.getId()) && messageModel.getReceiver_id().equals(firebaseUser.getUid()))) {
                            if(messageModel.getReceiver_id().equals(firebaseUser.getUid()) && !messageModel.isIs_seen()) {
                                Log.d("TEST IF", "TEST IF");
                                isSeen = false;
                                break;
                            }
                        }
                    }
                }
                if(isSeen == false) {
                    Log.d("TEST SUCCESS", "TEST SUCCESS");
                    imgTick.setImageDrawable(getContext().getResources().getDrawable(R.drawable.baseline_brightness_1_24));
                }
                else {
                    Log.d("TEST FAIL", "TEST FAIL");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi", Toast.LENGTH_SHORT).show();
            }
        });


        if (currentMess != null) {
            if(currentMess.getImg().equals("")) {
                imageButton.setImageDrawable(getContext().getResources().getDrawable(R.drawable.baseline_account_circle_24));
            }
            else {
                Glide.with(getContext())
                        .load(currentMess.getImg())
                        .circleCrop()
                        .into(imageButton);
            }
            userName.setText(currentMess.getUserName());
            mess.setText(currentMess.getMess());
        }
        return listItemView;
    }
}