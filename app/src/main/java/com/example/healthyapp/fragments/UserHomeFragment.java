package com.example.healthyapp.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthyapp.ChatActivity;
import com.example.healthyapp.R;
import com.example.healthyapp.SignInActivity;
import com.example.healthyapp.models.ListMessModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class UserHomeFragment extends Fragment {
    TextView txtUsername;
    Button btnChat;
    ImageView imgBack, imgAvatar;
    ArrayList<ListMessModel> listMess = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_home, container, false);
        txtUsername = rootView.findViewById(R.id.txtUsername);
        btnChat = rootView.findViewById(R.id.btnChat);
        imgBack = rootView.findViewById(R.id.back_button);
        imgAvatar = rootView.findViewById(R.id.imgAvatar);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                }
                else {
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            }
        });

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_logout, null);
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
                bottomSheetDialog.setContentView(bottomSheetView);

                Button btnConfirm = bottomSheetView.findViewById(R.id.btnConfirm);
                btnConfirm.setText("Upload Image");
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // check permission for camera
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 100);
                        }
                        // check permission for storage
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
                        }
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
                        }
                        bottomSheetDialog.dismiss();
                        pickImage();
                    }
                });

                Button btnCancel = bottomSheetView.findViewById(R.id.btnCancel);
                btnCancel.setText("Remove Image");
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.show();
            }
        });

        assert getArguments() != null;
        txtUsername.setText(getArguments().getString("userName"));
        String id = getArguments().getString("id");
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test UID: ", id);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("userName", txtUsername.getText().toString());
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        return rootView;
    }
    private static final int REQUEST_IMAGE_PICK = 1;
    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT); // To open gallery
        //intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE); // To open camera
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_PICK);
    }
}