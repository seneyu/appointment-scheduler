package com.seneyu.dto;

import com.seneyu.AppointmentType;

import java.time.Instant;
import java.util.Set;

public record AppointmentRequest(
        Instant startTime,
        Instant endTime,
        Integer providerId,
        Long patientId,
        Integer roomId,
        Set<AppointmentType> appointmentTypes
) {
}
