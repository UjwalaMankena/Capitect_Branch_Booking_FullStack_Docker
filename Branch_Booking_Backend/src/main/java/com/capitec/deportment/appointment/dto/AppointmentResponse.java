package com.capitec.deportment.appointment.dto;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Integer id,
        String appointmentType,
        Integer userId,
        LocalDateTime appointmentDateTime,
        LocalDateTime createdDate,
        String message
) {}