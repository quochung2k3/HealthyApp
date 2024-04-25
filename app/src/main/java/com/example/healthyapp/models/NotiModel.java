package com.example.healthyapp.models;

import java.sql.Timestamp;

public class NotiModel {
    private String user_id;
    private String userName;
    private String img;
    private String content;
    private Timestamp created_date;

    public NotiModel(String user_id, String userName, String img, String content, Timestamp created_date) {
        this.user_id = user_id;
        this.userName = userName;
        this.img = img;
        this.content = content;
        this.created_date = created_date;
    }

    public NotiModel() {

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUserName()
    {
        return userName;
    }
    public void setUserName(String userName)
    {
        this.userName =userName;
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

    public Timestamp getCreated_date() {
        return created_date;
    }

    public void setGoing_date_time(Timestamp created_date) {
        this.created_date = created_date;
    }
}