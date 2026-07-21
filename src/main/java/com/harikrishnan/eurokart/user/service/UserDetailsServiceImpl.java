package com.harikrishnan.eurokart.user.service;

import com.harikrishnan.eurokart.user.domain.User;
import com.harikrishnan.eurokart.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if(!userRepository.existsUserByEmail(username)) {
            log.error("Unable to find user with email: {}", username);
            throw new UsernameNotFoundException("Unable to find user with email: " + username);
        }

        User userDetails = userRepository.findUserByEmail(username);

       return  new org.springframework.security.core.userdetails.User(userDetails.getEmail(),userDetails.getPasswordHash(), List.of(new SimpleGrantedAuthority("ROLE_"+userDetails.getRole())));

    }
}

