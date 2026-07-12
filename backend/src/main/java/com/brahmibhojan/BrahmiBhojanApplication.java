package com.brahmibhojan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BrahmiBhojanApplication {

    public static void main(String[] args) {
        SpringApplication.run(BrahmiBhojanApplication.class, args);
    }
}

