package com.example.demo.service;

import com.example.demo.model.RssFeedItem;
import com.example.demo.model.RssFeedResult;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RssFeedService {

    public RssFeedResult fetchAndFormatRssFeed(String url) {
        String title = "";
        String link = "";
        List<RssFeedItem> items = new ArrayList<>();

        try {
            URL feedSource = new URL(url);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedSource));
            
            title = feed.getTitle();
            link = feed.getLink();

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
                // Limit the description length
                String description = entry.getDescription() != null ? entry.getDescription().getValue() : "";
                String truncatedDescription = truncateDescription(description, 200);
                
                RssFeedItem item = new RssFeedItem(
                    entry.getTitle(),
                    entry.getLink(),
                    truncatedDescription // 長度 200, 看 50 行
                );

                items.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new RssFeedResult(title, link, items);
    }

    private String truncateDescription(String description, int maxLength) {
        if (description.length() <= maxLength) {
            return description;
        }
        return description.substring(0, maxLength) + "...";
    }
}