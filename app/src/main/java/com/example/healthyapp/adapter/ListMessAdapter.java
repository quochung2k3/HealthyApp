package com.example.healthyapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.healthyapp.R;
import com.example.healthyapp.models.ListMessModel;

import java.util.ArrayList;

public class ListMessAdapter extends ArrayAdapter<ListMessModel> {

    public ListMessAdapter(@NonNull Context context, ArrayList<ListMessModel> listMessView) {
        super(context, 0, listMessView);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @NonNull
    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_mess_custom, parent, false);
        }
        ListMessModel currentMess = getItem(position);
        ImageView imageButton = listItemView.findViewById(R.id.imgAvatar);
        TextView userName = listItemView.findViewById(R.id.txtUsername);
        TextView mess = listItemView.findViewById(R.id.txtMess);
        if (currentMess != null) {
            if(currentMess.getImg().equals("")) {
                imageButton.setImageDrawable(getContext().getResources().getDrawable(R.drawable.baseline_account_circle_24));
            }
            else {
                Glide.with(getContext())
                        .load(currentMess.getImg())
                        .circleCrop()
                        .into(imageButton);
            }
            userName.setText(currentMess.getUserName());
            mess.setText(currentMess.getMess());
        }
        return listItemView;
    }
}