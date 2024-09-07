package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.demo.model.Notification;

import jakarta.servlet.http.HttpServletRequest;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


// 台北旅遊網 open api 一覽表
// https://www.travel.taipei/open-api

// 通知列表
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/notifications")
public class NotificationController {
	
    private RestTemplate restTemplate = new RestTemplate(); // 可以使用 Bean 來注入 RestTemplat

    private List<Notification> notifications = new ArrayList<>();

    String method = "mail"; // Here we consider only "mail" for now

    public NotificationController() {
        // Sample data
        notifications.add(
            new Notification(
                1, 
                "weather", 
                "tycg", 
                "line", 
                "b97b01067@gmail.com",
                "0937338506",
                "ooY1R7ACEpOON76PkHloQ7kdYFDVbTblvRNHafVfFXG",
                "3,5",
                7, 
                22,
                true)
            );
        notifications.add(
            new Notification(
                2, 
                "news", 
                "taipei", // subject
                "email", 
                "b97b01067@g.ntu.edu.tw",
                "0937338506",
                "ooY1R7ACEpOON76PkHloQ7kdYFDVbTblvRNHafVfFXG",
                "7",
                11, 
                57,
                true)
            );

    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

//     // (2) 列出單一資料 (by Id)
//     @GetMapping("/{id}")
//     public ResponseEntity<Notification> getNotificationById(@PathVariable("id") int id) {
//         for (Notification notification : notifications) {
//             if (notification.getId() == id) {
//                 return new ResponseEntity<>(notification, HttpStatus.OK);
//             }
//         }
//         return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//     }

//     // (3) 新增資料 post
//     @PostMapping
//     public ResponseEntity<Notification> addNotification(@RequestBody Notification notification) {
//         // notification.setId(idCounter++);
//         notifications.add(notification);
//         return new ResponseEntity<>(notification, HttpStatus.CREATED);
//     }

//     // (4) 更新資料 put
//     @PutMapping("/{update}")
//     public ResponseEntity<Notification> updateNotification(@PathVariable("id") int id, @RequestBody Notification updatedNotification) {
//         for (int i = 0; i < notifications.size(); i++) {
//             if (notifications.get(i).getId() == id) {
//                 updatedNotification.setId(id);
//                 notifications.set(i, updatedNotification);
//                 return new ResponseEntity<>(updatedNotification, HttpStatus.OK);
//             }
//         }
//         return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//     }

//     // (5) 刪除資料 delete
//     @DeleteMapping("/{delete}")
//     public ResponseEntity<Void> deleteNotification(@PathVariable("id") int id) {
//         for (Notification notification : notifications) {
//             if (notification.getId() == id) {
//                 notifications.remove(notification);
//                 return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//             }
//         }
//         return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//     }
// //
//     @GetMapping
//     public ResponseEntity<List<Notification>> getNotifications() {
//         return new ResponseEntity<>(notifications, HttpStatus.OK);
//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<Notification> getDataById(@PathVariable("id") int id) {
//         for (Notification item : notifications) {
//             if (item.getId() == id) {
//                 return new ResponseEntity<>(item, HttpStatus.OK);
//             }
//         }
//         return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//     }

    // 排程任務，每分鐘檢查一次通知
    @Scheduled(cron = "0 * * * * *") // 每分鐘執行一次
    public void checkNotifications() {
        // 取得當前時間並格式化
        LocalDateTime now = LocalDateTime.now();
        String formattedTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // 顯示現在時間
        System.out.println("現在時間: " + formattedTime);

        // 取得當前時間 (e.g., "07")
        DayOfWeek currentWeekday = now.getDayOfWeek();
        int currentHour = now.getHour();
        int currentMinute = now.getMinute();

        for (Notification notification : notifications) {
            System.out.println("====================================================");
            System.out.println("開始進行 "+ notification.getSubject() + " 執行與否判斷");

            // 檢查是否啟動中
            if (!notification.isActive()) {
                System.out.println("非啟動中，跳過");
                continue; // 如果小時不對，跳到下一個通知
            }

            // 解析通知的 weekday（用逗號分隔）
            String notificationWeekdays = notification.getDayOfWeek(); // 假設 weekday 是字串，如 "1,2,3"
            List<String> notificationWeekdayList = Arrays.asList(notificationWeekdays.split(","));

            // 檢查當前星期是否在通知的 weekday 列表中
            String currentWeekdayValue = String.valueOf(currentWeekday.getValue());
            // 星期一為 1, 星期天為 7
            if (!notificationWeekdayList.contains(currentWeekdayValue)) {
                System.out.println("星期不對，跳過");
                continue; // 如果星期不對，跳到下一個通知
            }

            // 檢查小時是否匹配
            if (notification.getHour() != currentHour) {
                System.out.println("小時不對，跳過");
                continue; // 如果小時不對，跳到下一個通知
            }

            // 檢查分鐘是否匹配
            if (notification.getMinute() != currentMinute) {
                System.out.println("分鐘不對，跳過");
                continue; // 如果分鐘不對，跳到下一個通知
            }

            System.out.println("開始執行: "+ notification.getSubject());
            performCrontabTask(notification);
        }
    }

    private void performCrontabTask(Notification notification) {
        // 實作通知的執行邏輯
        String url = "http://localhost:8080/api/rss/send-subject/"; // 假設 API 的 URL 是這個

        String recipient = getRecipient(notification);

        // 使用 URI Builder 構建完整 URL 和參數
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("subject", notification.getSubject())
                .queryParam("noticeMethod", notification.getNoticeMethod())
                .queryParam("recipient", recipient);

        try {
            // 發送 POST 請求
            ResponseEntity<String> response = restTemplate.postForEntity(builder.toUriString(), null, String.class);
            System.out.println("通知發送成功: " + response.getBody());
        } catch (Exception e) {
            System.err.println("通知發送失敗: " + e.getMessage());
        }
    }

    // 這是一個簡單的幫助方法來根據通知方法選擇聯絡人
    private String getRecipient(Notification notification) {
        switch (notification.getNoticeMethod().toLowerCase()) {
            case "email":
                return notification.getEmail();
            case "sms":
            case "whatsapp":
                return notification.getPhone();
            case "line":
                return notification.getLineNotifyToken();
            default:
                return "";
        }
    }
}