package com.example.healthyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ListView;

import com.example.healthyapp.adapter.ListMessAdapter;
import com.example.healthyapp.models.ListMessModel;

import java.util.ArrayList;

public class ListMessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_mess);
        ListView lvMess = (ListView) findViewById(R.id.lvMess);
        ArrayList<ListMessModel> listMess = new ArrayList<>();
        listMess.add(new ListMessModel(R.drawable.baseline_search_24, "Pham Quoc Hung", "Hello"));
        listMess.add(new ListMessModel(R.drawable.baseline_search_24, "Vinh", "Hi"));
        ListMessAdapter listMessAdapter = new ListMessAdapter(this, listMess);
        lvMess.setAdapter(listMessAdapter);
    }
}