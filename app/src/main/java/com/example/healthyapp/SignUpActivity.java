package com.example.healthyapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    Button btnSignUp;
    EditText edtEmail, edtPass, edtFirstName, edtLastName;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        Mapping();
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUp();
            }
        });
    }

    private void Mapping() {
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        edtEmail = (EditText) findViewById(R.id.edtUsername);
        edtPass = (EditText) findViewById(R.id.edtPassword);
        edtFirstName = (EditText) findViewById(R.id.edtFirstname);
        edtLastName = (EditText) findViewById(R.id.edtLastname);
    }

    private void SignUp() {
        String email = edtEmail.getText().toString();
        String password = edtPass.getText().toString();
        String firstName = edtFirstName.getText().toString();
        String lastName = edtLastName.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                            System.out.println("Test " + userID);
                            DocumentReference documentReference = firestore.collection("users").document(userID);
                            Map<String, String> user = new HashMap<>();
                            user.put("fName", firstName);
                            user.put("lName", lastName);
                            documentReference.set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(SignUpActivity.this, "Data added successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignUpActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            Toast.makeText(SignUpActivity.this, "Đăng kí thành công", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(SignUpActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}