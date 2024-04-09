package com.example.healthyapp.models;

import java.sql.Timestamp;

public class PostModel {
    private String id;
    private String title;
    private String content;
    private int likes;
    private boolean anonymous;
    private String user_id;
    private String flair_id;
    private Timestamp created_date;
    private boolean is_deleted;

    public PostModel(String id, String title, String content, int likes, boolean anonymous, String user_id, String flair_id, Timestamp created_date, boolean is_deleted) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.likes = likes;
        this.anonymous = anonymous;
        this.user_id = user_id;
        this.flair_id = flair_id;
        this.created_date = created_date;
        this.is_deleted = is_deleted;
    }

    public PostModel(String title, String content, int likes, boolean anonymous, String user_id, String flair_id, Timestamp created_date) {
        this.title = title;
        this.content = content;
        this.likes = likes;
        this.anonymous = anonymous;
        this.user_id = user_id;
        this.flair_id = flair_id;
        this.created_date = created_date;
    }

    public PostModel() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFlair_id() {
        return flair_id;
    }

    public void setFlair_id(String flair_id) {
        this.flair_id = flair_id;
    }

    public Timestamp getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Timestamp created_date) {
        this.created_date = created_date;
    }

    public boolean isIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }
}
