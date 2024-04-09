package com.example.healthyapp.models;

import java.sql.Timestamp;

public class NotiModel {
    private int user_id;
    private String img;
    private String content;
    private Timestamp going_date_time;

    public NotiModel(int user_id, String img, String content, Timestamp going_date_time) {
        this.user_id = user_id;
        this.img = img;
        this.content = content;
        this.going_date_time = going_date_time;
    }

    public NotiModel() {

    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getGoing_date_time() {
        return going_date_time;
    }

    public void setGoing_date_time(Timestamp going_date_time) {
        this.going_date_time = going_date_time;
    }
}
