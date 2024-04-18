package com.example.healthyapp.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.healthyapp.ChatActivity;
import com.example.healthyapp.R;
import com.example.healthyapp.models.ListMessModel;
import com.example.healthyapp.services.FirebaseStorageService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class UserHomeFragment extends Fragment {
    TextView txtUsername;
    Button btnChat;
    ImageView imgBack, imgAvatar;
    ArrayList<ListMessModel> listMess = new ArrayList<>();
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    private Uri imageUri = null;
    Long timestamp = System.currentTimeMillis();
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_home, container, false);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        txtUsername = rootView.findViewById(R.id.txtUsername);
        btnChat = rootView.findViewById(R.id.btnChat);
        imgBack = rootView.findViewById(R.id.back_button);
        imgAvatar = rootView.findViewById(R.id.imgAvatar);
        assert getArguments() != null;
        txtUsername.setText(getArguments().getString("userName"));
        String id = getArguments().getString("id");
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
                if(id.equals(mAuth.getCurrentUser().getUid())) {
                    View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_logout, null);
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
                    bottomSheetDialog.setContentView(bottomSheetView);
                    Button btnConfirm = bottomSheetView.findViewById(R.id.btnConfirm);
                    btnConfirm.setText("Upload Image");
                    Button btnCancel = bottomSheetView.findViewById(R.id.btnCancel);
                    btnCancel.setText("Remove Image");
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

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        @Override
                        public void onClick(View v) {
                            imgAvatar.setImageDrawable(getResources().getDrawable(R.drawable.baseline_account_circle_24));
                            imageUri = null;
                            DocumentReference documentReference = firestore.collection("users").document(mAuth.getCurrentUser().getUid());
                            documentReference.update("imgAvatar", "")
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            // Xử lý thành công
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Xử lý thất bại
                                            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            bottomSheetDialog.dismiss();
                        }
                    });
                    bottomSheetDialog.show();
                }
            }
        });
        DocumentReference document = firestore.collection("users").document(id);
        document.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()) {
                    String linkImg = doc.getString("imgAvatar");
                    assert linkImg != null;
                    if(linkImg.equals("")) {
                        imgAvatar.setImageDrawable(getResources().getDrawable(R.drawable.baseline_account_circle_24));
                    }
                    else {
                        Glide.with(requireContext())
                                .load(linkImg)
                                .circleCrop()
                                .into(imgAvatar);
                    }
                }
            }
            else {

            }
        });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean isImageLoaded = false;
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK) {
                Uri selectedImageUri = data.getData();
                imageUri = selectedImageUri;

                Glide.with(this)
                        .load(selectedImageUri)
                        .circleCrop()
                        .into(imgAvatar);
                isImageLoaded = true;
                imgAvatar.setVisibility(View.VISIBLE);
            }
        }
        // Upload image to Firebase Storage
        FirebaseStorageService storageService = new FirebaseStorageService();
        if (isImageLoaded) {
            byte[] imageInByte = getBytesFromImageUri(imageUri);
            String extension = getFileExtension(imageUri);
            storageService.uploadImage(imageInByte, "post_" + timestamp + extension).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    DocumentReference documentReference = firestore.collection("users").document(mAuth.getCurrentUser().getUid());
                    documentReference.update("imgAvatar", downloadUri.toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    // Xử lý thành công
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Xử lý thất bại
                                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private byte[] getBytesFromImageUri(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = requireContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }
}