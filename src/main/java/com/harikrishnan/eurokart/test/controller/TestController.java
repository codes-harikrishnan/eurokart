package com.harikrishnan.eurokart.test.controller;

import com.harikrishnan.eurokart.test.service.TestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
@Slf4j
public class TestController {

    private final TestService testService;

    @GetMapping("/{id}")
    public ResponseEntity<String> testEndpoint(@Valid @PathVariable Long id) {
        log.info("Received test endpoint call with id: {}",id);
            return ResponseEntity.status(HttpStatus.OK).body(testService.simulateTestCondition(id));
    }

}
