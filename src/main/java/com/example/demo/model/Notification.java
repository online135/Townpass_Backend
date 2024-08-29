package com.example.demo.model;

public class Notification {
    private int id;
    private String category;
    private String hour;
    private String region;
    private String noticeMethod;
    private String email;
    private String dataType; // New field to indicate the data type

    // Constructors
    public Notification() {
    }

    public Notification(int id, String category, String hour, String region, String noticeMethod, String email, String dataType) {
        this.id = id;
        this.category = category;
        this.hour = hour;
        this.region = region;
        this.noticeMethod = noticeMethod;
        this.email = email;
        this.dataType = dataType;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getNoticeMethod() {
        return noticeMethod;
    }

    public void setNoticeMethod(String noticeMethod) {
        this.noticeMethod = noticeMethod;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}