package com.example.demo.model;

import java.util.List;

public class RssFeedResult {
    private String title;
    private String link;
    private List<RssFeedItem> items;

    public RssFeedResult(String title, String link, List<RssFeedItem> items) {
        this.title = title;
        this.link = link;
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<RssFeedItem> getItems() {
        return items;
    }

    public void setItems(List<RssFeedItem> items) {
        this.items = items;
    }
}