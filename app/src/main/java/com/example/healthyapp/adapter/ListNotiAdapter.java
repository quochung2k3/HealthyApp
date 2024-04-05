package com.example.healthyapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.healthyapp.R;
import com.example.healthyapp.models.ListNotiModel;

import java.util.ArrayList;

public class ListNotiAdapter extends ArrayAdapter<ListNotiModel> {
    public ListNotiAdapter(@NonNull Context context, ArrayList<ListNotiModel> listNotiView) {
        super(context, 0, listNotiView);
    }
    @NonNull
    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_notification, parent, false);
        }
        ListNotiModel currentNoti = getItem(position);
        ImageView imageButton = listItemView.findViewById(R.id.imgAvatar);
        TextView userName = listItemView.findViewById(R.id.txtUsername);
        TextView noti = listItemView.findViewById(R.id.txtNoti);
        if (currentNoti != null) {
            imageButton.setImageResource(currentNoti.getImg());
            userName.setText(currentNoti.getUserName());
            noti.setText(currentNoti.getNoti());
        }
        return listItemView;
    }

}
