package com.example.demo.service;

import com.example.demo.model.RssFeedResult;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppService {


    public void sendNotification(RssFeedResult rssFeedResult, String recipient) {  
      try {   
          System.out.println("開始嘗試寄送 whatsapp");

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

          // https://console.twilio.com/us1/develop/sms/try-it-out/whatsapp-learn?frameUrl=%2Fconsole%2Fsms%2Fwhatsapp%2Flearn%3Fx-target-region%3Dus1
          // In this step, you can start a business-initiated conversation with your users. 
          // Business-initiated conversations required the use of pre-approved templates until the user responds. 
          // Choose from one of our pre-approved templates to start a business-initiated conversation. 
          // Once your customers reply, then you can send free form messages for the next 24 hours after your original message.
          // 
          // 因為 sms 是商業模式行為, 所以每次只開放24hr 測試，超過要再重溪 sync
          Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
          Message message = Message.creator(
            new com.twilio.type.PhoneNumber("whatsapp:+886937338506"), // to
            new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),  // from
            body
          ).create();
          
          System.out.println(message.getBody());
          System.out.println("Recipient Number: " + recipient);
          System.out.println("Send Result Success");

          System.out.println(body);
      } catch (Exception e) {
        System.err.println("Error sending notification: " + e.getMessage());
      }
    }
}
