package by.training.zaretskaya;

import by.training.zaretskaya.config.Configuration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskRestSpringApplication {

    public static void main(String[] args) {
        Configuration.startUp(System.getProperty("spring.application.name"));
        SpringApplication.run(TaskRestSpringApplication.class, args);
    }
}

