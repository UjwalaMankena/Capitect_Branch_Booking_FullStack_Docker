package com.capitec.deportment.appointment.dto;

public record LoginResponse(
        Integer userId,
        String name,
        String email,
        String token,
        String tokenType,
        long expiresIn,
        String message
) {}