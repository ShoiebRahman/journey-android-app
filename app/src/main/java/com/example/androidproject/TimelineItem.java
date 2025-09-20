package com.example.androidproject;

public class TimelineItem {
    private long id;
    private long itineraryId;
    private String timelinePeriod;
    private String notes;

    public TimelineItem(long id, long itineraryId, String timelinePeriod, String notes) {
        this.id = id;
        this.itineraryId = itineraryId;
        this.timelinePeriod = timelinePeriod;
        this.notes = notes;
    }

    public long getId() {
        return id;
    }

    public long getItineraryId() {
        return itineraryId;
    }

    public String getTimelinePeriod() {
        return timelinePeriod;
    }

    public String getNotes() {
        return notes;
    }
}
