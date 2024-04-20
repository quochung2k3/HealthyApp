package com.example.healthyapp.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.healthyapp.ChatActivity;
import com.example.healthyapp.R;
import com.example.healthyapp.adapter.ListMessAdapter;
import com.example.healthyapp.models.ListMessModel;
import com.example.healthyapp.models.MessageModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class MessFragment extends Fragment {
    FirebaseFirestore firestore;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    ListMessAdapter listMessAdapter = null;
    ListView lvMess = null;
    ArrayList<ListMessModel> listMess = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_list_mess, container, false);
        firestore = FirebaseFirestore.getInstance();
        lvMess = rootView.findViewById(R.id.lvMess);
        listMessAdapter = new ListMessAdapter(getActivity(), listMess);

        reloadDataFromFirebase();

        lvMess.setAdapter(listMessAdapter);
        lvMess.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListMessModel selectedMess = listMess.get(position);
                Log.d("ABCXYZ", selectedMess.getId());
                DocumentReference document = firestore.collection("users").document(selectedMess.getId());
                document.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            String linkImg = doc.getString("imgAvatar");
                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                            intent.putExtra("linkImg", linkImg);
                            intent.putExtra("userName", selectedMess.getUserName());
                            intent.putExtra("id", selectedMess.getId());
                            startActivity(intent);
                        }
                    }
                });
            }
        });

        lvMess.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ListMessModel selectedMess = listMess.get(position);
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View bottomSheetView = inflater.inflate(R.layout.bottom_sheet_logout, null);
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                bottomSheetDialog.setContentView(bottomSheetView);
                Button btnConfirm = bottomSheetView.findViewById(R.id.btnConfirm);
                btnConfirm.setText("Delete All Mess");
                Button btnCancel = bottomSheetView.findViewById(R.id.btnCancel);
                btnCancel.setText("Cancel");
                bottomSheetDialog.show();
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAnnouncementDialog(selectedMess.getId(), position);
                        Log.d("TEST UID MESS", selectedMess.getId());
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
            }
        });
        return rootView;
    }
    private void showAnnouncementDialog(String id, int position) {
        String myId = firebaseUser.getUid();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thông báo");
        builder.setMessage("Tin nhắn sau khi xoá không thể phục hồi được, bạn có chắc chắn muốn xoá?");
        builder.setPositiveButton("Xoá", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                updateMessageIsDeleted(id, myId, position);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateMessageIsDeleted(String id, String myId, int position) {
        DatabaseReference messagesRef = FirebaseDatabase.getInstance("https://healthyapp-bfba9-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Message");
        messagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MessageModel message = snapshot.getValue(MessageModel.class);

                    if (message != null) {
                        if ((message.getReceiver_id().equals(myId) && message.getSender_id().equals(id)) ||
                                (message.getReceiver_id().equals(id) && message.getSender_id().equals(myId))) {
                            if(message.getSender_id().equals(myId)) {
                                snapshot.getRef().child("is_deleted_by_me").setValue(true);
                            }
                            if(message.getReceiver_id().equals(myId)) {
                                snapshot.getRef().child("is_deleted_by_other").setValue(true);
                            }
                        }
                    }
                }
                reloadDataFromFirebase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void reloadDataFromFirebase() {
        listMess.clear();
        ArrayList<String> testList = new ArrayList<>();
        HashSet<String> seen = new HashSet<>();
        Log.d("TEST LIST", testList.toString());
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://healthyapp-bfba9-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference databaseReferenceMess = database.getReference().child("Message");
        databaseReferenceMess.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    if (messageModel != null) {
                        String UID = null;
                        if(messageModel.getSender_id().equals(firebaseUser.getUid()) && messageModel.getReceiver_id().equals(firebaseUser.getUid())) {
                            UID = firebaseUser.getUid();
                        }
                        else if(messageModel.getSender_id().equals(firebaseUser.getUid())) {
                            UID = messageModel.getReceiver_id();
                        }
                        else if(messageModel.getReceiver_id().equals(firebaseUser.getUid())) {
                            UID = messageModel.getSender_id();
                        }
                        if(UID != null) {
                            testList.add(UID);
                        }
                    }
                }
                Collections.reverse(testList);
                for (int i = 0; i < testList.size(); i++) {
                    String item = testList.get(i);
                    if (seen.contains(item)) {
                        testList.remove(i);
                        i--;
                    }
                    else {
                        seen.add(item);
                    }
                }
                Log.d("TEST LIST", testList.toString());
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                for (String userId : testList) {
                    CollectionReference usersRef = db.collection("users");
                    usersRef.document(userId.trim()).get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        String firstName = document.getString("first_name");
                                        String lastName = document.getString("last_name");
                                        String imgLink = document.getString("imgAvatar");
                                        listMess.add(new ListMessModel(imgLink,
                                                firstName + " " + lastName, "Hello", userId));
                                    }
                                    else {

                                    }
                                } else {
                                    Exception e = task.getException();
                                }
                            });
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