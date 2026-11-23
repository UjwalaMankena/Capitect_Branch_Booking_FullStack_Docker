package com.capitec.deportment.appointment.controller;

import com.capitec.deportment.appointment.dto.AppointmentRequest;
import com.capitec.deportment.appointment.dto.AppointmentResponse;
import com.capitec.deportment.appointment.service.AppointmentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private static final Logger log =
            LoggerFactory.getLogger(AppointmentController.class);

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(
            @RequestBody AppointmentRequest request) {

        log.info("Create appointment API invoked");

        AppointmentResponse response =
                appointmentService.createAppointment(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getAppointmentById(
            @PathVariable Integer id) {

        log.info("Get appointment API invoked. ID: {}", id);

        return ResponseEntity.ok(
                appointmentService.getAppointmentById(id));
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {

        log.info("Get all appointments API invoked");

        return ResponseEntity.ok(
                appointmentService.getAllAppointments());
    }

    // GET BY USER ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByUserId(
            @PathVariable Integer userId) {

        log.info("Get appointments by user ID API invoked: {}", userId);

        return ResponseEntity.ok(
                appointmentService.getAppointmentsByUserId(userId));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponse> updateAppointment(
            @PathVariable Integer id,
            @RequestBody AppointmentRequest request) {

        log.info("Update appointment API invoked. ID: {}", id);

        return ResponseEntity.ok(
                appointmentService.updateAppointment(id, request));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(
            @PathVariable Integer id) {

        log.info("Delete appointment API invoked. ID: {}", id);

        appointmentService.deleteAppointment(id);

        return ResponseEntity.noContent().build();
    }
}