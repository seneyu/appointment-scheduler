package com.seneyu.service;

import com.seneyu.AppointmentStatus;
import com.seneyu.dto.AppointmentRequest;
import com.seneyu.dto.AppointmentResponse;
import com.seneyu.entity.Appointment;
import com.seneyu.entity.Patient;
import com.seneyu.exception.AppointmentConflictException;
import com.seneyu.exception.ResourceNotFoundException;
import com.seneyu.mapper.AppointmentMapper;
import com.seneyu.repository.AppointmentRepository;
import com.seneyu.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public List<AppointmentResponse> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return appointmentMapper.toResponseList(appointments);
    }

    public AppointmentResponse getAppointmentById(Long id) {
        Appointment entityAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment " + id + " not found."));
        return appointmentMapper.toResponse(entityAppointment);
    }

    @Transactional
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        Long patientId = request.patientId();
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient " + patientId + " not found."));

        if (appointmentRepository.existsConflict(request.startTime(),
                request.endTime(),
                request.providerId(),
                request.roomId(),
                List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED),
                null)) {
            throw new AppointmentConflictException("Appointment overlap conflict.");
        }

        Appointment entityAppointment = appointmentMapper.toEntity(request, patient);
        Appointment savedAppointment = appointmentRepository.save(entityAppointment);
        return appointmentMapper.toResponse(savedAppointment);
    }

    @Transactional
    public AppointmentResponse updateAppointment(Long id, AppointmentRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment " + id + " not found."));

        Long patientId = request.patientId();
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient " + patientId + " not found."));

        if (appointmentRepository.existsConflict(request.startTime(),
                request.endTime(),
                request.providerId(),
                request.roomId(),
                List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED),
                appointment.getId())) {
            throw new AppointmentConflictException("Appointment overlap conflict.");
        }

        appointmentMapper.updateEntityFromRequest(appointment, request, patient);
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentResponse cancelAppointment(Long id) {
        Appointment found = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment " + id + " not found."));

        AppointmentStatus status = found.getAppointmentStatus();
        if (status == AppointmentStatus.COMPLETED) {
            throw new AppointmentConflictException("Appointment " + id + "'s current status " + found.getAppointmentStatus() + ". Cannot set to CANCELLED.");
        }

        found.setAppointmentStatus(AppointmentStatus.CANCELLED);
        return appointmentMapper.toResponse(found);
    }

    @Transactional
    public AppointmentResponse confirmAppointment(Long id) {
        Appointment found = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment " + id + " not found."));

        AppointmentStatus status = found.getAppointmentStatus();
        if (status != AppointmentStatus.SCHEDULED) {
            throw new AppointmentConflictException("Appointment " + id + "'s current status " + found.getAppointmentStatus() + ". Cannot set to CONFIRMED.");
        }

        found.setAppointmentStatus(AppointmentStatus.CONFIRMED);
        return appointmentMapper.toResponse(found);
    }

    @Transactional
    public AppointmentResponse completeAppointment(Long id) {
        Appointment found = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment " + id + " not found."));

        if (!List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED).contains(found.getAppointmentStatus())) {
            throw new AppointmentConflictException("Appointment " + id + "'s current status " + found.getAppointmentStatus() + ". Cannot set to COMPLETED.");
        }

        found.setAppointmentStatus(AppointmentStatus.COMPLETED);
        return appointmentMapper.toResponse(found);
    }

    public void deleteAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Appointment " + id + " not found."));
        appointmentRepository.delete(appointment);
    }
}
