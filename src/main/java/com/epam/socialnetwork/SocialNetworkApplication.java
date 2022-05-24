package com.epam.socialnetwork;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.epam"})
@EnableJpaRepositories("com.epam")
public class SocialNetworkApplication {
    public static void main(String[] args) {
        SpringApplication.run(SocialNetworkApplication.class, args);
    }
}
