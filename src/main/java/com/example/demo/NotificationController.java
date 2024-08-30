package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.EmailService;
import com.example.demo.model.Notification;


import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import java.io.*; 

// 台北旅遊網 open api 一覽表
// https://www.travel.taipei/open-api

// 通知列表
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/notifications")
public class NotificationController {
	
    private List<Notification> noticeList = new ArrayList<>();

    String method = "mail"; // Here we consider only "mail" for now

    @Autowired
    private EmailService emailService; // Autowire the EmailService

    @Autowired
    private HttpServletRequest request; // Autowire HttpServletRequest

    public NotificationController() {
        // Sample data
        noticeList.add(
            new Notification(
                1, 
                "weather", 
                "7", 
                "tycg", 
                "line", 
                "b97b01067@gmail.com", 
                "rss")
            );
        noticeList.add(
            new Notification(
                2, 
                "news", 
                "21", 
                "taipei", // subject
                "mail", 
                "b97b01067@g.ntu.edu.tw", 
                "json")
            );

    }

    // 請 品橋 在 以下 範圍完成 CRUD 的 web api 開發, 針對 Notification 這份資料 (上面的), 然後測試時可以用 postman 去呼叫可以操作資料 restful api
    // 請參考 https://github.com/online135/hackathon_practice_spring_boot/blob/main/src/main/java/com/example/demo/DataController.java 去寫

    // (1) 列出全部資料 (list)

    // (2) 列出單一資料 (by Id)

    // (3) 新增資料 post

    // (4) 更新資料 put

    // (5) 刪除資料 delete

    // 請 品橋 在 以上 範圍完成 CRUD 的 web api 開發, 針對 Notification 這份資料 (上面的), 然後測試時可以用 postman 去呼叫可以操作資料

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications() {
        return new ResponseEntity<>(noticeList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getDataById(@PathVariable("id") int id) {
        for (Notification item : noticeList) {
            if (item.getId() == id) {
                return new ResponseEntity<>(item, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Scheduled task to check notifications every minute
    // @Scheduled(cron = "0 * * * * *") // Executes every minute
    public void checkNotifications() {
        // Get the current hour in 24-hour format as a string (e.g., "07")
        String currentHour = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH"));
        for (Notification notification : noticeList) {
            if (notification.getHour().equals(currentHour)) {
                performCrontabTask(notification);
            }
        }
    }

    private void performCrontabTask(Notification notification) {

        System.out.println(notification.getEmail());
        //     @GetMapping("/send-news/{region}/{noticeMethod}/{recipient}")
    }
}