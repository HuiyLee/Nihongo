package com.example.japanese;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class JapaneseLearningApplication {

    public static void main(String[] args) {
        SpringApplication.run(JapaneseLearningApplication.class, args);
    }
}
