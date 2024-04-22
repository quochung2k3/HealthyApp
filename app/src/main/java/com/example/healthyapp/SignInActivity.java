package com.example.healthyapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {
    Button btnSignIn, btnSignUp, btnResetPass;
    EditText edtEmail, edtPass;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null) {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        Mapping();
        mAuth = FirebaseAuth.getInstance();
        btnSignIn.setOnClickListener(v -> SignIn());
        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });
        btnResetPass.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, ResetPassActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("Invalid login information");
        builder.setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void SignIn() {
        String email = edtEmail.getText().toString();
        String password = edtPass.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        showErrorDialog();
                    }
                });
    }

    private void Mapping() {
        btnSignIn = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnResetPass = findViewById(R.id.btnResetPass);
        edtEmail = findViewById(R.id.edtUsername);
        edtPass = findViewById(R.id.edtPassword);
    }
}