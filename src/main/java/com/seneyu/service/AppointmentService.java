package com.seneyu.service;

import com.seneyu.AppointmentStatus;
import com.seneyu.dto.AppointmentRequest;
import com.seneyu.dto.AppointmentResponse;
import com.seneyu.entity.Appointment;
import com.seneyu.entity.Patient;
import com.seneyu.exception.ResourceNotFoundException;
import com.seneyu.mapper.AppointmentMapper;
import com.seneyu.repository.AppointmentRepository;
import com.seneyu.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final AppointmentMapper appointmentMapper;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              AppointmentMapper appointmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.appointmentMapper = appointmentMapper;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment " + id + " not found."));
    }

    public AppointmentResponse createAppointment(AppointmentRequest request) {
        Long patientId = request.patientId();
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient " + patientId + " not found."));

        Appointment entityAppointment = appointmentMapper.toEntity(request, patient);
        Appointment savedAppointment = appointmentRepository.save(entityAppointment);
        return appointmentMapper.toResponse(savedAppointment);
    }

    public AppointmentResponse updateAppointment(Long id, AppointmentRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment " + id + " not found."));

        Long patientId = request.patientId();
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient " + patientId + " not found."));

        appointmentMapper.updateEntityFromRequest(appointment, request, patient);
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
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
