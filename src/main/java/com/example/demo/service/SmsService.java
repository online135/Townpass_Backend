package com.example.demo.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import com.example.demo.model.RssFeedResult;

import org.springframework.stereotype.Service;

import com.azure.communication.sms.models.*;
import com.azure.communication.sms.*;

// Azure  Send an SMS message
// https://learn.microsoft.com/en-us/azure/communication-services/quickstarts/sms/send?pivots=programming-language-java&tabs=windows
@Service
public class SmsService {
    // You can get your connection string from your resource in the Azure portal.
    String connectionString = "endpoint=https://<resource-name>.communication.azure.com/;accesskey=<access-key>";


    public void sendNotification(RssFeedResult rssFeedResult) {  
      try {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("【")
                      // .append(rssFeedResult.getTitle())
                      .append("台北通") // 簡訊長度太短, 所以寫死, 不用 rss 的 title
                      .append("】")
                      .append(rssFeedResult.getItems().get(0).getTitle())
                      .append("...，更多資訊請看: "); // 連結這裡不加, 後面要縮短後再加
  
        // Encode title and link to prevent garbled text

        // 看情況調整 encode or 非 encode
        // String encodedMessage = encodeMessage(messageBuilder.toString());
        // String encodedLink = encodeMessage(TinyUrlShortener.shortenUrl(rssFeedResult.getLink()));
  
        // String body = encodedMessage + encodedLink;

        String encodedMessage = messageBuilder.toString();
        String encodedLink = TinyUrlShortener.shortenUrl(rssFeedResult.getLink());
  
        String body = encodedMessage + encodedLink;


        // SmsClient smsClient = new SmsClientBuilder()
        // .connectionString(connectionString)
        // .buildClient();
        // SmsSendResult sendResult = smsClient.send(
        //           "+886937338506",
        //           "+886937338506",
        //           encodedMessage
        // );
  
        System.out.println(body);
        // System.out.println("Recipient Number: " + sendResult.getTo());
        // System.out.println("Send Result Successful:" + sendResult.isSuccessful());
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
