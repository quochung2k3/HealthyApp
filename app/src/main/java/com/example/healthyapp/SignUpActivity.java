package com.example.healthyapp;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.healthyapp.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    Button btnSignUp, btnSignIn;
    EditText edtEmail, edtPass, edtFirstName, edtLastName;
    FirebaseAuth mAuth;
    FirebaseFirestore ft;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        ft = FirebaseFirestore.getInstance();
        Mapping();
        btnSignUp.setOnClickListener(v -> SignUp());
        btnSignIn.setOnClickListener((v -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        }));
    }

    private void Mapping() {
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignIn = findViewById(R.id.btnSignIn);
        edtEmail = findViewById(R.id.edtUsername);
        edtPass = findViewById(R.id.edtPassword);
        edtFirstName = findViewById(R.id.edtFirstname);
        edtLastName = findViewById(R.id.edtLastname);
    }

    private void SignUp() {
        String email = edtEmail.getText().toString();
        String password = edtPass.getText().toString();
        String firstName = edtFirstName.getText().toString();
        String lastName = edtLastName.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()) {
                        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                        System.out.println("Test " + userID);
                        DocumentReference documentReference = ft.collection("users").document(userID);
                        UserModel user = new UserModel();
                        user.setFirst_name(firstName);
                        user.setLast_name(lastName);
                        user.setEmail(email);
                        user.setPassword(password);
                        user.setImgAvatar("");
                        user.setImgBackground("");
                        documentReference.set(user)
                                .addOnSuccessListener(unused -> Toast.makeText(SignUpActivity.this, "Data added successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(SignUpActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        showAnnouncementDialog();
                    }
                    else {
                        showErrorDialog();
                    }
                });
    }
    private void showAnnouncementDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Announcement");
        builder.setMessage("Sign Up Success");
        FirebaseAuth.getInstance().signOut();
        builder.setPositiveButton("Go to the login page", (dialog, id) -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("Registration information is invalid, please check again");
        builder.setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}