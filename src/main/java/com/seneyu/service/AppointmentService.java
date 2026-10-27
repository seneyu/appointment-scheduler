package com.seneyu.service;

import com.seneyu.AppointmentStatus;
import com.seneyu.entity.Appointment;
import com.seneyu.exception.ResourceNotFoundException;
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

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment " + id + " not found."));
    }

    public Appointment createAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(Long id, Appointment appointmentDetails) {
        Appointment updatedAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment " + id + " not found."));

        updatedAppointment.setStartTime(appointmentDetails.getStartTime());
        updatedAppointment.setEndTime(appointmentDetails.getEndTime());
        updatedAppointment.setProviderId(appointmentDetails.getProviderId());
        updatedAppointment.setPatient(appointmentDetails.getPatient());
        updatedAppointment.setRoomId(appointmentDetails.getRoomId());
        updatedAppointment.setAppointmentTypes(appointmentDetails.getAppointmentTypes());
        updatedAppointment.setAppointmentStatus(appointmentDetails.getAppointmentStatus());
        return appointmentRepository.save(updatedAppointment);
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

    public void deleteAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Appointment " + id + " not found."));
        appointmentRepository.delete(appointment);
    }
}
