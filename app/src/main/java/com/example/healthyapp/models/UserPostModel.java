package com.example.healthyapp.models;

public class UserPostModel {
    private int img;
    private String userName;
    private String postTitle;
    private int postImg;

    public UserPostModel(int img, String userName, String postTitle, int postImg) {
        this.img = img;
        this.userName = userName;
        this.postTitle = postTitle;
        this.postImg = postImg;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public int getPostImg() {
        return postImg;
    }

    public void setPostImg(int postImg) {
        this.postImg = postImg;
    }

    public UserPostModel(int img, String userName) {
        this.img = img;
        this.userName = userName;
    }

    public UserPostModel() {
    }

    public int getImg() {
        return img;
    }

    public String getUserName() {
        return userName;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
