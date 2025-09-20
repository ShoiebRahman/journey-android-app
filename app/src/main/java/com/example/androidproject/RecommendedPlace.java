package com.example.androidproject;

public class RecommendedPlace {
    private String cityName;
    private String country;
    private String imageUrl;

    public RecommendedPlace(String name, String location, String imageUrl) {
        this.cityName = name;
        this.country = location;
        this.imageUrl = imageUrl;
    }

    public String getCity() {
        return cityName;
    }

    public String getCountry() {
        return country;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
