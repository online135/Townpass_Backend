package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import com.example.demo.model.RssFeedItem;
import com.example.demo.model.RssFeedResult;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender; // Autowire JavaMailSender

    public void sendEmailWithRssContent(RssFeedResult rssFeedResult, String recipient) {
        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setTo(recipient);
            message.setSubject(rssFeedResult.getTitle());

            // Construct the HTML content
            StringBuilder emailContent = new StringBuilder();
            emailContent.append("<div style='font-family: Arial, sans-serif; color: #333;'>")
                        .append("<h2 style='color: #0056b3; border-bottom: 2px solid #0056b3; padding-bottom: 5px;'>")
                        // Make the main title clickable
                        .append("<a href='").append(rssFeedResult.getLink()).append("' style='color: #0056b3; text-decoration: none;'>")
                        .append(rssFeedResult.getTitle())
                        .append("</a>")
                        .append("</h2>")
                        .append("<p style='font-size: 14px; color: #333;'>更多內容請看 <a href=\"").append(rssFeedResult.getLink())
                        .append("\" style='color: #1a73e8; text-decoration: none;'>這裡</a>。</p>");

            // Append each item in the RSS feed with clickable titles and links
            for (RssFeedItem item : rssFeedResult.getItems()) {
                emailContent.append("<div style='border: 1px solid #e0e0e0; padding: 20px; margin-bottom: 15px; border-radius: 8px; background-color: #f9f9f9;'>")
                            .append("<h3 style='color: #0056b3; margin-bottom: 8px;'>")
                            // Make the item title clickable
                            .append("<a href='").append(item.getLink()).append("' style='color: #0056b3; text-decoration: none;'>")
                            .append(item.getTitle())
                            .append("</a>")
                            .append("</h3>")
                            .append("<p style='font-size: 14px; color: #555;'>").append(item.getDescription()).append("</p>")
                            .append("<p style='font-size: 14px; margin-top: 10px;'>更多內容請看 <a href=\"").append(item.getLink())
                            .append("\" style='color: #1a73e8; text-decoration: none;'>連結</a></p>")
                            .append("</div>");
            }

            emailContent.append("</div>");

            // Set the constructed HTML content as the email body
            message.setText(emailContent.toString(), true); // 'true' indicates the content is HTML
        };

        try {
            mailSender.send(preparator);
            System.out.println("HTML email sent to " + recipient);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
    }
}