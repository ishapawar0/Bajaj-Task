package isha.api.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.WebApplicationType;

@SpringBootApplication
public class TaskApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TaskApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE); // no web server needed
        app.run(args);
    }
}
