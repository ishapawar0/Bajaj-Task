package isha.api.task;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class WebhookTaskRunner implements CommandLineRunner {

    private final WebClient webClient;

    public WebhookTaskRunner(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    @Override
    public void run(String... args) throws Exception {
        // First SQL query (Question 1)
        String query1 = "SELECT p.AMOUNT AS SALARY, " +
                "CONCAT(e.FIRSTNAME, ' ', e.LASTNAME) AS NAME, " +
                "TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, " +
                "d.DEPARTMENTNAME " +
                "FROM PAYMENTS p " +
                "JOIN EMPLOYEE e ON p.EMPID = e.EMPID " +
                "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENTID " +
                "WHERE p.AMOUNT = ( " +
                "SELECT MAX(AMOUNT) FROM PAYMENTS WHERE DAY(PAYMENTTIME) <> 1 " +
                ") AND DAY(p.PAYMENTTIME) <> 1";

        // Second SQL query (Question 2)
        String query2 = "SELECT " +
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

        submitSQLQuery(query1);
        submitSQLQuery(query2);
    }

    private void submitSQLQuery(String sqlQuery) {
        Map<String, String> requestBody = Map.of(
                "name", "John Doe",
                "regNo", "REG12347",
                "email", "john@example.com"
        );

        Map<String, Object> response = webClient.post()
                .uri("/hiring/generateWebhook/JAVA")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        String webhookUrl = (String) response.get("webhook");
        String accessToken = (String) response.get("accessToken");

        System.out.println("Webhook URL: " + webhookUrl);
        System.out.println("Access Token: " + accessToken);

        Map<String, String> finalRequest = Map.of("finalQuery", sqlQuery);

        Map<String, Object> finalResponse = webClient.post()
                .uri(webhookUrl)
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .bodyValue(finalRequest)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        System.out.println("Submission Response: " + finalResponse);
    }
}
