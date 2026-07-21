package com.harikrishnan.eurokart.user.service;

import com.harikrishnan.eurokart.exception.ConflictException;
import com.harikrishnan.eurokart.user.domain.User;
import com.harikrishnan.eurokart.user.dto.AuthResponseDto;
import com.harikrishnan.eurokart.user.dto.UserRequestDto;
import com.harikrishnan.eurokart.user.dto.UserResponseDto;
import com.harikrishnan.eurokart.user.repository.UserRepository;
import com.harikrishnan.eurokart.util.JWTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final JWTService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;


    public UserResponseDto registerUser (UserRequestDto userRequestDto) {
        if (userRepository.existsUserByEmail(userRequestDto.getEmail())) {
            throw new ConflictException("A user is already registered with the given email id: "+ userRequestDto.getEmail());
        }

        User user = User.builder()
                .email(userRequestDto.getEmail())
                .passwordHash(passwordEncoder.encode(userRequestDto.getPassword()))
                .role("USER")
                .build();

       User registeredUser = userRepository.save(user);
       return UserResponseDto.builder()
               .email(registeredUser.getEmail())
               .message("The user registration has been successful")
               .build();

    }

    public AuthResponseDto authenticateUser ( UserRequestDto userRequestDto) {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRequestDto.getEmail(),
              userRequestDto.getPassword()));

           return AuthResponseDto.builder()
                   .token(jwtService.getAccessToken(userRequestDto.getEmail()))
                   .build();
       }

}
