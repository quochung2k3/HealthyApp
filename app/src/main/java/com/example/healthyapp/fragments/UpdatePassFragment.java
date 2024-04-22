package com.example.healthyapp.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.healthyapp.R;
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
        btnUpdate.setOnClickListener(v -> {
            String newPassword = edtNewPass.getText().toString();
            if(!edtNewPass.getText().toString().equals(edtConfirmPass.getText().toString())) {
                showErrorDialog("The confirmation password does not match the new password");
            }
            else {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    user.updatePassword(newPassword)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    showAnnouncementDialog();
                                } else {
                                    showErrorDialog("Password update failed");
                                }
                            });
                }
            }
        });
        return rootView;
    }

    private void showAnnouncementDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Thông báo");
        builder.setMessage("Updated password successfully");
        builder.setPositiveButton("OK", (dialog, id) -> {
            dialog.dismiss();
            edtNewPass.setText("");
            edtConfirmPass.setText("");
            edtNewPass.requestFocus();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Lỗi");
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}