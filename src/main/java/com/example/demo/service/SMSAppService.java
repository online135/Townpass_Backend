package com.example.demo.service;

import com.example.demo.model.RssFeedResult;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * SMS 的會不能執行, 因為有比較麻煩, 實際用 whatsAppService 處理
 */
@Service
public class SMSAppService {

    @Value("${twilio.accountsid}")
    private String ACCOUNT_SID;

    @Value("${twilio.authtoken}")
    private String AUTH_TOKEN;

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
          // 錯誤原因 
          // Permission to send an SMS or MMS has not been enabled for the region indicated by the 'To' number
          // Your message content was flagged as going against carrier guidelines.
          Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
          Message message = Message.creator(
            new com.twilio.type.PhoneNumber("+886937338506"),
            new com.twilio.type.PhoneNumber("+16503977032"),
            body
          ).create();
          System.out.println(message.getSid());
          
          System.out.println("Recipient Number: " + recipient);
          System.out.println("Send Result Success");

          System.out.println(body);
      } catch (Exception e) {
        System.err.println("Error sending notification: " + e.getMessage());
      }
    }
}
