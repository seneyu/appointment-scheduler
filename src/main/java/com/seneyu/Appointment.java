package com.seneyu;

import java.time.Instant;
import java.util.Set;

public class Appointment {
    private Long id;
    private Instant startTime;
    private Instant endTime;
    private Integer providerId;
    private Long patientId;
    private Integer roomId;
    private Set<AppointmentTypes> appointmentType;
    private AppointmentStatus appointmentStatus;

    public Appointment() {
    }

    public Appointment(Long id,
                       Instant startTime,
                       Instant endTime,
                       Integer providerId,
                       Long patientId,
                       Integer roomId,
                       Set<AppointmentTypes> appointmentType,
                       AppointmentStatus appointmentStatus) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.providerId = providerId;
        this.patientId = patientId;
        this.roomId = roomId;
        this.appointmentType = appointmentType;
        this.appointmentStatus = appointmentStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Set<AppointmentTypes> getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(Set<AppointmentTypes> appointmentType) {
        this.appointmentType = appointmentType;
    }

    public AppointmentStatus getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setAppointmentStatus(AppointmentStatus appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }
}