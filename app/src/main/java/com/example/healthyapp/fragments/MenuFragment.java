package com.example.healthyapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.healthyapp.MainActivity;
import com.example.healthyapp.R;
import com.example.healthyapp.SignInActivity;
import com.example.healthyapp.SignUpActivity;
import com.example.healthyapp.adapter.ListMenuAdapter;
import com.example.healthyapp.adapter.ListMessAdapter;
import com.example.healthyapp.databinding.ActivityMainBinding;
import com.example.healthyapp.models.ListMenuModel;
import com.example.healthyapp.models.ListMessModel;

import java.util.ArrayList;

public class MenuFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_menu, container, false);

        ListView lvMenu = rootView.findViewById(R.id.lvMenu);
        ArrayList<ListMenuModel> listMenu = new ArrayList<>();
        listMenu.add(new ListMenuModel(R.drawable.baseline_search_24, "Update Info"));
        listMenu.add(new ListMenuModel(R.drawable.baseline_search_24, "Saved"));
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
            }
        });
        return rootView;
    }
}