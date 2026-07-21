package com.harikrishnan.eurokart.configuration;

import com.harikrishnan.eurokart.util.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JWTService jwtService;

    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("Verifying authorization");
        String authorization = request.getHeader("Authorization");

        if(authorization == null || !authorization.startsWith("Bearer ")) {
            log.info("Unable to find authorization in request header or not with the correct format");
            filterChain.doFilter(request,response);
            return ;
        }

        String token = authorization.substring(7);
        log.info("Found token");

        try {
            String email = jwtService.extractEmailFromToken(token);
            log.info("Email extracted from token : {}", email);
            if(jwtService.isAccessTokenValid(token,email) && SecurityContextHolder.getContext().getAuthentication() == null) {
                SecurityContext context = SecurityContextHolder.getContext();

                UserDetails user = userDetailsService.loadUserByUsername(email);
                log.info("Authenticating user credentials");
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
                context.setAuthentication(usernamePasswordAuthenticationToken);
                filterChain.doFilter(request,response);
            }
        } catch (Exception e) {
           log.error("Error in processing user details");
        }



    }
}
