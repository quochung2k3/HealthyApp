package com.example.healthyapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.healthyapp.DBConnetion.FirebaseDBConnection;
import com.example.healthyapp.models.CommentModel;
import com.example.healthyapp.models.FlairModel;
import com.example.healthyapp.models.LikeCommentModel;
import com.example.healthyapp.models.LikePostModel;
import com.example.healthyapp.models.MessageModel;
import com.example.healthyapp.models.PostImageModel;
import com.example.healthyapp.models.PostModel;
import com.example.healthyapp.models.UserModel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Tạo kết nối Firebase
        FirebaseDBConnection connection = FirebaseDBConnection.getInstance();

        // Thêm dữ liệu bảng Flair (Test)
        FlairModel flairModel = new FlairModel();
        flairModel.setName("Test");
        connection.setData(FirebaseDBConnection.FLAIR, flairModel);

        // Thêm dữ liệu bảng Comment (Test)
        CommentModel commentModel = new CommentModel();
        commentModel.setContent("I love u");
        commentModel.setLikes(10);
        commentModel.setParent_id("1");
        commentModel.setUser_id("1");
        commentModel.setPost_id("1");
        commentModel.setImage_link("Test");
        commentModel.setCreated_date(null);
        commentModel.setIs_deleted(false);
        commentModel.setModified_date(null);
        connection.setData(FirebaseDBConnection.COMMENT, commentModel);

        // Thêm dữ liệu bảng LikeComment (Test)
        LikeCommentModel likeCommentModel = new LikeCommentModel();
        likeCommentModel.setUser_id("1");
        likeCommentModel.setComment_id("1");
        connection.setData(FirebaseDBConnection.LIKE_COMMENT, likeCommentModel);

        // Thêm dữ liệu bảng LikePost (Test)
        LikePostModel likePostModel = new LikePostModel();
        likePostModel.setUser_id("1");
        likePostModel.setPost_id("1");
        connection.setData(FirebaseDBConnection.LIKE_POST, likePostModel);

        // Thêm dữ liệu bảng Message (Test)
        MessageModel messageModel = new MessageModel();
        messageModel.setSender_id("1");
        messageModel.setReceiver_id("2");
        messageModel.setContent("Hello");
        messageModel.setCreated_date(null);
        messageModel.setIs_deleted(false);
        connection.setData(FirebaseDBConnection.MESSAGE, messageModel);

        // Thêm dữ liệu bảng Post (Test)
        PostModel postModel = new PostModel();
        postModel.setTitle("Test");
        postModel.setContent("I'm Hưng");
        postModel.setLikes(100);
        postModel.setAnonymous(false);
        postModel.setUser_id("1");
        postModel.setFlair_id("1");
        postModel.setCreated_date(null);
        postModel.setIs_deleted(false);
        connection.setData(FirebaseDBConnection.POST, postModel);

        // Thêm dữ liệu bảng PostImage (Test)
        PostImageModel postImageModel = new PostImageModel();
        postImageModel.setId("1");
        postImageModel.setPost_id("1");
        postImageModel.setImage_link("test");
        connection.setData(FirebaseDBConnection.POST_IMAGE, postImageModel);

        // Thêm dữ liệu bảng User (Test)
        UserModel userModel = new UserModel();
        userModel.setId("1");
        userModel.setFirst_name("Phạm");
        userModel.setLast_name("Hưng");
        userModel.setEmail("pqh29052003@gmail.com");
        userModel.setPassword("123456");
        userModel.setCreated_date(null);
        userModel.setModified_date(null);
        connection.setData(FirebaseDBConnection.USER, userModel);
    }
}