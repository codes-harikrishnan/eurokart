package com.harikrishnan.eurokart.util;

import com.harikrishnan.eurokart.configuration.JWTConfiguration;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JWTService {

    private final JWTConfiguration jwtConfiguration;

    private SecretKey getSecretKey () {
            return Keys.hmacShaKeyFor(jwtConfiguration.getSecret().getBytes());
    }

    private String generateToken (String subject, Map<String , String> claims, Long expiration) {

        return Jwts.builder()
                .signWith(getSecretKey())
                .subject(subject)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .compact();
    }

    private Claims getClaimsFromToken (String token) {
        return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload();
    }

    public String extractEmailFromToken (String token) {
       return getClaimsFromToken(token).getSubject();
    }

    private Date getExpirationDateFromToken (String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    public String getAccessToken (String email) {
        return generateToken(email,Map.of(), jwtConfiguration.getExpiration());
    }

    public boolean isAccessTokenValid (String token, String email) {
       return extractEmailFromToken(token).equals(email) && getExpirationDateFromToken(token).after(new Date());
    }

}
