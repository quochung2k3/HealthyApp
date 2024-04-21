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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
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
import com.example.healthyapp.DBConnetion.FirebaseDBConnection;
import com.example.healthyapp.R;
import com.example.healthyapp.adapter.UserPostAdapter;
import com.example.healthyapp.models.ListMessModel;
import com.example.healthyapp.models.PostModel;
import com.example.healthyapp.services.FirebaseStorageService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Objects;

public class UserHomeFragment extends Fragment {
    TextView txtUsername;
    Button btnChat;
    RecyclerView rvUserPost;
    UserPostAdapter userPostAdapter;
    ArrayList<PostModel> postList = new ArrayList<>();
    ArrayList<ListMessModel> listMess = new ArrayList<>();
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    FirebaseDatabase db = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL);
    ImageView imgBack, imgAvatar, imgBackground;
    FirebaseFirestore ft;
    private Uri imageUri = null;
    Long timestamp = System.currentTimeMillis();
    String field = null;
    @SuppressLint({"UseCompatLoadingForDrawables", "MissingInflatedId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_home, container, false);
        mAuth = FirebaseAuth.getInstance();
        ft = FirebaseFirestore.getInstance();
        txtUsername = rootView.findViewById(R.id.txtUsername);
        btnChat = rootView.findViewById(R.id.btnChat);
        imgBack = rootView.findViewById(R.id.back_button);
        imgAvatar = rootView.findViewById(R.id.imgAvatar);
        imgBackground = rootView.findViewById(R.id.imgBackground);
        assert getArguments() != null;
        txtUsername.setText(getArguments().getString("userName"));
        String id = getArguments().getString("id");
        imgBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
            else {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
        imgBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                field = "imgBackground";
                assert id != null;
                if(id.equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
                    solve();
                }
            }
        });
        rvUserPost = rootView.findViewById(R.id.rvUserPost);
        userPostAdapter = new UserPostAdapter(getContext(), postList);
        rvUserPost.setAdapter(userPostAdapter);
        rvUserPost.setLayoutManager(new LinearLayoutManager(getContext()));
        loadPost(id);
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                field = "imgAvatar";
                assert id != null;
                if(id.equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
                    solve();
                }
            }
        });

        assert id != null;
        DocumentReference document = ft.collection("users").document(id);
        document.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()) {
                    String linkImg = doc.getString("imgAvatar");
                    String linkImgBackground = doc.getString("imgBackground");
                    assert linkImg != null;
                    if(linkImg.isEmpty()) {
                        imgAvatar.setImageDrawable(getResources().getDrawable(R.drawable.baseline_account_circle_24));
                    }
                    else {
                        Glide.with(requireContext())
                                .load(linkImg)
                                .circleCrop()
                                .into(imgAvatar);
                    }
                    assert linkImgBackground != null;
                    if(linkImgBackground.isEmpty()) {
                        imgBackground.setImageDrawable(getResources().getDrawable(R.drawable.background));
                    }
                    else {
                        Glide.with(requireContext())
                                .load(linkImgBackground)
                                .into(imgBackground);
                    }
                }
            }
        });
        btnChat.setOnClickListener(v -> {
            DocumentReference document1 = ft.collection("users").document(id);
            document1.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        String linkImg = doc.getString("imgAvatar");
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra("linkImg", linkImg);
                        intent.putExtra("userName", txtUsername.getText().toString());
                        intent.putExtra("id", id);
                        startActivity(intent);
                    }
                }
            });
        });
        return rootView;
    }

    private void loadPost(String id) {
        // get post by user id
        Query query = db.getReference("Post").orderByChild("user_id").equalTo(id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    PostModel post = postSnapshot.getValue(PostModel.class);
                    post.setId(postSnapshot.getKey());
                    postList.add(post);
                }
                userPostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                if(field.equals("imgAvatar")) {
                    Glide.with(this)
                            .load(selectedImageUri)
                            .circleCrop()
                            .into(imgAvatar);
                    isImageLoaded = true;
                    imgAvatar.setVisibility(View.VISIBLE);
                }
                if(field.equals("imgBackground")) {
                    Glide.with(this)
                            .load(selectedImageUri)
                            .into(imgBackground);
                    isImageLoaded = true;
                    imgBackground.setVisibility(View.VISIBLE);
                }
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
                    DocumentReference documentReference = ft.collection("users").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                    documentReference.update(field, downloadUri.toString())
                            .addOnSuccessListener(unused -> {

                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private byte[] getBytesFromImageUri(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
            ByteArrayOutputStream bass = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bass);
            return bass.toByteArray();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = requireContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    private void solve() {
        @SuppressLint("InflateParams") View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_logout, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(bottomSheetView);
        Button btnConfirm = bottomSheetView.findViewById(R.id.btnConfirm);
        btnConfirm.setText("Upload Image");
        Button btnCancel = bottomSheetView.findViewById(R.id.btnCancel);
        btnCancel.setText("Remove Image");
        btnConfirm.setOnClickListener(v1 -> {
            // check permission for camera
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, 100);
            }
            // check permission for storage
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
            }
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
            }
            bottomSheetDialog.dismiss();
            pickImage();
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                if(field.equals("imgAvatar")) {
                    imgAvatar.setImageDrawable(getResources().getDrawable(R.drawable.baseline_account_circle_24));
                }
                else {
                    imgBackground.setImageDrawable(getResources().getDrawable(R.drawable.background));
                }
                imageUri = null;
                DocumentReference documentReference = ft.collection("users").document(mAuth.getCurrentUser().getUid());
                documentReference.update(field, "")
                        .addOnSuccessListener(unused -> {

                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();
    }
}