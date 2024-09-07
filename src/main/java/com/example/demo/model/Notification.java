package com.example.demo.model;

public class Notification {
    private int id;
    private String subjectName;         // 使用者輸入的 中文標題
    private String mainCategory;
    private String subCategory;
    private String noticeMethod;
    private String email = "b97b01067@gmail.com";
    private String phone = "0937338506";
    private String lineNotifyToken = "ooY1R7ACEpOON76PkHloQ7kdYFDVbTblvRNHafVfFXG";
    private String dayOfWeek;
    private int hour;
    private int minute;
    private boolean isActive = true;

    public Notification(
        int id, 
        String subjectName,         // 使用者輸入的 中文標題
        String mainCategory,            // 通知的大類別 ex: news
        String subCategory,             // 通知的主題或標題 ex: taipei_news
        String noticeMethod,        // line, email, sms, whatsapp
        String email,  
        String phone,
        String lineNotifyToken,
        String dayOfWeek,           // 3,5  1~7 周一 ~ 週日
        int hour,                   // 20時
        int minute,                 // 53分
        boolean isActive            // 是否啟動中
    ) {
        this.id = id;
        this.subjectName = subjectName;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.noticeMethod = noticeMethod;
        this.email = email;
        this.phone = phone;
        this.lineNotifyToken = lineNotifyToken;
        this.dayOfWeek = dayOfWeek;
        this.hour = hour;
        this.minute = minute;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(String category) {
        this.mainCategory = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subject) {
        this.subCategory = subject;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}