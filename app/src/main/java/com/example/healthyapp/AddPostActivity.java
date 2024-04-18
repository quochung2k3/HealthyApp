package com.example.healthyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.example.healthyapp.DBConnetion.FirebaseDBConnection;
import com.example.healthyapp.models.FlairModel;
import com.example.healthyapp.models.PostModel;
import com.example.healthyapp.services.FirebaseStorageService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddPostActivity extends AppCompatActivity {
    ImageButton ibBack, ibRemoveImage;
    Button btnAddPost, btnAddImage;
    TextView tvTitle, tvContent;
    ImageView ivImage;
    FrameLayout loadingOverlay;
    Switch swAnonymous;
    Spinner spFlair;
    List<FlairModel> flairs = new ArrayList<>();
    ArrayAdapter<FlairModel> adapter = null;
    FirebaseDBConnection db = null;
    private Uri imageUri = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseDBConnection.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        btnAddPost = findViewById(R.id.btnPost);
        btnAddImage = findViewById(R.id.btnAddImage);
        tvTitle = findViewById(R.id.edtTitle);
        tvContent = findViewById(R.id.edtContent);
        ivImage = findViewById(R.id.ivImage);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        ibRemoveImage = findViewById(R.id.ibRemoveImage);
        ibBack = findViewById(R.id.ibBack);
        swAnonymous = findViewById(R.id.swAnonymous);
        spFlair = findViewById(R.id.spFlair);
        getFlairs();

        ibBack.setOnClickListener(v -> {
            // set result code
            setResult(RESULT_CANCELED);
            finish();
        });
        ibRemoveImage.setOnClickListener(v -> {
            ivImage.setImageDrawable(null);
            ivImage.setVisibility(ImageView.GONE);
            ibRemoveImage.setVisibility(ImageView.GONE);
            imageUri = null;
        });
        btnAddImage.setOnClickListener(v -> {
            // check permission for camera
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
            }
            // check permission for storage
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
            }
            pickImage();
        });
        btnAddPost.setOnClickListener(v -> {
            loadingOverlay.setVisibility(FrameLayout.VISIBLE);

            String title = tvTitle.getText().toString();
            String content = tvContent.getText().toString();
            if (title.isEmpty()) {
                return;
            }
            boolean anonymous = swAnonymous.isChecked();
            String flairId = flairs.get(spFlair.getSelectedItemPosition()).getId();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = user.getUid();
            Long timestamp = System.currentTimeMillis();

            // Add post to database
            PostModel post = new PostModel(title, content, anonymous, uid, flairId, timestamp);


            // Upload image to Firebase Storage
            FirebaseStorageService storageService = new FirebaseStorageService();
            if (ivImage.getDrawable() != null) {
                byte[] imageInByte = getBytesFromImageUri(imageUri);
                String extension = getFileExtension(imageUri);
                storageService.uploadImage(imageInByte, "post_" + timestamp + extension).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        post.setPostImg(downloadUri.toString());
                        db.setData(FirebaseDBConnection.POST, post);
                        finish();

                    } else {
                        Toast.makeText(AddPostActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        loadingOverlay.setVisibility(FrameLayout.GONE);
                    }
                });
            } else {
                db.setData(FirebaseDBConnection.POST, post);
                finish();
            }

        });
    }
    private byte[] getBytesFromImageUri(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(imageUri));
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK) {
                // Get image from gallery
                Uri selectedImageUri = data.getData();
                imageUri = selectedImageUri;
                ivImage.setImageURI(selectedImageUri);
                ivImage.setVisibility(ImageView.VISIBLE);
                ibRemoveImage.setVisibility(ImageView.VISIBLE);
            }
        }
    }
    private void getFlairs() {
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        spFlair.setAdapter(adapter);

        db.readData(FirebaseDBConnection.FLAIR, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adapter.clear();
                Object value = snapshot.getValue();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FlairModel flair = dataSnapshot.getValue(FlairModel.class);
                    flair.setId(dataSnapshot.getKey());
                    // log flairs
                    Log.d("flair", flair.getName());
                    flairs.add(flair);
                }
                // add flairs to spinner
                adapter.addAll(flairs);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddPostActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}