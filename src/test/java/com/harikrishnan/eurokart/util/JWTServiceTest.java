package com.harikrishnan.eurokart.util;

import com.harikrishnan.eurokart.configuration.JWTConfiguration;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JWTServiceTest {


    private JWTService jwtService;

    @BeforeEach
    void setup () {
        JWTConfiguration configuration = new JWTConfiguration();
        configuration.setSecret("dGVzdC1zZWNyZXQta2V5LWZvci10ZXN0aW5nLXB1cnBvc2Vz");
        configuration.setExpiration(86400000L);
        jwtService = new JWTService(configuration);
    }


    @Test
   void getAccessToken_ShouldReturnValidToken () {
        String accessToken = jwtService.getAccessToken("test@gmail.com");
        assertThat(accessToken).isNotBlank();
        assertThat(accessToken).isNotNull();
   }

    @Test
   void  extractEmailFromToken_ShouldReturnCorrectEmail () {
        String accessToken = jwtService.getAccessToken("test@gmail.com");
        assertThat(jwtService.extractEmailFromToken(accessToken)).isEqualTo("test@gmail.com");
   }

    @Test
   void isAccessTokenValid_WithValidToken_ShouldReturnTrue () {
        String accessToken = jwtService.getAccessToken("test@gmail.com");
        assertThat(jwtService.isAccessTokenValid(accessToken,"test@gmail.com")).isEqualTo(true);
   }

    @Test
   void isAccessTokenValid_WithWrongEmail_ShouldReturnFalse () {
        String accessToken = jwtService.getAccessToken("test@gmail.com");
        assertThat(jwtService.isAccessTokenValid(accessToken,"anotherEmail@gmail.com")).isEqualTo(false);
   }

   @Test
   void isAccessTokenValid_WithExpiredToken_ShouldReturnFalse () {
       JWTConfiguration configuration = new JWTConfiguration();
       configuration.setSecret("dGVzdC1zZWNyZXQta2V5LWZvci10ZXN0aW5nLXB1cnBvc2Vz");
       configuration.setExpiration(1L);
       JWTService shotJwtService = new JWTService(configuration);
       String accessToken = shotJwtService.getAccessToken("test@gmail.com");
       assertThatThrownBy(() -> shotJwtService.isAccessTokenValid(accessToken,"test@gmail.com")).isInstanceOf(ExpiredJwtException.class);
   }

}
