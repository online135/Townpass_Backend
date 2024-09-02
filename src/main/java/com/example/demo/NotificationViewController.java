package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import com.example.demo.model.Notification;

@Controller
public class NotificationViewController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/notifications")
    public String viewNotifications(Model model) {
        // Fetch notifications from the backend API
        List<Notification> notifications = restTemplate.getForObject("http://localhost:8080/api/notifications", List.class);

        // Add notifications to the model
        model.addAttribute("notifications", notifications);
        
        // Return the Thymeleaf template name
        return "notifications";
    }
}