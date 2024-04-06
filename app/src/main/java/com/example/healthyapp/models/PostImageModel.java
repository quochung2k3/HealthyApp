package com.example.healthyapp.models;

public class PostImageModel {
    private int id;
    private int post_id;
    private String image_link;

    public PostImageModel(int id, int post_id, String image_link) {
        this.id = id;
        this.post_id = post_id;
        this.image_link = image_link;
    }

    public PostImageModel() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public String getImage_link() {
        return image_link;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }
}
