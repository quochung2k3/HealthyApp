package com.example.healthyapp.models;

public class LikeCommentModel {
    private int user_id;
    private int comment_id;

    public LikeCommentModel(int user_id, int comment_id) {
        this.user_id = user_id;
        this.comment_id = comment_id;
    }

    public LikeCommentModel() {

    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }
}
