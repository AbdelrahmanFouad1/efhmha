package com.example.task_efhamha.models;

public class PostModel {

    private String image;
    private String descImage;
    private String title;
    private String body;
    private long time;
    private String postId;

    private UserModel userModel;

    public PostModel(String image, String descImage, String title, String body, long time, String postId, UserModel userModel) {
        this.image = image;
        this.descImage = descImage;
        this.title = title;
        this.body = body;
        this.time = time;
        this.postId = postId;
        this.userModel = userModel;
    }

    public PostModel() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescImage() {
        return descImage;
    }

    public void setDescImage(String descImage) {
        this.descImage = descImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }
}
