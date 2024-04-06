package com.example.healthyapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.healthyapp.R;
import com.example.healthyapp.adapter.ListMessAdapter;
import com.example.healthyapp.models.ListMessModel;

import java.util.ArrayList;

public class MessFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_list_mess, container, false);

        ListView lvMess = rootView.findViewById(R.id.lvMess);
        ArrayList<ListMessModel> listMess = new ArrayList<>();
        listMess.add(new ListMessModel(R.drawable.baseline_search_24, "Pham Quoc Hung", "Hello"));
        listMess.add(new ListMessModel(R.drawable.baseline_search_24, "Vinh", "Hi"));
        ListMessAdapter listMessAdapter = new ListMessAdapter(getActivity(), listMess);
        lvMess.setAdapter(listMessAdapter);

        return rootView;
    }
}