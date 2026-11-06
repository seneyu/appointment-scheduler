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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
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
    @DisplayName("Get All Appointments Tests")
    class GetAllAppointmentsTests {

        @Test
        @DisplayName("Should get all appointments")
        void shouldGetAllAppointments() {
            when(appointmentRepository.findAll()).thenReturn(List.of(testAppointment));
            when(appointmentMapper.toResponseList(List.of(testAppointment))).thenReturn(List.of(testAppointmentResponse));

            final List<AppointmentResponse> resultList = appointmentService.getAllAppointments();

            assertEquals(List.of(testAppointmentResponse), resultList);
        }
    }

    @Nested
    @DisplayName("Get Appointment By Id Tests")
    class GetAppointmentByIdTests {

        @Test
        @DisplayName("Should get appointment by ID successfully with valid request")
        void shouldGetAppointment_whenRequestIsValid() {
            when(appointmentRepository.findById(10L)).thenReturn(Optional.of(testAppointment));
            when(appointmentMapper.toResponse(testAppointment)).thenReturn(testAppointmentResponse);

            final AppointmentResponse result = appointmentService.getAppointmentById(10L);

            assertNotNull(result);
            assertEquals(testAppointmentResponse, result);

            verify(appointmentMapper, times(1)).toResponse(testAppointment);
        }

        @Test
        @DisplayName("Should throw when appointment ID not found")
        void shouldThrow_whenAppointmentNotFound() {
            when(appointmentRepository.findById(10L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> appointmentService.getAppointmentById(10L));

            verifyNoInteractions(appointmentMapper);
        }
    }

    @Nested
    @DisplayName("Create Appointment Tests")
    class CreateAppointmentTests {

        @Test
        @DisplayName("Should create a new appointment successfully with valid request")
        void shouldCreateAppointment_whenRequestIsValid() {
            when(patientRepository.findById(testAppointmentRequest.patientId())).thenReturn(Optional.of(testPatient));
            when(appointmentRepository.existsConflict(any(), any(), any(), any(), anyList(), isNull())).thenReturn(false);
            when(appointmentMapper.toEntity(testAppointmentRequest, testPatient)).thenReturn(testAppointment);
            when(appointmentRepository.save(testAppointment)).thenReturn(testAppointment);
            when(appointmentMapper.toResponse(any(Appointment.class))).thenReturn(testAppointmentResponse);

            final AppointmentResponse result = appointmentService.createAppointment(testAppointmentRequest);

            assertNotNull(result);
            assertEquals(testAppointmentResponse, result);

            // verify the times of methods have been called
            verify(patientRepository, times(1)).findById(testAppointmentRequest.patientId());
            verify(appointmentMapper, times(1)).toEntity(testAppointmentRequest, testPatient);
            verify(appointmentRepository, times(1)).save(testAppointment);
            verify(appointmentMapper, times(1)).toResponse(any(Appointment.class));
        }

        @Test
        @DisplayName("Should throw when patient ID not found")
        void shouldThrow_whenPatientNotFound() {
            when(patientRepository.findById(testAppointmentRequest.patientId())).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> appointmentService.createAppointment(testAppointmentRequest));

            verifyNoInteractions(appointmentMapper);
            verifyNoInteractions(appointmentRepository);
        }

        @Test
        @DisplayName("Should throw when an appointment conflict exists")
        void shouldThrow_whenExistsConflict() {
            when(patientRepository.findById(testAppointmentRequest.patientId())).thenReturn(Optional.of(testPatient));
            when(appointmentRepository.existsConflict(any(), any(), any(), any(), anyList(), isNull())).thenReturn(true);

            assertThrows(AppointmentConflictException.class, () -> appointmentService.createAppointment(testAppointmentRequest));

            verify(appointmentRepository, never()).save(any());

            verifyNoInteractions(appointmentMapper);
        }
    }

    @Nested
    @DisplayName("Update Appointment Tests")
    class UpdateAppointmentTests {

        @Test
        @DisplayName("Should update appointment successfully with valid request")
        void shouldUpdateAppointment_whenRequestIsValid() {
            when(appointmentRepository.findById(10L)).thenReturn(Optional.of(testAppointment));
            when(patientRepository.findById(testAppointment.getPatient().getId())).thenReturn(Optional.of(testPatient));
            when(appointmentRepository.existsConflict(any(), any(), any(), any(), anyList(), eq(testAppointment.getId()))).thenReturn(false);
            when(appointmentRepository.save(testAppointment)).thenReturn(testAppointment);
            when(appointmentMapper.toResponse(testAppointment)).thenReturn(testAppointmentResponse);

            final AppointmentResponse result = appointmentService.updateAppointment(10L, testAppointmentRequest);

            assertNotNull(result);
            assertEquals(testAppointmentResponse, result);

            verify(appointmentRepository, times(1)).findById(10L);
            verify(patientRepository, times(1)).findById(testAppointment.getPatient().getId());
            verify(appointmentRepository, times(1)).existsConflict(any(), any(), any(), any(), anyList(), eq(testAppointment.getId()));
            verify(appointmentMapper).updateEntityFromRequest(testAppointment, testAppointmentRequest, testPatient);
            verify(appointmentRepository, times(1)).save(testAppointment);
            verify(appointmentMapper, times(1)).toResponse(any(Appointment.class));
        }

        @Test
        @DisplayName("Should throw when appointment ID not found")
        void shouldThrow_whenAppointmentNotFound() {
            when(appointmentRepository.findById(10L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> appointmentService.updateAppointment(10L, testAppointmentRequest));

            verify(appointmentRepository, never()).existsConflict(any(), any(), any(), any(), anyList(), eq(testAppointment.getId()));
            verify(appointmentRepository, never()).save(testAppointment);

            verifyNoInteractions(patientRepository);
            verifyNoInteractions(appointmentMapper);
        }

        @Test
        @DisplayName("Should throw when patient ID not found")
        void shouldThrow_whenPatientNotFound() {
            when(appointmentRepository.findById(10L)).thenReturn(Optional.of(testAppointment));
            when(patientRepository.findById(testAppointmentRequest.patientId())).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> appointmentService.updateAppointment(10L, testAppointmentRequest));

            verify(appointmentRepository, never()).existsConflict(any(), any(), any(), any(), anyList(), eq(testAppointment.getId()));
            verify(appointmentRepository, never()).save(testAppointment);

            verifyNoInteractions(appointmentMapper);
        }

        @Test
        @DisplayName("Should throw when an appointment conflict exists")
        void shouldThrow_whenExistsConflict() {
            when(appointmentRepository.findById(10L)).thenReturn(Optional.of(testAppointment));
            when(patientRepository.findById(testAppointment.getPatient().getId())).thenReturn(Optional.of(testPatient));
            when(appointmentRepository.existsConflict(any(), any(), any(), any(), anyList(), eq(testAppointment.getId()))).thenReturn(true);

            assertThrows(AppointmentConflictException.class, () -> appointmentService.updateAppointment(10L, testAppointmentRequest));

            verify(appointmentRepository, never()).save(testAppointment);

            verifyNoInteractions(appointmentMapper);
        }
    }

    @Nested
    @DisplayName("Cancel Appointment Tests")
    class CancelAppointmentTests {

        @Test
        @DisplayName("Should set appointment CANCELLED successfully with valid request")
        void shouldCancelAppointment() {
            when(appointmentRepository.findById(10L)).thenReturn(Optional.of(testAppointment));
            when(appointmentMapper.toResponse(testAppointment)).thenReturn(testAppointmentResponse);

            final AppointmentResponse result = appointmentService.cancelAppointment(10L);

            assertNotNull(result);
            assertEquals(testAppointmentResponse, result);
            assertEquals(AppointmentStatus.CANCELLED, testAppointment.getAppointmentStatus());

            verify(appointmentMapper, times(1)).toResponse(testAppointment);
        }

        @Test
        @DisplayName("Should throw when appointment ID not found")
        void shouldThrow_whenAppointmentNotFound() {
            when(appointmentRepository.findById(10L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> appointmentService.cancelAppointment(10L));

            verifyNoInteractions(appointmentMapper);
        }

        @Test
        @DisplayName("Should throw when appointment status is COMPLETED")
        void shouldThrow_whenAppointmentStatusIsCompleted() {
            when(appointmentRepository.findById(10L)).thenReturn(Optional.of(testAppointment));
            testAppointment.setAppointmentStatus(AppointmentStatus.COMPLETED);

            assertThrows(AppointmentConflictException.class, () -> appointmentService.cancelAppointment(10L));

            verifyNoInteractions(appointmentMapper);
        }
    }

    @Nested
    @DisplayName("Confirm Appointment Tests")
    class ConfirmAppointmentTests {

        @Test
        @DisplayName("Should set appointment CONFIRMED successfully with valid request")
        void shouldConfirmAppointment() {
            when(appointmentRepository.findById(10L)).thenReturn(Optional.of(testAppointment));
            when(appointmentMapper.toResponse(testAppointment)).thenReturn(testAppointmentResponse);

            final AppointmentResponse result = appointmentService.confirmAppointment(10L);

            assertNotNull(result);
            assertEquals(testAppointmentResponse, result);
            assertEquals(AppointmentStatus.CONFIRMED, testAppointment.getAppointmentStatus());

            verify(appointmentMapper, times(1)).toResponse(testAppointment);
        }

        @Test
        @DisplayName("Should throw when appointment ID not found")
        void shouldThrow_whenAppointmentNotFound() {
            when(appointmentRepository.findById(10L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> appointmentService.confirmAppointment(10L));

            verifyNoInteractions(appointmentMapper);
        }

        @ParameterizedTest
        @DisplayName("Should throw when appointment status is not SCHEDULED")
        @EnumSource(value = AppointmentStatus.class, names = "SCHEDULED", mode = EnumSource.Mode.EXCLUDE)
        void shouldThrow_whenAppointmentStatusIsNotScheduled(AppointmentStatus status) {
            when(appointmentRepository.findById(10L)).thenReturn(Optional.of(testAppointment));
            testAppointment.setAppointmentStatus(status);

            assertThrows(AppointmentConflictException.class, () -> appointmentService.confirmAppointment(10L));

            verifyNoInteractions(appointmentMapper);
        }
    }

    @Nested
    @DisplayName("Complete Appointment Tests")
    class CompleteAppointmentTests {

        @Test
        @DisplayName("Should set appointment COMPLETED successfully with valid request")
        void shouldCompleteAppointment() {
            when(appointmentRepository.findById(10L)).thenReturn(Optional.of(testAppointment));
            when(appointmentMapper.toResponse(testAppointment)).thenReturn(testAppointmentResponse);

            final AppointmentResponse result = appointmentService.completeAppointment(10L);

            assertNotNull(result);
            assertEquals(testAppointmentResponse, result);
            assertEquals(AppointmentStatus.COMPLETED, testAppointment.getAppointmentStatus());

            verify(appointmentMapper, times(1)).toResponse(testAppointment);
        }

        @Test
        @DisplayName("Should throw when appointment ID not found")
        void shouldThrow_whenAppointmentNotFound() {
            when(appointmentRepository.findById(10L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> appointmentService.completeAppointment(10L));

            verifyNoInteractions(appointmentMapper);
        }

        @ParameterizedTest
        @DisplayName("Should throw when appointment status is neither SCHEDULED nor CONFIRMED")
        @EnumSource(value = AppointmentStatus.class, names = {"SCHEDULED", "CONFIRMED"}, mode = EnumSource.Mode.EXCLUDE)
        void shouldThrow_whenAppointmentStatusIsNeitherScheduledNorConfirmed(AppointmentStatus status) {
            when(appointmentRepository.findById(10L)).thenReturn(Optional.of(testAppointment));
            testAppointment.setAppointmentStatus(status);

            assertThrows(AppointmentConflictException.class, () -> appointmentService.completeAppointment(10L));

            verifyNoInteractions(appointmentMapper);
        }
    }

    @Nested
    @DisplayName("Delete Appointment Tests")
    class DeleteAppointmentTests {

        @Test
        @DisplayName("Should delete appointment successfully with valid request")
        void shouldDeleteAppointment() {
            when(appointmentRepository.findById(10L)).thenReturn(Optional.of(testAppointment));

            appointmentService.deleteAppointment(10L);

            verify(appointmentRepository).delete(testAppointment);
        }

        @Test
        @DisplayName("Should throw when appointment ID not found")
        void shouldThrow_whenAppointmentNotFound() {
            when(appointmentRepository.findById(10L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> appointmentService.deleteAppointment(10L));

            verify(appointmentRepository, never()).delete(any());
        }
    }
}