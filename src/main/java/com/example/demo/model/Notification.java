package com.example.demo.model;

import java.time.DayOfWeek;

public class Notification {
    private int id;
    private String category;
    private String subject;
    private String noticeMethod;
    private String email;
    private String phone;
    private String lineNotifyToken;
    private String dayOfWeek;
    private int hour;
    private int minute;

    public Notification(
        int id, 
        String category, // 通知的類別 ex: news
        String subject,  // 通知的主題或標題 ex: taipei_news
        String noticeMethod, // line
        String email,  
        String phone,
        String lineNotifyToken,
        String dayOfWeek,  // 3,5
        int hour,  // 20
        int minute // 53
    ) {
        this.id = id;
        this.category = category;
        this.subject = subject;
        this.noticeMethod = noticeMethod;
        this.email = email;
        this.phone = phone;
        this.lineNotifyToken = lineNotifyToken;
        this.dayOfWeek = dayOfWeek;
        this.hour = hour;
        this.minute = minute;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLineNotifyToken() {
        return lineNotifyToken;
    }

    public void setLineNotifyToken(String lineNotifyToken) {
        this.lineNotifyToken = lineNotifyToken;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }


    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

}