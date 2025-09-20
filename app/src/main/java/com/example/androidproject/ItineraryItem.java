package com.example.androidproject;

public class ItineraryItem {
    private long id;
    private long userId;
    private String title;
    private String date;

    public ItineraryItem(long id, long userId,String title, String date) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.date = date;
    }

    public long getId() {
        return id;
    }
    public long getUserId(){return userId;}

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return title + " - " + date;
    }
}