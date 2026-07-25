package com.harikrishnan.eurokart.util;

import com.harikrishnan.eurokart.exception.ResourceNotFoundException;
import com.harikrishnan.eurokart.user.domain.User;
import com.harikrishnan.eurokart.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityUtils {

    private final UserRepository userRepository;

    public User getCurrentUser () {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        log.info("Email identified is: {}",email);
        User user = userRepository.findUserByEmail(email);
        if(user == null) {
            throw new ResourceNotFoundException("Unable to find user with email:" + email);
        }
        return user;
    }
}
