package com.capitec.deportment.appointment.dto;

import java.time.LocalDateTime;

public record AppointmentRequest(
        String appointmentType,
        Integer userId,
        LocalDateTime appointmentDateTime,
        String createdBy
) {}