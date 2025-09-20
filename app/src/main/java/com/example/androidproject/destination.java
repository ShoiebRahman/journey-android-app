package com.example.androidproject;

public class destination {
    private String title;
    private String description;
    private String location;
    private double rating;
    private int imageResId;
    public destination(String title, String description, String location, double rating, int imageResId) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.rating = rating;
        this.imageResId = imageResId;
    }

    public String getTitle() {

        return title;
    }

    public String getLocation() {

        return location;
    }

    public int getImageResId() {
        return imageResId;
    }

    public double getRating() {
        return rating;
    }

    public String getDescription() {
        return description;
    }
}
