package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;


@Controller
public class NotificationViewController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/notifications")
    public void getNotifications() {

        return ;
    }
}