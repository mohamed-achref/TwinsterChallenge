package com.khattech.twinsterchallenge.models;

import android.net.Uri;

/**
 * Created by Mohamed Achref on 4/30/20.
 */
public class User {

    private String image;
    private String name;
    private String email;
    private Uri imageUri;

    public User(String image, String name, String email) {
        this.image = image;
        this.name = name;
        this.email = email;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    @Override
    public String toString() {
        return "User{" +
                "image='" + image + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", imageUri='" + imageUri + '\'' +
                '}';
    }
}
