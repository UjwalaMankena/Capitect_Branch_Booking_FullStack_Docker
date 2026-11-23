package com.capitec.deportment.appointment.repository;

import com.capitec.deportment.appointment.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository
        extends JpaRepository<AppointmentEntity, Integer> {

    List<AppointmentEntity> findByUserId(Integer userId);
}