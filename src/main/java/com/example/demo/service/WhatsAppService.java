package com.example.demo.service;

import com.example.demo.model.RssFeedResult;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

import org.springframework.stereotype.Service;

@Service
public class WhatsAppService {

    // Find your Account Sid and Token at twilio.com/console
    public static final String ACCOUNT_SID = "不推上git, 之後處理";
    public static final String AUTH_TOKEN = "不推上git, 之後處理";

    public void sendNotification(RssFeedResult rssFeedResult, String recipient) {  
      try {   
          StringBuilder messageBuilder = new StringBuilder();
          messageBuilder.append("【")
                        // .append(rssFeedResult.getTitle())
                        .append("台北通") // 簡訊長度太短, 所以寫死, 不用 rss 的 title
                        .append("】")
                        .append(rssFeedResult.getItems().get(0).getTitle())
                        .append("...，更多資訊請看: "); // 連結這裡不加, 後面要縮短後再加

          String encodedMessage = messageBuilder.toString();
          String encodedLink = TinyUrlShortener.shortenUrl(rssFeedResult.getLink());
    
          String body = encodedMessage + encodedLink;

          // 初始化 Twilio
          Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
          Message message = Message.creator(
            new com.twilio.type.PhoneNumber("whatsapp:+886937338506"),
            new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
            body
          ).create();
          
          System.out.println("Recipient Number: " + recipient);
          System.out.println("Send Result Success");

          System.out.println(body);
      } catch (Exception e) {
        System.err.println("Error sending notification: " + e.getMessage());
      }
    }
}
