package com.loki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableJpaRepositories("com.loki.**")
@EnableScheduling
public class BiMarketingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BiMarketingApplication.class, args);
    }

}
