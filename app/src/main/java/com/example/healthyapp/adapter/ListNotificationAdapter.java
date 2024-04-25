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

import com.example.healthyapp.R;
import com.example.healthyapp.models.ListNotiModel;

import java.util.ArrayList;

public class ListNotificationAdapter extends ArrayAdapter<ListNotiModel> {
    public ListNotificationAdapter(@NonNull Context context, ArrayList<ListNotiModel> listNotificationView) {
        super(context, 0, listNotificationView);
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        @SuppressLint("ViewHolder") View listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_notification, parent, false);
        ListNotiModel currentNotification = getItem(position);
        ImageView imageButton = listItemView.findViewById(R.id.imgAvatar);
        TextView userName = listItemView.findViewById(R.id.txtUsername);
        TextView notification = listItemView.findViewById(R.id.txtNoti);
        if (currentNotification != null) {
            imageButton.setImageResource(currentNotification.getImg());
            userName.setText(currentNotification.getUserName());
            notification.setText(currentNotification.getNoti());
        }
        return listItemView;
    }

}
