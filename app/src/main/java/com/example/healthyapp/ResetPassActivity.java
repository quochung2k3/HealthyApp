package com.example.healthyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassActivity extends AppCompatActivity {
    EditText edtEmail;
    Button btnResetPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        Mapping();
        btnResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String emailAddress = edtEmail.getText().toString();

                auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ResetPassActivity.this, "Đã gởi đến địa chỉ email của bạn", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ResetPassActivity.this, SignInActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });
    }

    private void Mapping() {
        edtEmail = findViewById(R.id.edtEmail);
        btnResetPass = findViewById(R.id.btnResetPass);
    }
}