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

    private Map<String, String> regionRssUrls;

    public RssFeedController() {
        // Initialize the region-to-RSS URL mapping
        regionRssUrls = new HashMap<>();
        regionRssUrls.put("tycg", "https://news.tycg.gov.tw/OpenData.aspx?SN=65C6B1AA38BDD145");
        regionRssUrls.put("taipei", "https://www.gov.taipei/OpenData.aspx?SN=7DEC7150E6BAD606");
        regionRssUrls.put("taichung", "https://www.taichung.gov.tw/10179/564770/rss?nodeId=9962");
        // Add more regions as needed
    }

    private String getRssUrlByRegion(String region) {
        return regionRssUrls.get(region);
    }

    @GetMapping("/send-news/{region}/{noticeMethod}/{recipient}")
    public ResponseEntity<String> sendRegionNews(
        @PathVariable("region") String region, 
        @PathVariable("noticeMethod") String noticeMethod, 
        @PathVariable("recipient") String recipient) {   

        String rssUrl = getRssUrlByRegion(region);
        
        if (rssUrl == null) {
            return new ResponseEntity<>("Invalid region", HttpStatus.BAD_REQUEST);
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
            message.setText(rssFeedResult.getTitle(), true); // 'true' indicates HTML content
        };

        try {
            mailSender.send(preparator);
            System.out.println("HTML email sent to " + to);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
    }
}