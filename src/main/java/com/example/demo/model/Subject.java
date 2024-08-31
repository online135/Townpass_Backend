package com.example.demo.model;

// Class representing a single subject with its English code, Chinese display name, and RSS URI
public class Subject {
    private String code;          // English code for identification, e.g., "taipei"
    private String displayName;   // Chinese display name, e.g., "台北市"
    private String rssUri;        // RSS feed URI

    public Subject(String code, String displayName, String rssUri) {
        this.code = code;
        this.displayName = displayName;
        this.rssUri = rssUri;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRssUri() {
        return rssUri;
    }
}