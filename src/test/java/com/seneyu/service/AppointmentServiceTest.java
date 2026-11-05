package com.seneyu.service;

import com.seneyu.AppointmentStatus;
import com.seneyu.AppointmentType;
import com.seneyu.dto.AppointmentRequest;
import com.seneyu.dto.AppointmentResponse;
import com.seneyu.entity.Appointment;
import com.seneyu.entity.Patient;
import com.seneyu.exception.AppointmentConflictException;
import com.seneyu.exception.ResourceNotFoundException;
import com.seneyu.mapper.AppointmentMapper;
import com.seneyu.repository.AppointmentRepository;
import com.seneyu.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Appointment Service Tests")
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private AppointmentMapper appointmentMapper;

    @InjectMocks
    private AppointmentService appointmentService;

    private Appointment testAppointment;
    private Patient testPatient;
    private AppointmentRequest testAppointmentRequest;
    private AppointmentResponse testAppointmentResponse;

    @BeforeEach
    void setUp() {

        this.testPatient = Patient.builder()
                .id(123L)
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.parse("2000-04-18"))
                .phone("1274958888")
                .email("johndoe@email.com")
                .build();

        this.testAppointment = Appointment.builder()
                .id(10L)
                .startTime(Instant.parse("2027-11-27T09:00:00Z"))
                .endTime(Instant.parse("2027-11-27T09:30:00Z"))
                .providerId(3)
                .patient(testPatient)
                .roomId(1)
                .appointmentTypes(Set.of(AppointmentType.EXAM, AppointmentType.CLEANING))
                .appointmentStatus(AppointmentStatus.SCHEDULED)
                .build();

        this.testAppointmentRequest = AppointmentRequest.builder()
                .startTime(Instant.parse("2027-11-27T09:00:00Z"))
                .endTime(Instant.parse("2027-11-27T09:30:00Z"))
                .providerId(3)
                .patientId(123L)
                .roomId(1)
                .appointmentTypes(Set.of(AppointmentType.EXAM, AppointmentType.CLEANING))
                .build();

        this.testAppointmentResponse = AppointmentResponse.builder()
                .id(10L)
                .startTime(Instant.parse("2027-11-27T09:00:00Z"))
                .endTime(Instant.parse("2027-11-27T09:30:00Z"))
                .providerId(3)
                .patientId(123L)
                .roomId(1)
                .appointmentTypes(Set.of(AppointmentType.EXAM, AppointmentType.CLEANING))
                .appointmentStatus(AppointmentStatus.SCHEDULED)
                .build();
    }

    @Nested
    @DisplayName("Create Appointment Tests")
    class createAppointmentTests {

        @Test
        @DisplayName("Should create a new appointment successfully with valid request")
        void shouldCreateAppointment_whenRequestIsValid() {
            when(patientRepository.findById(testAppointmentRequest.patientId())).thenReturn(Optional.of(testPatient));
            when(appointmentRepository.existsConflict(any(), any(), any(), any(), anyList(), isNull())).thenReturn(false);
            when(appointmentMapper.toEntity(testAppointmentRequest, testPatient)).thenReturn(testAppointment);
            when(appointmentRepository.save(testAppointment)).thenReturn(testAppointment);
            when(appointmentMapper.toResponse(any(Appointment.class))).thenReturn(testAppointmentResponse);

            final AppointmentResponse result = AppointmentServiceTest.this.appointmentService.createAppointment(testAppointmentRequest);

            assertNotNull(result);
            assertEquals(testAppointmentResponse, result);

            // verify the times of methods have been called
            verify(patientRepository, times(1)).findById(testAppointmentRequest.patientId());
            verify(appointmentMapper, times(1)).toEntity(testAppointmentRequest, testPatient);
            verify(appointmentRepository, times(1)).save(testAppointment);
            verify(appointmentMapper, times(1)).toResponse(any(Appointment.class));

            // verify appointment is created
            verify(appointmentRepository).save(argThat(
                    appointment -> appointment.getId() != null && appointment.getId().equals(10L)));
        }

        @Test
        @DisplayName("Should throw when patient ID not found")
        void shouldThrow_whenPatientNotFound() {
            when(patientRepository.findById(testAppointmentRequest.patientId())).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> appointmentService.createAppointment(testAppointmentRequest));

            verify(appointmentRepository, never()).existsConflict(any(), any(), any(), any(), anyList(), isNull());
            verify(appointmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when an appointment conflict exists")
        void shouldThrow_whenExistsConflict() {
            when(patientRepository.findById(testAppointmentRequest.patientId())).thenReturn(Optional.of(testPatient));
            when(appointmentRepository.existsConflict(any(), any(), any(), any(), anyList(), isNull())).thenReturn(true);

            assertThrows(AppointmentConflictException.class, () -> appointmentService.createAppointment(testAppointmentRequest));

            verify(appointmentRepository, never()).save(any());
        }
    }
}