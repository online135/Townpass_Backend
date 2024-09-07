package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.RssFeedResult;
import com.example.demo.model.SubCategory;
import com.example.demo.model.MainCategory;
import com.example.demo.service.EmailService;
import com.example.demo.service.LineNotifyService;
import com.example.demo.service.RssFeedService;
import com.example.demo.service.SMSAppService;
import com.example.demo.service.WhatsAppService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/rss")
public class RssFeedController {

    @Autowired
    private RssFeedService rssFeedService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private WhatsAppService whatsAppService;  // Inject WhatsAppService

    @Autowired
    private SMSAppService smsAppService;  // Inject SMSAppService

    @Autowired
    private LineNotifyService lineNotifyService;

    private List<MainCategory> mainCategories;  // List of all main categories

    public RssFeedController() {
        mainCategories = new ArrayList<>();

        // Example of adding a main category with its respective subjects
        MainCategory localNews = new MainCategory("local_news", "地方新聞");
        localNews.addSubCategory("taipei", "台北市", "https://www.gov.taipei/OpenData.aspx?SN=7DEC7150E6BAD606");
        localNews.addSubCategory("taichung", "台中市", "https://www.taichung.gov.tw/10179/564770/rss?nodeId=9962");
        localNews.addSubCategory("tycg", "桃園市", "https://news.tycg.gov.tw/OpenData.aspx?SN=65C6B1AA38BDD145");
        // Add the main category to the list
        mainCategories.add(localNews);

        // You can add more main categories similarly
        MainCategory otherCategory = new MainCategory("other_news", "其他分類");
        otherCategory.addSubCategory("other", "其他新聞", "https://example.com/rss");
        mainCategories.add(otherCategory);
    }

    // API endpoint to get all categories
    // Method to retrieve categories, 取得大分類列表
    @GetMapping("/mainCategories")
    public List<MainCategory> getMainCategories() {
        return mainCategories;
    }

    // API endpoint to get subjects by category code
    // Method to retrieve subjects, 丟入大分類的 String code, 取得小分類
    @GetMapping("/mainCategories/{subCategory}/")
    public MainCategory getSubjectsByCategory(
        @PathVariable String subCategory
    ) {
        for (MainCategory mainCategory : mainCategories) {
            if (mainCategory.getCode().equals(subCategory)) {
                return mainCategory;
            }
        }
        return null;
    }

    private String getRssUri(String inputSubCategory) {
        for (MainCategory mainCategory : mainCategories) {
            for (SubCategory subCategory : mainCategory.getSubCategories()) {
                if (subCategory.getCode().equalsIgnoreCase(inputSubCategory)) {
                    return subCategory.getRssUri();
                }
            }
        }
        return "SubCategory not found"; // Handle cases where the subject does not exist
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
                if (recipient == null || !isValidEmail(recipient)) {
                    return new ResponseEntity<>("Invalid or missing email address", HttpStatus.BAD_REQUEST);
                }

                emailService.sendEmailWithRssContent(rssFeedResult, recipient);
                return new ResponseEntity<>("RSS feed sent via email successfully", HttpStatus.OK);
            
            // line
            case "line":
                lineNotifyService.sendNotification(rssFeedResult, recipient);
                return new ResponseEntity<>("RSS feed sent via LINE Notify successfully", HttpStatus.OK);
            
            // whatsapp
            case "whatsapp":
                if (recipient == null || !isValidPhoneNumber(recipient)) {
                    return new ResponseEntity<>("Invalid or missing phone number", HttpStatus.BAD_REQUEST);
                }

                whatsAppService.sendNotification(rssFeedResult, recipient);
                return new ResponseEntity<>("RSS feed sent via whatsapp successfully", HttpStatus.OK);

            // 三竹需要有統編後跟簡訊商開通才能夠串接 api 成功
            // sms, 要串台灣簡訊商 三竹 等才可以寄送(需再申請對應api, token 等)
            // https://www.twsms.com/
            // 三竹簡訊 https://msg.mitake.com.tw/SMS/Home.jsp?t=1725121253337
            case "sms":
                if (recipient == null || !isValidPhoneNumber(recipient)) {
                    return new ResponseEntity<>("Invalid or missing phone number", HttpStatus.BAD_REQUEST);
                }

                smsAppService.sendNotification(rssFeedResult, recipient);
                return new ResponseEntity<>("RSS feed sent via sms successfully", HttpStatus.OK);


            default:
                return new ResponseEntity<>("Invalid notification method", HttpStatus.BAD_REQUEST);

        }
    }

    // Basic validation for email addresses
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        return email.matches(emailRegex);
    }

    // Basic validation for phone numbers (e.g., digits only, 7-15 characters)
    private boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "^\\d{7,15}$";
        return phoneNumber.matches(phoneRegex);
    }
}