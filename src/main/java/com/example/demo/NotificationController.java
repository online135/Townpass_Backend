package com.example.demo;

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
                        "測試標題",
                        "weather",
                        "tycg",
                        "line",
                        "b97b01067@gmail.com",
                        "0937338506",
                        "ooY1R7ACEpOON76PkHloQ7kdYFDVbTblvRNHafVfFXG",
                        "1,2,3,4,5",
                        23,
                        35,
                        true));
        notifications.add(
                new Notification(
                        2,
                        "測試標題2",
                        "news",
                        "taipei", // subject
                        "email",
                        "b97b01067@g.ntu.edu.tw",
                        "0937338506",
                        "ooY1R7ACEpOON76PkHloQ7kdYFDVbTblvRNHafVfFXG",
                        "7",
                        11,
                        57,
                        true));

    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    // (2) 列出單一資料 (by Id)
    // /api/notifications/2
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable("id") int id) {
        for (Notification notification : notifications) {
            if (notification.getId() == id) {
                return new ResponseEntity<>(notification, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // 新增
    @PostMapping
    public ResponseEntity<Notification> addNotification(@RequestBody Notification notification) {

        int nextId = notifications.isEmpty() ? 1
                : notifications.stream()
                        .mapToInt(notif -> notif.getId())
                        .max()
                        .getAsInt() + 1;

        // 設定 id 及狀態
        notification.setId(nextId);
        // notification.setStatus("UNPROCESSED");未處理

        notification.setEmail("b97b01067@g.ntu.edu.tw");
        notification.setPhone("0937338506");
        notification.setLineNotifyToken("ooY1R7ACEpOON76PkHloQ7kdYFDVbTblvRNHafVfFXG");

        notifications.add(notification);

        return new ResponseEntity<>(notification, HttpStatus.CREATED);
    }

    // (4) 更新資料 put
    @PutMapping("/{id}")
    public ResponseEntity<String> updateNotification(
            @PathVariable("id") int id,
            @RequestBody Notification payload) {
        for (Notification notification : notifications) {
            if (notification.getId() == id) {
                // 更新屬性
                // notification.setType((String) payload.get("type"));
                if (payload == null) {
                    break;
                }

                if (payload.getSubCategory() != null) {
                    notification.setSubCategory((String) payload.getSubCategory());
                }

                notification.setSubjectName((String) payload.getSubjectName());
                notification.setNoticeMethod((String) payload.getNoticeMethod());
                // notification.setEmail((String) payload.getEmail());
                // notification.setPhone((String) payload.getPhone());
                // notification.setLineNotifyToken((String) payload.getLineNotifyToken());
                System.out.println(payload.getDayOfWeek());
                notification.setDayOfWeek((String) payload.getDayOfWeek());
                notification.setHour((int) payload.getHour());
                notification.setMinute((int) payload.getMinute());
                // notification.setActive((boolean) payload.isActive());

                // 回應更新成功
                return new ResponseEntity<>("Notification updated successfully", HttpStatus.OK);
            }
        }
        // 如果找不到資料
        return new ResponseEntity<>("Notification not found", HttpStatus.NOT_FOUND);
    }

    // (5) 刪除資料 delete
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(
            @PathVariable("id") int id) {
        // 尋找並刪除對應的通知
        for (Notification notification : notifications) {
            if (notification.getId() == id) {
                notifications.remove(notification);
                return new ResponseEntity<>("Notification deleted successfully", HttpStatus.OK);
            }
        }
        // 如果找不到指定的通知
        return new ResponseEntity<>("Notification not found", HttpStatus.NOT_FOUND);
    }

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
            System.out.println("開始進行 " + notification.getSubCategory() + " 執行與否判斷");

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

            System.out.println("開始執行: " + notification.getSubCategory());
            performCrontabTask(notification);
        }
    }

    private void performCrontabTask(Notification notification) {
        // 實作通知的執行邏輯
        String url = "http://localhost:8080/api/rss/send-subject/"; // 假設 API 的 URL 是這個

        String recipient = getRecipient(notification);

        // 使用 URI Builder 構建完整 URL 和參數
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("subject", notification.getSubCategory())
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

    @GetMapping("/directExecute/{id}")
    public ResponseEntity<String> directExecute(@PathVariable("id") int id) {
        System.out.println("directExecute");
        performCrontabTask(getNotificationById(id).getBody());
        return new ResponseEntity<>("directExecute", HttpStatus.OK);
    }

    @GetMapping("/switchActive/{id}")
    public ResponseEntity<String> switchActive(@PathVariable("id") int id) {
        System.out.println("Switch active");
        int index = getIndexById(id);
        if (index == -1) {
            return new ResponseEntity<>("ID is not found", HttpStatus.NOT_FOUND);
        } else {
            Notification notification = notifications.get(index);
            notification.setActive(!notification.isActive());
            return new ResponseEntity<>("switchActive", HttpStatus.OK);
        }
    }

    private int getIndexById(int id) {
        for (Notification notification : notifications) {
            if (notification.getId() == id) {
                return notifications.indexOf(notification);
            }
        }
        return -1;
    }
}