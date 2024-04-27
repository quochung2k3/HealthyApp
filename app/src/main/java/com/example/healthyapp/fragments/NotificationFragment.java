package com.example.healthyapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.healthyapp.PostDetailActivity;
import com.example.healthyapp.R;
import com.example.healthyapp.adapter.ListNotificationAdapter;
import com.example.healthyapp.models.ListNotiModel;
import com.example.healthyapp.models.NotiModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class NotificationFragment extends Fragment {
    View rootView;
    TextView txtBookMark;
    ImageView img;
    ListView lvNoti;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mDatabase;
    DatabaseReference notificationRef;
    ListNotificationAdapter listNotificationAdapter = null;
    ArrayList<ListNotiModel> listNoti = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_notification, container, false);
        Mapping();
        notificationRef = FirebaseDatabase.getInstance("https://healthyapp-bfba9-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Notification");
        notificationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listNoti.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    NotiModel notification = dataSnapshot.getValue(NotiModel.class);
                    assert notification != null;
                    if (notification.getUserPostId().equals(firebaseUser.getUid())) {
                        dataSnapshot.getRef().child("is_seen").setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        notificationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listNoti.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    NotiModel notification = dataSnapshot.getValue(NotiModel.class);
                    assert notification != null;
                    if (notification.getUserPostId().equals(firebaseUser.getUid()) && notification.isIs_active()) {
                        listNoti.add(new ListNotiModel(notification.getImgAvatar(), notification.getContent(), notification.getPostId(), notification.isIs_click(), dataSnapshot.getKey()));
                    }
                }
                Collections.reverse(listNoti);
                listNotificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listNotificationAdapter = new ListNotificationAdapter(requireContext(), listNoti);
        lvNoti.setAdapter(listNotificationAdapter);

        lvNoti.setOnItemClickListener((parent, view, position, id) -> {
            ListNotiModel notiModel = listNoti.get(position);
            String notificationId = notiModel.getId();

            notificationRef = FirebaseDatabase.getInstance("https://healthyapp-bfba9-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Notification").child(notificationId);
            notificationRef.child("is_click").setValue(true)
                    .addOnSuccessListener(aVoid -> {

                    })
                    .addOnFailureListener(e -> {

                    });
            Intent intent = new Intent(getActivity(), PostDetailActivity.class);
            intent.putExtra("post_id", notiModel.getPostId());
            startActivity(intent);
        });

        lvNoti.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ListNotiModel selectedNotification = listNoti.get(position);
                LayoutInflater inflater1 = LayoutInflater.from(getContext());
                @SuppressLint("InflateParams") View bottomSheetView = inflater1.inflate(R.layout.bottom_sheet, null);
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
                bottomSheetDialog.setContentView(bottomSheetView);
                Button btnConfirm = bottomSheetView.findViewById(R.id.btnConfirm);
                btnConfirm.setText("Delete Notification");
                Button btnCancel = bottomSheetView.findViewById(R.id.btnCancel);
                btnCancel.setText("Cancel");
                TextView txtTitle = bottomSheetView.findViewById(R.id.txtTitle);
                txtTitle.setText("You want to delete this notification?");
                bottomSheetDialog.show();
                btnConfirm.setOnClickListener(v -> {
                    showAnnouncementDialogNotification(selectedNotification.getId());
                    bottomSheetDialog.dismiss();
                });
                btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());
                return true;
            }
        });

        txtBookMark.setOnClickListener(v -> showAnnouncementDialog());

        return rootView;
    }

    private void showAnnouncementDialogNotification(String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Announcement");
        builder.setMessage("Once deleted, notification cannot be recovered, are you sure you want to delete?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            DatabaseReference notificationRef = FirebaseDatabase.getInstance("https://healthyapp-bfba9-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("Notification").child(id);
            notificationRef.child("is_active").setValue(false)
                    .addOnSuccessListener(aVoid -> {

                    })
                    .addOnFailureListener(e -> {

                    });
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAnnouncementDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Announcement");
        builder.setMessage("Would you like to mark all as read?");
        builder.setPositiveButton("Yes", (dialog, id) -> {
            mDatabase = FirebaseDatabase.getInstance("https://healthyapp-bfba9-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Notification");
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot notificationSnapshot : dataSnapshot.getChildren()) {
                        NotiModel notification = notificationSnapshot.getValue(NotiModel.class);
                        if (notification != null && notification.getUserPostId().equals(firebaseUser.getUid())
                                && !notification.isIs_click()) {
                            notificationSnapshot.getRef().child("is_click").setValue(true);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });
        builder.setNegativeButton("Cancel", (dialog, id) -> {
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void Mapping() {
        lvNoti = rootView.findViewById(R.id.lvNotification);
        txtBookMark = rootView.findViewById(R.id.txtBookMark);
        img = rootView.findViewById(R.id.img);

        ConstraintLayout notificationFragment = rootView.findViewById(R.id.notificationFragment);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;
        int newHeight = (int) ((811.0 / 891.0) * screenHeight);
        ViewGroup.LayoutParams layoutParams = notificationFragment.getLayoutParams();
        layoutParams.height = newHeight;
        notificationFragment.setLayoutParams(layoutParams);

        // img
        int imgWidth = (int) (screenWidth * 0.3);
        int imgHeight = (int) (newHeight * 0.07);
        img.getLayoutParams().width = imgWidth;
        img.getLayoutParams().height = imgHeight;

        // lv
        lvNoti.getLayoutParams().height = (int) (newHeight * 0.84);
    }
}