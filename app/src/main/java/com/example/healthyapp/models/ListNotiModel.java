package com.example.healthyapp.models;

public class ListNotiModel {
    private int img;
    private String userName;
    private String notification;
    public ListNotiModel() {

    }
    public ListNotiModel(int img, String userName, String notification) {
        this.img = img;
        this.userName = userName;
        this.notification = notification;
    }

    public int getImg() {
        return img;
    }
    public void setImg(int img) {
        this.img = img;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNotification() {
        return notification;
    }

}
