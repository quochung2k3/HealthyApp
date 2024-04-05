package com.example.healthyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.example.healthyapp.adapter.ListNotiAdapter;
import com.example.healthyapp.models.ListNotiModel;

import java.util.ArrayList;

public class Notification extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ListView lvNoti = (ListView) findViewById(R.id.lvNotification);
        ArrayList<ListNotiModel> listNoti = new ArrayList<>();
        listNoti.add(new ListNotiModel(R.drawable.baseline_search_24, "Quốc Hưng đã đăng 1 bài viết", "1 giờ"));
        listNoti.add(new ListNotiModel(R.drawable.baseline_search_24, "Đức Phú đã 1 đăng bài viết", "20 phút"));
        listNoti.add(new ListNotiModel(R.drawable.baseline_search_24, "Quốc Long đã đăng 1 bài viết", "30 phút"));
        ListNotiAdapter listMessAdapter = new ListNotiAdapter(this, listNoti);
        lvNoti.setAdapter(listMessAdapter);
    }
}