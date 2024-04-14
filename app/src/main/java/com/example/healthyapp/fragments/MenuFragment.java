package com.example.healthyapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.healthyapp.R;
import com.example.healthyapp.SignInActivity;
import com.example.healthyapp.adapter.ListMenuAdapter;
import com.example.healthyapp.models.ListMenuModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MenuFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_menu, container, false);
        FrameLayout frameLayout = rootView.findViewById(R.id.userHome);
        Button btnLogout = rootView.findViewById(R.id.btnLogout);
        ListView lvMenu = rootView.findViewById(R.id.lvMenu);
        TextView txtUsername = rootView.findViewById(R.id.txtUsername);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        String uid = currentUser.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);
        String[] username = new String[1];
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String fName = document.getString("first_name");
                        String lName = document.getString("last_name");
                        username[0] = fName + " " + lName;
                        txtUsername.setText(username[0]);
                    }
                }
            }
        });
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("state", 0);
                bundle.putString("userName", username[0]);
                bundle.putString("id", uid);
                UserHomeFragment userHomeFragment = new UserHomeFragment();
                userHomeFragment.setArguments(bundle);
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.frame_layout, userHomeFragment);
                fragmentTransaction.commit();
            }
        });

        ArrayList<ListMenuModel> listMenu = new ArrayList<>();
        listMenu.add(new ListMenuModel(R.drawable.baseline_search_24, "Update Info"));
        listMenu.add(new ListMenuModel(R.drawable.baseline_search_24, "Saved"));
        listMenu.add(new ListMenuModel(R.drawable.baseline_admin_panel_settings_24, "Update Password"));
        ListMenuAdapter listMenuAdapter = new ListMenuAdapter(getActivity(), listMenu);
        lvMenu.setAdapter(listMenuAdapter);
        lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListMenuModel selectedItem = listMenu.get(position);
                if(selectedItem.getTitle().equals("Update Info")) {
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, new ProfileFragment());
                    fragmentTransaction.addToBackStack(null); // Để có thể quay lại Fragment trước đó nếu cần
                    fragmentTransaction.commit();
                }
                if(selectedItem.getTitle().equals("Update Password")) {
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, new UpdatePassFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_logout, null);
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
                bottomSheetDialog.setContentView(bottomSheetView);

                Button btnConfirmLogout = bottomSheetView.findViewById(R.id.btnConfirmLogout);
                btnConfirmLogout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getActivity(), SignInActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        bottomSheetDialog.dismiss();
                    }
                });

                Button btnCancelLogout = bottomSheetView.findViewById(R.id.btnCancelLogout);
                btnCancelLogout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.show();
            }
        });
        return rootView;
    }
}