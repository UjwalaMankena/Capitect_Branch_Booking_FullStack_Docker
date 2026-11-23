package com.capitec.deportment.appointment.controller;

import com.capitec.deportment.appointment.dto.LoginRequest;
import com.capitec.deportment.appointment.dto.LoginResponse;
import com.capitec.deportment.appointment.service.LoginService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private static final Logger log =
            LoggerFactory.getLogger(LoginController.class);

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        log.info("Login API invoked");

        LoginResponse response =
                loginService.authenticate(request);

        log.info("Login API execution completed successfully");

        return ResponseEntity.ok(response);
    }
}