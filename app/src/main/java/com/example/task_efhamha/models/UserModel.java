package com.example.task_efhamha.models;

public class UserModel {

    String uId;
    String name;
    String email;
    String image;

    public UserModel(String uId, String name, String email, String image) {
        this.uId = uId;
        this.name = name;
        this.email = email;
        this.image = image;
    }

    public UserModel() {
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
