package com.example.healthyapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthyapp.PostDetailActivity;
import com.example.healthyapp.R;
import com.example.healthyapp.adapter.ListNotificationAdapter;
import com.example.healthyapp.models.ListNotiModel;
import com.example.healthyapp.models.NotiModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {
    View rootView;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference notificationRef;
    ListNotificationAdapter listNotificationAdapter = null;
    ArrayList<ListNotiModel> listNoti = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_notification, container, false);
        ListView lvNoti = rootView.findViewById(R.id.lvNotification);

        notificationRef = FirebaseDatabase.getInstance("https://healthyapp-bfba9-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Notification");

        notificationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listNoti.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    NotiModel notification = dataSnapshot.getValue(NotiModel.class);
                    assert notification != null;
                    if (notification.getUserPostId().equals(firebaseUser.getUid())) {
                        listNoti.add(new ListNotiModel(notification.getImgAvatar(), notification.getContent(), notification.getPostId(), notification.isIs_click(), dataSnapshot.getKey()));
                    }
                }
                listNotificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        listNoti.add(new ListNotiModel(R.drawable.baseline_search_24, "Quốc Hưng đã đăng 1 bài viết", "1 giờ"));
//        listNoti.add(new ListNotiModel(R.drawable.baseline_search_24, "Đức Phú đã 1 đăng bài viết", "20 phút"));
//        listNoti.add(new ListNotiModel(R.drawable.baseline_search_24, "Quốc Long đã đăng 1 bài viết", "30 phút"));
        listNotificationAdapter = new ListNotificationAdapter(requireContext(), listNoti);
        lvNoti.setAdapter(listNotificationAdapter);

        lvNoti.setOnItemClickListener((parent, view, position, id) -> {
            ListNotiModel notiModel = listNoti.get(position);
            String notificationId = notiModel.getId();

            notificationRef = FirebaseDatabase.getInstance("https://healthyapp-bfba9-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Notification").child(notificationId);
            notificationRef.child("is_click").setValue(true)
                    .addOnSuccessListener(aVoid -> {
                        // Xử lý khi cập nhật thành công (nếu cần)
                    })
                    .addOnFailureListener(e -> {
                        // Xử lý khi cập nhật thất bại (nếu cần)
                    });
            Intent intent = new Intent(getActivity(), PostDetailActivity.class);
            intent.putExtra("post_id", notiModel.getPostId());
            startActivity(intent);
        });

        return rootView;
    }
}
