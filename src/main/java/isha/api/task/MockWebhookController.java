package isha.api.task;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
public class MockWebhookController {

    @PostMapping("/hiring/generateWebhook/JAVA")
    public ResponseEntity<Map<String, String>> generateWebhook(@RequestBody Map<String, String> request) {
        // Generate dummy webhook and token
        String webhookUrl = "http://localhost:8080/hiring/testWebhook/JAVA";
        String accessToken = UUID.randomUUID().toString();

        return ResponseEntity.ok(
                Map.of(
                        "webhook", webhookUrl,
                        "accessToken", accessToken
                )
        );
    }

    @PostMapping("/hiring/testWebhook/JAVA")
    public ResponseEntity<Map<String, String>> testWebhook(@RequestHeader("Authorization") String authorization,
                                                           @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(
                Map.of(
                        "status", "success",
                        "finalQueryReceived", body.getOrDefault("finalQuery", "")
                )
        );
    }
}
