package com.example.healthyapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.healthyapp.ChatActivity;
import com.example.healthyapp.R;
import com.example.healthyapp.adapter.ListMessAdapter;
import com.example.healthyapp.models.ListMessModel;
import com.example.healthyapp.models.MessageModel;
import com.example.healthyapp.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

public class MessFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_list_mess, container, false);

        ListView lvMess = rootView.findViewById(R.id.lvMess);
        ArrayList<ListMessModel> listMess = new ArrayList<>();
        ListMessAdapter listMessAdapter = new ListMessAdapter(getActivity(), listMess);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");

        usersRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listMess.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String userId = documentSnapshot.getId();
                        assert firebaseUser != null;
                        String firstName = documentSnapshot.getString("first_name");
                        String lastName = documentSnapshot.getString("last_name");
                        if(!userId.equals(firebaseUser.getUid())) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance("https://healthyapp-bfba9-default-rtdb.asia-southeast1.firebasedatabase.app/");
                            DatabaseReference databaseReferenceMess = database.getReference().child("Message");
                            Log.d("HungTest", databaseReferenceMess.toString());
                            databaseReferenceMess.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                                        assert messageModel != null;
                                        if((messageModel.getSender_id().equals(firebaseUser.getUid()) && messageModel.getReceiver_id().equals(userId))
                                                || (messageModel.getSender_id().equals(userId) && messageModel.getReceiver_id().equals(firebaseUser.getUid()))) {
                                            listMess.add(new ListMessModel(R.drawable.baseline_search_24,
                                                    firstName + " " + lastName, "Hello", userId));
                                        }
                                    }
                                    listMessAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getContext(), "Lỗi", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(e -> {

                });
        lvMess.setAdapter(listMessAdapter);
        lvMess.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListMessModel selectedMess = listMess.get(position);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("userName", selectedMess.getUserName());
                intent.putExtra("id", selectedMess.getId());
                startActivity(intent);
            }
        });
        return rootView;
    }
}