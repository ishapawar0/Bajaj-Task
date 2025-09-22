package isha.api.task;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class WebhookTaskRunner implements CommandLineRunner {

    private final WebClient webClient;

    public WebhookTaskRunner(WebClient.Builder webClientBuilder) {
        // Bajaj API server
        this.webClient = webClientBuilder.baseUrl("https://bfhldevapigw.healthrx.co.in").build();
    }

    @Override
    public void run(String... args) throws Exception {
        // ✅ Question 2 SQL query (because regNo ends with 24 → even)
        String sqlQuery = "SELECT " +
                "e1.EMPID, " +
                "e1.FIRSTNAME, " +
                "e1.LASTNAME, " +
                "d.DEPARTMENTNAME, " +
                "COUNT(e2.EMPID) AS YOUNGEREMPLOYEESCOUNT " +
                "FROM EMPLOYEE e1 " +
                "JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENTID " +
                "LEFT JOIN EMPLOYEE e2 ON e1.DEPARTMENT = e2.DEPARTMENT " +
                "AND e2.DOB > e1.DOB " +
                "GROUP BY " +
                "e1.EMPID, " +
                "e1.FIRSTNAME, " +
                "e1.LASTNAME, " +
                "d.DEPARTMENTNAME " +
                "ORDER BY e1.EMPID DESC";

        submitSQLQuery(sqlQuery);

        System.out.println("✅ Task completed. Application will now exit.");
    }

    private void submitSQLQuery(String sqlQuery) {
        try {
            // Step 1: Generate webhook + token
            Map<String, String> requestBody = Map.of(
                    "name", "Isha Paradkar",
                    "regNo", "0002AL221024",
                    "email", "ishaparadkar12@gmail.com"
            );

            Map<String, Object> response = webClient.post()
                    .uri("/hiring/generateWebhook/JAVA")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null) {
                System.err.println("❌ Failed: Empty response from generateWebhook");
                return;
            }

            String webhookUrl = (String) response.get("webhook");
            String accessToken = (String) response.get("accessToken");

            System.out.println("Webhook URL: " + webhookUrl);
            System.out.println("Access Token: " + accessToken);

            // Step 2: Submit SQL query to returned webhook
            Map<String, String> finalRequest = Map.of("finalQuery", sqlQuery);

            Map<String, Object> finalResponse = webClient.post()
                    .uri(webhookUrl)
                    .header("Authorization", accessToken) // ✅ Correct: no "Bearer"
                    .header("Content-Type", "application/json")
                    .bodyValue(finalRequest)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            System.out.println("✅ Submission Response: " + finalResponse);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Error during submission: " + e.getMessage());
        }
    }
}
