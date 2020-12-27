package com.example.task_efhamha.models;

public class CommentModel {

    private String commentId;
    private String comment;

    private PostModel postModel;

    private UserModel userModel;

    private String postId;

    public CommentModel() {
    }

    public CommentModel(String commentId, String comment, PostModel postModel) {
        this.commentId = commentId;
        this.comment = comment;
        this.postModel = postModel;
    }

    public CommentModel(String commentId, String comment, UserModel userModel) {
        this.commentId = commentId;
        this.comment = comment;
        this.userModel = userModel;
    }

    public CommentModel(String commentId, String comment, UserModel userModel, String postId) {
        this.commentId = commentId;
        this.comment = comment;
        this.userModel = userModel;
        this.postId = postId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public PostModel getPostModel() {
        return postModel;
    }

    public void setPostModel(PostModel postModel) {
        this.postModel = postModel;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
