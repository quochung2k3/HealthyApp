package com.example.healthyapp.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;

import com.example.healthyapp.R;
import com.example.healthyapp.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdatePassFragment extends Fragment {
    ActivityMainBinding binding;
    EditText edtNewPass, edtConfirmPass;
    Button btnUpdate;
    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_update_pass, container, false);
        Mapping();
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

    private void Mapping() {
        edtNewPass = rootView.findViewById(R.id.edtNewPass);
        edtConfirmPass = rootView.findViewById(R.id.edtNewPassConfirm);
        btnUpdate = rootView.findViewById(R.id.btnUpdatePass);
        ConstraintLayout updatePassLayout = rootView.findViewById(R.id.updatePassFragment);
        // Lấy kích thước của màn hình
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int newHeight = (int) ((811.0 / 891.0) * screenHeight);
        ViewGroup.LayoutParams layoutParams = updatePassLayout.getLayoutParams();
        layoutParams.height = newHeight;
        updatePassLayout.setLayoutParams(layoutParams);
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