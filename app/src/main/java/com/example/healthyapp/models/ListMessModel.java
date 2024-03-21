package com.example.healthyapp.models;

public class ListMessModel {
    private int img;
    private String userName;
    private String mess;

    public ListMessModel() {

    }

    public ListMessModel(int img, String userName, String mess) {
        this.img = img;
        this.userName = userName;
        this.mess = mess;
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

    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }
}