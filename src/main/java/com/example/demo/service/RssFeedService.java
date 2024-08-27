package com.example.demo.service;

import com.example.demo.model.RssFeedResult;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RssFeedService {

    public RssFeedResult fetchAndFormatRssFeed(String url) {
        StringBuilder content = new StringBuilder();
        String title = "";

        try {
            URL feedSource = new URL(url);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedSource));
            
            title = feed.getTitle();

            content.append("<h2>").append(title).append("</h2>");
            
            // 取得資料以及排序
            List<SyndEntry> entries = feed.getEntries().stream()
            .sorted((e1, e2) -> {
                Date d1 = e1.getPublishedDate();
                Date d2 = e2.getPublishedDate();
                if (d1 == null && d2 == null) return 0;
                if (d1 == null) return 1;
                if (d2 == null) return -1;
                return d2.compareTo(d1);
            })
            .limit(5)
            .collect(Collectors.toList());

            for (SyndEntry entry : entries) {
                content.append("<div>");
                content.append("<h3><a href=\"").append(entry.getLink()).append("\" target=\"_blank\">");
                content.append(entry.getTitle()).append("</a></h3>");
                
                // 限制描述內容的長度
                String description = entry.getDescription().getValue();
                String truncatedDescription = truncateDescription(description, 200); // 限制到200字
                
                content.append("<p>").append(truncatedDescription).append("詳情請點").append(" <a href=\"").append(entry.getLink()).append("\" target=\"_blank\">連結</a></p>");
                content.append("<small>").append(entry.getPublishedDate()).append("</small>");
                content.append("<hr />");
                content.append("</div>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new RssFeedResult(title, content.toString());
    }

    private String truncateDescription(String description, int maxLength) {
        if (description.length() <= maxLength) {
            return description;
        }
        return description.substring(0, maxLength) + "...";
    }
}