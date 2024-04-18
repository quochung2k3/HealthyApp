package com.example.healthyapp.fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
                    showErrorDialog("Mật khẩu xác nhận không khớp với mật khẩu mới");
                }
                else {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        user.updatePassword(newPassword)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            showAnnouncementDialog("Cập nhật mật khẩu thành công");
                                        } else {
                                            showErrorDialog("Cập nhật mật khẩu thất bại");
                                        }
                                    }
                                });
                    }
                }
            }
        });
        return rootView;
    }

    private void showAnnouncementDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thông báo");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                edtNewPass.setText("");
                edtConfirmPass.setText("");
                edtNewPass.requestFocus();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Lỗi");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}