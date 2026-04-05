package com.portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ProjectManagerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectManagerApiApplication.class, args);
    }

}
