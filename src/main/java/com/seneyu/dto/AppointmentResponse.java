package com.seneyu.dto;

import com.seneyu.AppointmentStatus;
import com.seneyu.AppointmentType;
import lombok.Builder;

import java.time.Instant;
import java.util.Set;

@Builder
public record AppointmentResponse(
        Long id,
        Instant startTime,
        Instant endTime,
        Integer providerId,
        Long patientId,
        Integer roomId,
        Set<AppointmentType> appointmentTypes,
        AppointmentStatus appointmentStatus
) {
}
