package com.example.healthyapp.services;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FirebaseStorageService {
    private static final String TAG = "StorageService";
    private FirebaseStorage storage;
    private StorageReference storageRef;

    public FirebaseStorageService() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }
    public Task<Uri> uploadImage(byte[] image, String imageName) {
        StorageReference imageRef = storageRef.child("images/" + imageName);
        return imageRef.putBytes(image)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return imageRef.getDownloadUrl();
                });
    }

    // Upload image to Firebase Storage
    public Task<Uri> uploadImage(String imagePath, String imageName) {
        StorageReference imageRef = storageRef.child("images/" + imageName);
        File file = new File(imagePath);

        try {
            InputStream stream = new FileInputStream(file);
            UploadTask uploadTask = imageRef.putStream(stream);

            // Register observers to listen for when the download is done or if it fails
            return uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    Log.d("Image", imageRef.getDownloadUrl().toString());

                    // Continue with the task to get the download URL
                    return imageRef.getDownloadUrl();
                }
            });
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.getMessage());
            return null;
        }
    }

    // Download image from Firebase Storage
    public Task<Uri> downloadImage(String imageName) {
        StorageReference imageRef = storageRef.child("images/" + imageName);
        return imageRef.getDownloadUrl();
    }
}
