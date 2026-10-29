package com.clinic.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.clinic.app.entities")
@EnableJpaRepositories(basePackages = "com.clinic.app.data.repositories")
public class ClinicApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClinicApiApplication.class, args);
    }
}