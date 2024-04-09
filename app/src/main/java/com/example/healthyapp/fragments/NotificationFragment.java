package com.example.healthyapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthyapp.R;
import com.example.healthyapp.adapter.ListNotiAdapter;
import com.example.healthyapp.models.ListNotiModel;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_notification, container, false);

        ListView lvNoti = view.findViewById(R.id.lvNotification);
        ArrayList<ListNotiModel> listNoti = new ArrayList<>();
        listNoti.add(new ListNotiModel(R.drawable.baseline_search_24, "Quốc Hưng đã đăng 1 bài viết", "1 giờ"));
        listNoti.add(new ListNotiModel(R.drawable.baseline_search_24, "Đức Phú đã 1 đăng bài viết", "20 phút"));
        listNoti.add(new ListNotiModel(R.drawable.baseline_search_24, "Quốc Long đã đăng 1 bài viết", "30 phút"));
        ListNotiAdapter listMessAdapter = new ListNotiAdapter(requireContext(), listNoti);
        lvNoti.setAdapter(listMessAdapter);

        return view;
    }
}
