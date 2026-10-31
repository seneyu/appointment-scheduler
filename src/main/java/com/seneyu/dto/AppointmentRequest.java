package com.seneyu.dto;

import com.seneyu.AppointmentType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Set;

public record AppointmentRequest(

        @NotNull(message = "Start time is required")
        @Future(message = "Start time must be in the future")
        Instant startTime,

        @NotNull(message = "End time is required")
        @Future(message = "End time must be in the future")
        Instant endTime,

        @NotNull(message = "Provider ID is required")
        Integer providerId,

        @NotNull(message = "Patient ID is required")
        Long patientId,

        @NotNull(message = "Room ID is required")
        Integer roomId,

        @NotEmpty(message = "At least one appointment type is required")
        Set<AppointmentType> appointmentTypes
) {
}
