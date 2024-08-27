package com.example.demo;

public enum Status {
    UNPROCESSED("未處理通報"),
    PROCESSING("處理中"),
    PROCESSED("處理完畢");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
