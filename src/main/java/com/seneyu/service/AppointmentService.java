package com.seneyu.service;

import com.seneyu.AppointmentStatus;
import com.seneyu.entity.Appointment;
import com.seneyu.repository.AppointmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public Appointment createAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Optional<Appointment> cancelAppointment(Long id) {
        Optional<Appointment> found = appointmentRepository.findById(id);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        Appointment appointment = found.get();
        appointment.setAppointmentStatus(AppointmentStatus.CANCELLED);
        return Optional.of(appointment);
    }
}
