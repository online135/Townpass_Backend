package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


// 通報列表
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/issues")
public class DataController {
	
    private List<Map<String, Object>> issueList = new ArrayList<>();

    public DataController() {
        // Sample data
        Map<String, Object> issue1 = new HashMap<>();
        issue1.put("category", Category.A);
        issue1.put("title", "TestTitle1");
        issue1.put("date", "2024-08-22");
        issue1.put("description", "This is the 1 item.");
        issue1.put("status", Status.UNPROCESSED);
        issue1.put("id", 1);

        issueList.add(issue1);

        Map<String, Object> issue2 = new HashMap<>();
        issue2.put("category", Category.B);
        issue2.put("title", "TestTitle2");
        issue2.put("date", "2024-08-24");
        issue2.put("description", "This is the 2 item.");
        issue2.put("status", Status.PROCESSING);
        issue2.put("id", 2);

        issueList.add(issue2);

        Map<String, Object> issue3 = new HashMap<>();
        issue3.put("category", Category.C);
        issue3.put("title", "TestTitle3");
        issue3.put("date", "2024-08-24");
        issue3.put("description", "This is the third item.");
        issue3.put("status", Status.PROCESSED);
        issue3.put("id", 3);

        issueList.add(issue3);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getIssueList() {
        return new ResponseEntity<>(issueList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDataById(@PathVariable("id") int id) {
        for (Map<String, Object> item : issueList) {
            if (item.get("id").equals(id)) {
                return new ResponseEntity<>(item, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<String> postData(@RequestBody Map<String, Object> payload) {
        int nextId = issueList.isEmpty() ? 1 : issueList.stream()
        .mapToInt(item -> (int) item.get("id"))
        .max()
        .getAsInt() + 1;

        // 給予 id 以及 狀態為 未處理
        payload.put("id", nextId);
        payload.put("status", Status.UNPROCESSED);
        issueList.add(payload);
        return new ResponseEntity<>("Data received successfully", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateData(@PathVariable("id") int id, @RequestBody Map<String, Object> payload) {
        for (Map<String, Object> item : issueList) {
            if (item.get("id").equals(id)) {
                item.put("category", payload.get("category"));
                item.put("title", payload.get("title"));
                item.put("date", payload.get("date"));
                item.put("description", payload.get("description"));
                return new ResponseEntity<>("Data updated successfully", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Data not found", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteData(@PathVariable("id") int id) {
        for (Map<String, Object> item : issueList) {
            if (item.get("id").equals(id)) {
                issueList.remove(item);
                return new ResponseEntity<>("Data deleted successfully", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Data not found", HttpStatus.NOT_FOUND);
    }
}