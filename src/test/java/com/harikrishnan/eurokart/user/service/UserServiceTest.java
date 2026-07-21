package com.harikrishnan.eurokart.user.service;

import com.harikrishnan.eurokart.exception.ConflictException;
import com.harikrishnan.eurokart.user.domain.User;
import com.harikrishnan.eurokart.user.dto.AuthResponseDto;
import com.harikrishnan.eurokart.user.dto.UserRequestDto;
import com.harikrishnan.eurokart.user.dto.UserResponseDto;
import com.harikrishnan.eurokart.user.repository.UserRepository;
import com.harikrishnan.eurokart.util.JWTService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JWTService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;


    @Test
    void registerUser_WithValidRequest_ShouldReturnUserResponseDto () {

        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email("test@gmail.com")
                .password("password")
                .build();

        User user = User.builder()
                .email("test@gmail.com")
                .passwordHash("abcdef1234")
                .role("USER")
                .build();
        when(userRepository.existsUserByEmail(any(String.class))).thenReturn(false);
        when(passwordEncoder.encode(any(String.class))).thenReturn("abcdef1234");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDto userResponseDto = userService.registerUser(userRequestDto);
        assertThat(userResponseDto.getEmail()).isEqualTo(userRequestDto.getEmail());
        assertThat(userResponseDto.getMessage()).isEqualTo("The user registration has been successful");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_WithExistingEmail_ShouldThrowConflictException () {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email("test@gmail.com")
                .password("password")
                .build();
        when(userRepository.existsUserByEmail(any(String.class))).thenReturn(true);
        assertThatThrownBy(() -> userService.registerUser(userRequestDto)).isInstanceOf(ConflictException.class);
    }

    @Test
    void authenticateUser_WithValidCredentials_ShouldReturnAuthResponseDto () {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email("test@gmail.com")
                .password("password")
                .build();

        when(jwtService.getAccessToken(any(String.class))).thenReturn("abcd");
        AuthResponseDto authResponseDto = userService.authenticateUser(userRequestDto);
        assertThat(authResponseDto.getToken()).isEqualTo("abcd");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

}
