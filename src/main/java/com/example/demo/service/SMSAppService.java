package com.example.demo.service;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.model.RssFeedResult;

/**
 * 
三竹簡訊真的很妙， API 會根據註冊的站（現有二站、三站）也有不同的 API 網域名稱和請求路徑。
要不是這次串接碰到雷，客服打電話過來聯繫，我還真不知道這回事。
也難怪官網上面的 API 文件中，請求網域名稱居然是用變數，然後也不說是什麼，找半天！
後來電話解釋 API 功能是企業客戶才可以使用，所以那份文件也會隨著開通的同時完整轉交給客戶，自然串接就不會發生問題。
http://smexpress[.]mitake[.]com[.]tw[:]9600/
和
http://smexpress[.]mitake[.]com[.]tw/
和
http://smsb2c[.]mitake[.]com[.]tw
和
http://smsapi[.]mitake[.]com[.]tw
上述都是 API 網域，但請求方式和對應站點都不太一樣呢！！ 先筆記起這個雷...


我是用二站註冊的
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


          StringBuffer reqUrl = new StringBuffer();
          reqUrl.append("http://smsapi.mitake.com.tw/b2c/mtk/SmSend?");
          StringBuffer params = new StringBuffer();
          params.append("&username=0937338506");
          params.append("&password=gn00311869");
          params.append("&dstaddr=" + recipient);
          params.append("&smbody=" + body);
          params.append("&CharsetURL=UTF-8");
          URL url = new URL(reqUrl.toString());
          HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
          urlConnection.setRequestMethod("POST");
          urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
          urlConnection.setDoOutput(true);
          urlConnection.connect();
          DataOutputStream dos = new DataOutputStream(urlConnection.getOutputStream());
          dos.write(params.toString().getBytes("utf-8"));
          System.out.println((dos));
          dos.flush();
          dos.close();
          
          // 在測試環境成功的話是不會收到簡訊的，而是會收到回傳回來的狀態（所以要理解狀態代碼的意思）跟一段提示成功 or 失敗的訊息
          System.out.println("Recipient Number: " + recipient);
          System.out.println("Send Result Success");

          System.out.println(body);
      } catch (Exception e) {
        System.err.println("Error sending notification: " + e.getMessage());
      }
    }
}
