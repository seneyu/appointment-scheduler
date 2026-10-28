package com.seneyu.entity;

import com.seneyu.AppointmentStatus;
import com.seneyu.AppointmentType;
import jakarta.persistence.*;
import lombok.Builder;

import java.time.Instant;
import java.util.Set;

@Entity
@Builder
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant startTime;

    @Column(nullable = false)
    private Instant endTime;

    @Column(nullable = false)
    private Integer providerId;

    @ManyToOne(optional = false)
    @JoinColumn(name="patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false)
    private Integer roomId;

    @ElementCollection(targetClass = AppointmentType.class)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Set<AppointmentType> appointmentTypes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus appointmentStatus;

    public Appointment() {
    }

    public Appointment(Long id,
                       Instant startTime,
                       Instant endTime,
                       Integer providerId,
                       Patient patient,
                       Integer roomId,
                       Set<AppointmentType> appointmentTypes,
                       AppointmentStatus appointmentStatus) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.providerId = providerId;
        this.patient = patient;
        this.roomId = roomId;
        this.appointmentTypes = appointmentTypes;
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

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Set<AppointmentType> getAppointmentTypes() {
        return appointmentTypes;
    }

    public void setAppointmentTypes(Set<AppointmentType> appointmentTypes) {
        this.appointmentTypes = appointmentTypes;
    }

    public AppointmentStatus getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setAppointmentStatus(AppointmentStatus appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }
}