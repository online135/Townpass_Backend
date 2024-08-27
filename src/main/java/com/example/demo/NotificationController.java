package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// 通知列表
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/notifications")
public class NotificationController {
	
    private List<Map<String, Object>> noticeList = new ArrayList<>();

    String method = "mail"; // Here we consider only "mail" for now

    @Autowired
    private HttpServletRequest request; // Autowire HttpServletRequest

    public NotificationController() {
        // Sample data
        Map<String, Object> notice1 = new HashMap<>();
        notice1.put("id", 1);
        notice1.put("category", "weather");
        notice1.put("hour", "7");
        notice1.put("region", "tycg");
        notice1.put("noticeMethod", "mail");
        notice1.put("email", "b97b01067@gmail.com");

        noticeList.add(notice1);

        Map<String, Object> notice2 = new HashMap<>();
        notice2.put("id", 2);
        notice2.put("category", "news");
        notice2.put("hour", "22");
        notice2.put("region", "taipei");
        notice1.put("noticeMethod", "mail");
        notice2.put("email", "b97b01067@g.ntu.edu.tw");

        noticeList.add(notice2);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getNotifications() {
        return new ResponseEntity<>(noticeList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDataById(@PathVariable("id") int id) {
        for (Map<String, Object> item : noticeList) {
            if (item.get("id").equals(id)) {
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
        for (Map<String, Object> notification : noticeList) {
            if (notification.get("hour").equals(currentHour)) {
                performCrontabTask(request, notification);
            }
        }
    }

    private void performCrontabTask(HttpServletRequest request, Map<String, Object> notification) {
        if ("mail".equalsIgnoreCase(notification.get("noticeMethod").toString()))
        {
            // Get dynamic URL
            String apiUrl = getApiUrl(request, notification.get("region").toString(), notification.get("email").toString());
            // Call the API
            String apiResponse = sendTycgNews(apiUrl);
            System.out.println("API Response: " + apiResponse);
        } else {
            System.out.println("No action defined for method: " + method);
        }
    }

    private String sendTycgNews(String apiUrl) {
        System.out.println(apiUrl);

        String response = "";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            // JSON payload
            String jsonInputString = "{ \"message\": \"Your news content here\" }";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Handle the response
            int code = connection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                response = "News sent successfully.";
            } else {
                response = "Failed to send news. HTTP response code: " + code;
            }
        } catch (Exception e) {
            response = "Error occurred: " + e.getMessage();
        }

        return response;
    }

    public static String getApiUrl(HttpServletRequest request, String region, String email) {
        String scheme = request.getScheme();             // http or https
        String serverName = request.getServerName();     // Hostname or IP
        int serverPort = request.getServerPort();        // Server port
        String contextPath = request.getContextPath();   // Webapp's context path

        // Constructing the URL
        return String.format("%s://%s:%d%s/send-news/%s", scheme, serverName, serverPort, contextPath, region, email);
    }
}