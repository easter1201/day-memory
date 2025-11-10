package com.daymemory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DayMemoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DayMemoryApplication.class, args);
    }
}
