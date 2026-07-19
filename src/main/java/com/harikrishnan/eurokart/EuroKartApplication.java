package com.harikrishnan.eurokart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
public class EuroKartApplication {

    public static void main(String[] args) {
        SpringApplication.run(EuroKartApplication.class, args);
    }

}

