package com.example.demo.service;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.RssFeedItem;
import com.example.demo.model.RssFeedResult;

import org.springframework.stereotype.Service;

@Service
public class LineNotifyService {

    private static final String LINE_NOTIFY_URL = "https://notify-api.line.me/api/notify";

    public void sendNotification(RssFeedResult rssFeedResult, String recipient) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + recipient);
            headers.set("Content-Type", "application/x-www-form-urlencoded");

            // Get the current date formatted as "yyyy-MM-dd"
            LocalDate currentDate = LocalDate.now();
            String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // Create message with the current date and RSS feed title
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("【").append(formattedDate).append("】 ").append(rssFeedResult.getTitle()).append("\n");

            // Include titles of each item in the message
            for (RssFeedItem item : rssFeedResult.getItems()) {
                messageBuilder.append("- ").append(item.getTitle()).append("\n");
            }

            messageBuilder.append("更多資訊請看: "); // 連結這裡不加, 後面要縮短後再加

            // Encode title and link to prevent garbled text
            String encodedMessage = encodeMessage(messageBuilder.toString());
            String encodedLink = encodeMessage(TinyUrlShortener.shortenUrl(rssFeedResult.getLink()));


            String body = "message=" + encodedMessage + encodedLink;

            HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.exchange(LINE_NOTIFY_URL, HttpMethod.POST, requestEntity, String.class);

            System.out.println("Notification sent successfully");
        } catch (Exception e) {
            System.err.println("Error sending notification: " + e.getMessage());
        }
    }

    private String encodeMessage(String message) {
        try {
            return URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            System.err.println("Error encoding message: " + e.getMessage());
            return message; // Fallback to original message if encoding fails
        }
    }

}