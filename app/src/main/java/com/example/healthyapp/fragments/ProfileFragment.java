package com.example.healthyapp.fragments;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.healthyapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {
    View rootView;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    ImageView imgAvatar;
    EditText edtFirstname, edtLastname, edtAddress, edtSDT, edtBirthDay;
    Button btnEdit, btnSave;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        Mapping();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(firebaseUser.getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String firstName = document.getString("first_name");
                        String lastName = document.getString("last_name");
                        edtFirstname.setText(firstName);
                        edtLastname.setText(lastName);
                    }
                }
            }
        });
        return rootView;
    }

    private void Mapping() {
        imgAvatar = rootView.findViewById(R.id.imgAvatar);
        edtFirstname = rootView.findViewById(R.id.edtFirstname);
        edtLastname = rootView.findViewById(R.id.edtLastname);
        edtAddress = rootView.findViewById(R.id.edtAddress);
        edtSDT = rootView.findViewById(R.id.edtSDT);
        edtBirthDay = rootView.findViewById(R.id.edtBirthDay);
        btnEdit = rootView.findViewById(R.id.btnEdit);
        btnSave = rootView.findViewById(R.id.btnSave);

        ConstraintLayout infoFragment = rootView.findViewById(R.id.infoFragment);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;
        int newHeight = (int) ((811.0 / 891.0) * screenHeight);
        ViewGroup.LayoutParams layoutParams = infoFragment.getLayoutParams();
        layoutParams.height = newHeight;
        infoFragment.setLayoutParams(layoutParams);

        // img
        int imgWidth = (int) (screenWidth * 0.68);
        int imgHeight = (int) (newHeight * 0.18);
        imgAvatar.getLayoutParams().width = imgWidth;
        imgAvatar.getLayoutParams().height = imgHeight;

        // edt
        int edtWidth = (int) (screenWidth * 0.8);
        int edtHeight = (int) (newHeight * 0.062);
        edtFirstname.getLayoutParams().width = edtWidth;
        edtFirstname.getLayoutParams().height = edtHeight;
        edtLastname.getLayoutParams().width = edtWidth;
        edtLastname.getLayoutParams().height = edtHeight;
        edtAddress.getLayoutParams().width = edtWidth;
        edtAddress.getLayoutParams().height = edtHeight;
        edtSDT.getLayoutParams().width = edtWidth;
        edtSDT.getLayoutParams().height = edtHeight;
        edtBirthDay.getLayoutParams().width = edtWidth;
        edtBirthDay.getLayoutParams().height = edtHeight;

        // btn
        int btnWidth = (int) (screenWidth * 0.365);
        btnEdit.getLayoutParams().width = btnWidth;
        btnEdit.getLayoutParams().height = edtHeight;
        btnSave.getLayoutParams().width = btnWidth;
        btnSave.getLayoutParams().height = edtHeight;
    }
}
