package com.example.salon.repository;

import com.example.salon.entity.Appointment;
import com.example.salon.entity.Barber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findAllByOrderByAppointmentDateTimeDesc();

    List<Appointment> findByBarberAndAppointmentDateTimeBetween(
            Barber barber, LocalDateTime start, LocalDateTime end);
}
