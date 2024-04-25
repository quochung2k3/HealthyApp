package com.example.healthyapp.fragments;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.healthyapp.R;

public class ProfileFragment extends Fragment {
    View rootView;
    ImageView imgAvatar;
    EditText edtFirstname, edtLastname, edtEmail, edtPassword, edtPasswordConfirm;
    Button btnEdit, btnSave;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_profile, container, false);
        Mapping();
        return rootView;
    }

    private void Mapping() {
        imgAvatar = rootView.findViewById(R.id.imgAvatar);
        edtFirstname = rootView.findViewById(R.id.edtFirstname);
        edtLastname = rootView.findViewById(R.id.edtLastname);
        edtEmail = rootView.findViewById(R.id.edtEmail);
        edtPassword = rootView.findViewById(R.id.edtPassword);
        edtPasswordConfirm = rootView.findViewById(R.id.edtPasswordConfirm);
        btnEdit = rootView.findViewById(R.id.btnEdit);
        btnSave = rootView.findViewById(R.id.btnSave);

        ConstraintLayout infoFragment = rootView.findViewById(R.id.infoFragment);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;
        int newHeight = (int) ((811.0 / 891.0) * screenHeight);
        ViewGroup.LayoutParams layoutParams = infoFragment.getLayoutParams();
        layoutParams.height = newHeight;
        infoFragment.setLayoutParams(layoutParams);

        // img
        int imgWidth = (int) (screenWidth * 0.68);
        int imgHeight = (int) (newHeight * 0.18);
        imgAvatar.getLayoutParams().width = imgWidth;
        imgAvatar.getLayoutParams().height = imgHeight;

        // edt
        int edtWidth = (int) (screenWidth * 0.8);
        int edtHeight = (int) (newHeight * 0.062);
        edtFirstname.getLayoutParams().width = edtWidth;
        edtFirstname.getLayoutParams().height = edtHeight;
        edtLastname.getLayoutParams().width = edtWidth;
        edtLastname.getLayoutParams().height = edtHeight;
        edtEmail.getLayoutParams().width = edtWidth;
        edtEmail.getLayoutParams().height = edtHeight;
        edtPassword.getLayoutParams().width = edtWidth;
        edtPassword.getLayoutParams().height = edtHeight;
        edtPasswordConfirm.getLayoutParams().width = edtWidth;
        edtPasswordConfirm.getLayoutParams().height = edtHeight;

        // btn
        int btnWidth = (int) (screenWidth * 0.365);
        btnEdit.getLayoutParams().width = btnWidth;
        btnEdit.getLayoutParams().height = edtHeight;
        btnSave.getLayoutParams().width = btnWidth;
        btnSave.getLayoutParams().height = edtHeight;
    }
}
