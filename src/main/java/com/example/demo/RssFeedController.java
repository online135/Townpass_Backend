package com.example.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.RssFeedItem;
import com.example.demo.model.RssFeedResult;
import com.example.demo.model.Subject;
import com.example.demo.model.SubjectCategory;
import com.example.demo.service.EmailService;
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
    private EmailService emailService;

    @Autowired
    private SmsService smsService;  // Inject SMS service

    @Autowired
    private LineNotifyService lineNotifyService;

    private List<SubjectCategory> categories;  // List of all main categories

    public RssFeedController() {
        categories = new ArrayList<>();

        // Example of adding a main category with its respective subjects
        SubjectCategory localNews = new SubjectCategory("local_news", "地方新聞");
        localNews.addSubject("taipei", "台北市", "https://www.gov.taipei/OpenData.aspx?SN=7DEC7150E6BAD606");
        localNews.addSubject("taichung", "台中市", "https://www.taichung.gov.tw/10179/564770/rss?nodeId=9962");
        localNews.addSubject("tycg", "桃園市", "https://news.tycg.gov.tw/OpenData.aspx?SN=65C6B1AA38BDD145");
        // Add the main category to the list
        categories.add(localNews);

        // You can add more main categories similarly
        SubjectCategory otherCategory = new SubjectCategory("other_news", "其他分類");
        otherCategory.addSubject("other", "其他新聞", "https://example.com/rss");
        categories.add(otherCategory);
    }

    // API endpoint to get all categories
    // Method to retrieve categories, 取得大分類列表
    @GetMapping("/categories")
    public List<SubjectCategory> getCategories() {
        return categories;
    }

    // API endpoint to get subjects by category code
    // Method to retrieve subjects, 丟入大分類的 String code, 取得小分類
    @GetMapping("/categories/{category}/")
    public SubjectCategory getSubjectsByCategory(
        @PathVariable String category
    ) {
        for (SubjectCategory subjectCategory : categories) {
            if (subjectCategory.getCode().equals(category)) {
                return subjectCategory;
            }
        }
        return null;
    }

    private String getRssUri(String subject) {
        for (SubjectCategory category : categories) {
            for (Subject subj : category.getSubjects()) {
                if (subj.getCode().equalsIgnoreCase(subject)) {
                    return subj.getRssUri();
                }
            }
        }
        return "Subject not found"; // Handle cases where the subject does not exist
    }

    // 寄送通知 api
    @PostMapping("/send-subject/")
    public ResponseEntity<String> sendSubject(
        @RequestParam("subject") String subject, 
        @RequestParam("noticeMethod") String noticeMethod, 
        @RequestParam("recipient") String recipient) {   

        String rssUrl = getRssUri(subject);
        System.out.println(rssUrl);
        
        if (rssUrl == null) {
            return new ResponseEntity<>("Invalid subject", HttpStatus.BAD_REQUEST);
        }

        RssFeedResult rssFeedResult = rssFeedService.fetchAndFormatRssFeed(rssUrl);

        switch(noticeMethod)
        {
            // email
            case "email":
                emailService.sendEmailWithRssContent(rssFeedResult, recipient);
                return new ResponseEntity<>("RSS feed sent via email successfully", HttpStatus.OK);
            
            // line
            case "line":
                lineNotifyService.sendNotification(rssFeedResult);
                return new ResponseEntity<>("RSS feed sent via LINE Notify successfully", HttpStatus.OK);
            
            // sms
            case "sms":
                smsService.sendNotification(rssFeedResult, recipient);
                return new ResponseEntity<>("RSS feed sent via sms successfully", HttpStatus.OK);

            default:
                return new ResponseEntity<>("Invalid notification method", HttpStatus.BAD_REQUEST);

        }
    }
}