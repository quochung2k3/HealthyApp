package com.example.healthyapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.healthyapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdatePassFragment extends Fragment {
    EditText edtNewPass, edtConfirmPass;
    Button btnUpdate;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_update_pass, container, false);

        edtNewPass = rootView.findViewById(R.id.edtNewPass);
        edtConfirmPass = rootView.findViewById(R.id.edtNewPassConfirm);
        btnUpdate = rootView.findViewById(R.id.btnUpdatePass);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = edtNewPass.getText().toString();
                if(!edtNewPass.getText().toString().equals(edtConfirmPass.getText().toString())) {
                    Toast.makeText(getContext(), "Mật khẩu xác nhận không khớp với mật khẩu mới", Toast.LENGTH_SHORT).show();
                }
                else {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        user.updatePassword(newPassword)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Cập nhật mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getContext(), "Cập nhật mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            }
        });
        return rootView;
    }
}