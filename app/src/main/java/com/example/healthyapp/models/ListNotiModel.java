package com.example.healthyapp.models;

public class ListNotiModel {
    private int img;
    private String userName;
    private String noti;
    public ListNotiModel() {

    }
    public ListNotiModel(int img, String userName, String noti) {
        this.img = img;
        this.userName = userName;
        this.noti = noti;
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

    public String getNoti() {
        return noti;
    }

    public void setMess(String noti) {
        this.noti = noti;
    }
}
