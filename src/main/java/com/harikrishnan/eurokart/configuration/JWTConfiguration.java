package com.harikrishnan.eurokart.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "jwt")
public class JWTConfiguration {
    private String secret;
    private Long expiration;
}
