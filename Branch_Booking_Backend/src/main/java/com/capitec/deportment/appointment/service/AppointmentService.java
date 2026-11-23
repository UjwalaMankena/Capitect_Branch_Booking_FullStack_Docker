package com.capitec.deportment.appointment.service;

import com.capitec.deportment.appointment.dto.AppointmentRequest;
import com.capitec.deportment.appointment.dto.AppointmentResponse;
import com.capitec.deportment.appointment.entity.AppointmentEntity;
import com.capitec.deportment.appointment.repository.AppointmentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AppointmentService {

    private static final Logger log =
            LoggerFactory.getLogger(AppointmentService.class);

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    // CREATE APPOINTMENT
    public AppointmentResponse createAppointment(AppointmentRequest request) {

        log.info("Creating appointment for user ID: {}", request.userId());

        AppointmentEntity appointment = new AppointmentEntity();

        appointment.setAppointmentType(request.appointmentType());
        appointment.setUserId(request.userId());
        appointment.setAppointmentDateTime(request.appointmentDateTime());
        appointment.setCreatedDate(LocalDateTime.now());
        appointment.setCreatedBy(request.createdBy());

        AppointmentEntity saved =
                appointmentRepository.save(appointment);

        log.info("Appointment created successfully. Appointment ID: {}",
                saved.getId());

        return mapToResponse(saved, "Appointment created successfully");
    }

    // GET APPOINTMENT BY ID
    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(Integer id) {

        log.info("Fetching appointment ID: {}", id);

        AppointmentEntity appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Appointment not found. ID: {}", id);

                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Appointment not found"
                    );
                });

        return mapToResponse(appointment, "Appointment retrieved successfully");
    }

    // GET ALL APPOINTMENTS
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAllAppointments() {

        log.info("Fetching all appointments");

        return appointmentRepository.findAll()
                .stream()
                .map(a -> mapToResponse(a, "Appointment retrieved successfully"))
                .toList();
    }

    // GET APPOINTMENTS BY USER ID
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByUserId(Integer userId) {

        log.info("Fetching appointments for user ID: {}", userId);

        return appointmentRepository.findByUserId(userId)
                .stream()
                .map(a -> mapToResponse(a, "Appointment retrieved successfully"))
                .toList();
    }

    // UPDATE APPOINTMENT
    public AppointmentResponse updateAppointment(
            Integer id, AppointmentRequest request) {

        log.info("Updating appointment ID: {}", id);

        AppointmentEntity appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Appointment not found"
                ));

        appointment.setAppointmentType(request.appointmentType());
        appointment.setAppointmentDateTime(request.appointmentDateTime());
        appointment.setUpdatedBy(request.createdBy());
        appointment.setUpdatedDate(LocalDateTime.now());

        AppointmentEntity updated =
                appointmentRepository.save(appointment);

        log.info("Appointment updated successfully. ID: {}", id);

        return mapToResponse(updated, "Appointment updated successfully");
    }

    // DELETE APPOINTMENT
    public void deleteAppointment(Integer id) {

        log.info("Deleting appointment ID: {}", id);

        if (!appointmentRepository.existsById(id)) {
            log.warn("Cannot delete. Appointment not found. ID: {}", id);

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Appointment not found"
            );
        }

        appointmentRepository.deleteById(id);

        log.info("Appointment deleted successfully. ID: {}", id);
    }

    // ENTITY TO RESPONSE DTO MAPPING
    private AppointmentResponse mapToResponse(
            AppointmentEntity appointment, String message) {

        return new AppointmentResponse(
                appointment.getId(),
                appointment.getAppointmentType(),
                appointment.getUserId(),
                appointment.getAppointmentDateTime(),
                appointment.getCreatedDate(),
                message
        );
    }
}