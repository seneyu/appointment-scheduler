package com.seneyu.mapper;

import com.seneyu.AppointmentStatus;
import com.seneyu.dto.AppointmentRequest;
import com.seneyu.dto.AppointmentResponse;
import com.seneyu.entity.Appointment;
import com.seneyu.entity.Patient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AppointmentMapper {

    public Appointment toEntity(AppointmentRequest request, Patient patient) {
        return Appointment.builder()
                .startTime(request.startTime())
                .endTime(request.endTime())
                .providerId(request.providerId())
                .patient(patient)
                .roomId(request.roomId())
                .appointmentTypes(request.appointmentTypes())
                .appointmentStatus(AppointmentStatus.SCHEDULED)
                .build();
    }

    public AppointmentResponse toResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .providerId(appointment.getProviderId())
                .patientId(appointment.getPatient().getId())
                .roomId(appointment.getRoomId())
                .appointmentTypes(appointment.getAppointmentTypes())
                .appointmentStatus(appointment.getAppointmentStatus())
                .build();
    }

    public void updateEntityFromRequest(Appointment appointment, AppointmentRequest request, Patient patient) {
        appointment.setStartTime(request.startTime());
        appointment.setEndTime(request.endTime());
        appointment.setProviderId(request.providerId());
        appointment.setPatient(patient);
        appointment.setRoomId(request.roomId());
        appointment.setAppointmentTypes(request.appointmentTypes());
    }

    public List<AppointmentResponse> toResponseList(List<Appointment> appointments) {
        return appointments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
