package com.example.demo.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TinyUrlShortener {

    private static final String TINYURL_API_URL = "http://tinyurl.com/api-create.php?url=";

    public static String shortenUrl(String longUrl) throws IOException {
        // 構建請求 URL
        String requestUrl = TINYURL_API_URL + longUrl;

        // 建立 HTTP 連接
        HttpURLConnection connection = (HttpURLConnection) new URL(requestUrl).openConnection();
        connection.setRequestMethod("GET");

        // 檢查響應代碼
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            System.out.println("Failed to shorten URL, HTTP response code: " + responseCode);
            return longUrl;
        }

        // 讀取響應
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString(); // 返回縮短後的 URL
        }
    }
}