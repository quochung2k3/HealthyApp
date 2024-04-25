package com.example.healthyapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.healthyapp.ChatActivity;
import com.example.healthyapp.R;
import com.example.healthyapp.adapter.ListNotiAdapter;
import com.example.healthyapp.models.ListNotiModel;
import com.example.healthyapp.models.NotiModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_notification, container, false);

        ListView lvNoti = rootView.findViewById(R.id.lvNotification);
        ArrayList<ListNotiModel> listNoti = new ArrayList<>();
        ListNotiAdapter listNotiAdapter = new ListNotiAdapter(getActivity(), listNoti);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
///////
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://healthyapp-bfba9-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference databaseReferenceMess = database.getReference().child("Notification");
        Log.d("HungTest", databaseReferenceMess.toString());
        databaseReferenceMess.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    NotiModel notiModel = dataSnapshot.getValue(NotiModel.class);
                    assert notiModel != null;
                    listNoti.add(new ListNotiModel(R.drawable.baseline_search_24, "Hung", notiModel.getContent()));

                }
                listNotiAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi", Toast.LENGTH_SHORT).show();
            }
        });

        lvNoti.setAdapter(listNotiAdapter);
        lvNoti.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListNotiModel selectedNoti = listNoti.get(position);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("userName", selectedNoti.getUserName());
                intent.putExtra("id", selectedNoti.getNotification());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        return rootView;
    }
}