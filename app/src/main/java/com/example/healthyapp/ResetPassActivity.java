package com.example.healthyapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ResetPassActivity extends AppCompatActivity {
    EditText edtEmail;
    Button btnResetPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        Mapping();
        btnResetPass.setOnClickListener(v -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String emailAddress = edtEmail.getText().toString();

            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            showAnnouncementDialog();
                        }
                    });
        });
    }

    private void showAnnouncementDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Announcement");
        builder.setMessage("The password reset link has been sent to your email address");
        builder.setPositiveButton("Go to the login page", (dialog, id) -> {
            Intent intent = new Intent(ResetPassActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void Mapping() {
        edtEmail = findViewById(R.id.edtEmail);
        btnResetPass = findViewById(R.id.btnResetPass);
    }
}