package com.rolematch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * RoleMatch Enterprise Application Entry Point.
 */
@SpringBootApplication
@EnableJpaAuditing
public class RoleMatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(RoleMatchApplication.class, args);
    }
}
