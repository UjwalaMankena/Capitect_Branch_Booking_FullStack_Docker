package com.capitec.deportment.appointment.service;

import com.capitec.deportment.appointment.dto.LoginRequest;
import com.capitec.deportment.appointment.dto.LoginResponse;
import com.capitec.deportment.appointment.entity.UserEntity;
import com.capitec.deportment.appointment.repository.LoginRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LoginService {

    private static final Logger log =
            LoggerFactory.getLogger(LoginService.class);

    private final LoginRepository loginRepository;
    //private final PasswordEncoder passwordEncoder;
    //private final String passwordEncoder;
    private final JwtService jwtService;

    public LoginService(
            LoginRepository loginRepository,
           // String passwordEncoder,
            JwtService jwtService) {

        this.loginRepository = loginRepository;
       // this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse authenticate(LoginRequest request) {

        log.info("Authentication process started");

        // Step 1: Find user by email
        UserEntity user = loginRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Authentication failed: User not found");

                    return new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED,
                            "Invalid email or password"
                    );
                });

        // Step 2: Validate password
       /* if (!passwordEncoder.matches(
                request.password(), user.getPassword())) { */
        if(!(request.password().equals(user.getPassword()))){

            log.warn("Authentication failed: Invalid credentials");

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

        log.info("User credentials validated successfully. User ID: {}",
                user.getId());

        // Step 3: Generate JWT token
        String token = jwtService.generateToken(user);

        // Step 4: Return login response with JWT
        log.info("Authentication completed successfully");

        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                token,
                "Bearer",
                3600,
                "Login successful"
        );
    }
}