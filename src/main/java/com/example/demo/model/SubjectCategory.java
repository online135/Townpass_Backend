package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

// Class representing a category with its English code, Chinese display name, and list of subjects
public class SubjectCategory {
    private String code;          // English code for identification, e.g., "local_news"
    private String displayName;   // Chinese display name, e.g., "地方新聞"
    private List<Subject> subjects; // List of subjects under this category

    public SubjectCategory(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
        this.subjects = new ArrayList<>();
    }

    public void addSubject(String code, String displayName, String rssUri) {
        subjects.add(new Subject(code, displayName, rssUri));
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }
}