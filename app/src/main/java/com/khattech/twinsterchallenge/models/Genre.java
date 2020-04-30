package com.khattech.twinsterchallenge.models;

/**
 * Created by Mohamed Achref on 4/30/20.
 */
public class Genre {

    private String id;
    private String name;


    @Override
    public String toString() {
        return "Genre{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
