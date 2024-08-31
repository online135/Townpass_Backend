package com.example.demo;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.RssFeedItem;
import com.example.demo.model.RssFeedResult;
import com.example.demo.service.LineNotifyService;
import com.example.demo.service.RssFeedService;
import com.example.demo.service.SmsService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/rss")
public class RssFeedController {

    @Autowired
    private RssFeedService rssFeedService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SmsService smsService;  // Inject SMS service

    @Autowired
    private LineNotifyService lineNotifyService;

    private Map<String, String> subjectRssUrls;

    public RssFeedController() {
        // Initialize the subject-to-RSS URL mapping
        subjectRssUrls = new HashMap<>();
        subjectRssUrls.put("tycg", "https://news.tycg.gov.tw/OpenData.aspx?SN=65C6B1AA38BDD145");
        subjectRssUrls.put("taipei", "https://www.gov.taipei/OpenData.aspx?SN=7DEC7150E6BAD606");
        subjectRssUrls.put("taichung", "https://www.taichung.gov.tw/10179/564770/rss?nodeId=9962");
        // Add more regions as needed
    }

    private String getRssUrlBySubject(String subject) {
        return subjectRssUrls.get(subject);
    }

    @GetMapping("/send-news/{subject}/{noticeMethod}/{recipient}")
    public ResponseEntity<String> sendSubject(
        @PathVariable("subject") String subject, 
        @PathVariable("noticeMethod") String noticeMethod, 
        @PathVariable("recipient") String recipient) {   

        String rssUrl = getRssUrlBySubject(subject);
        
        if (rssUrl == null) {
            return new ResponseEntity<>("Invalid subject", HttpStatus.BAD_REQUEST);
        }

        RssFeedResult rssFeedResult = rssFeedService.fetchAndFormatRssFeed(rssUrl);

        switch(noticeMethod)
        {
            // email
            case "email":
                sendEmailWithRssContent(recipient, rssFeedResult);

                return new ResponseEntity<>("RSS feed sent via email successfully", HttpStatus.OK);
            
            // line
            case "line":
                lineNotifyService.sendNotification(rssFeedResult);

                return new ResponseEntity<>("RSS feed sent via LINE Notify successfully", HttpStatus.OK);
            
            // sms
            case "sms":
                smsService.sendNotification(rssFeedResult);

                return new ResponseEntity<>("RSS feed sent via sms successfully", HttpStatus.OK);
            default:
                return new ResponseEntity<>("Invalid notification method", HttpStatus.BAD_REQUEST);

        }
    }

    private void sendEmailWithRssContent(String to, RssFeedResult rssFeedResult) {
        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setTo(to);
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
            System.out.println("HTML email sent to " + to);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
    }
}