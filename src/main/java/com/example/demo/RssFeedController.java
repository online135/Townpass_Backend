package com.example.demo;

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

import com.example.demo.service.RssFeedService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/rss")
public class RssFeedController {

    @Autowired
    private RssFeedService rssFeedService;

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("/send-tycg-news/{email}")
    public ResponseEntity<String> sendTycgNews(@PathVariable("email") String email) {
        String rssUrl = "https://news.tycg.gov.tw/OpenData.aspx?SN=65C6B1AA38BDD145";
        String rssContent = rssFeedService.fetchAndFormatRssFeed(rssUrl);
        
        System.err.println(email);
        // Send email with the RSS content
        sendEmailWithRssContent(email, "桃園政府市政新聞", rssContent);
        
        return new ResponseEntity<>("RSS feed sent via email successfully", HttpStatus.OK);
    }

    private void sendEmailWithRssContent(String to, String subject, String content) {
        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content, true); // 'true' indicates HTML content
        };

        try {
            mailSender.send(preparator);
            System.out.println("HTML email sent to " + to);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
    }
}