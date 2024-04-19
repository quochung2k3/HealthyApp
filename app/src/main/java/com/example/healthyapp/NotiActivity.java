package com.example.healthyapp;

import android.app.Notification;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
public class NotiActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private ArrayList<Notification> listNoti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listNoti = new ArrayList<>();

        // Tham chiếu đến node "users"
        mDatabase = FirebaseDatabase.getInstance().getReference("notification");

        // Đọc dữ liệu từ Realtime Database
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listNoti.clear(); // Xóa dữ liệu cũ

                // Lặp qua các child node và lấy dữ liệu vào danh sách
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Notification notification = snapshot.getValue(Notification.class);
                    listNoti.add(notification);
                }

                // Dữ liệu đã được lưu vào userList, bạn có thể sử dụng nó ở đây hoặc gọi một hàm khác để xử lý dữ liệu.
                // Ví dụ: updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
                Log.e("TAG", "Error: " + databaseError.getMessage());
            }
        });
    }
}


