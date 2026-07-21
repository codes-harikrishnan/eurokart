package com.harikrishnan.eurokart.user.controller;

import com.harikrishnan.eurokart.user.dto.AuthResponseDto;
import com.harikrishnan.eurokart.user.dto.UserRequestDto;
import com.harikrishnan.eurokart.user.dto.UserResponseDto;
import com.harikrishnan.eurokart.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser (@Valid @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(userRequestDto));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponseDto> authenticateUser (@Valid @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.authenticateUser(userRequestDto));
    }

}
