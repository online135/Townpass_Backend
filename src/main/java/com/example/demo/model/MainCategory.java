package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

// Class representing a category with its English code, Chinese display name, and list of subjects
public class MainCategory {
    private String code;          // English code for identification, e.g., "local_news"
    private String displayName;   // Chinese display name, e.g., "地方新聞"
    private List<SubCategory> subCategories; // List of subjects under this category

    public MainCategory(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
        this.subCategories = new ArrayList<>();
    }

    public void addSubCategory(String code, String displayName, String rssUri) {
        subCategories.add(new SubCategory(code, displayName, rssUri));
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<SubCategory> getSubCategories() {
        return subCategories;
    }
}