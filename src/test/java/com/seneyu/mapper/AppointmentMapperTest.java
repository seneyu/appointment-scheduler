package com.seneyu.mapper;

import com.seneyu.AppointmentStatus;
import com.seneyu.AppointmentType;
import com.seneyu.dto.AppointmentRequest;
import com.seneyu.dto.AppointmentResponse;
import com.seneyu.entity.Appointment;
import com.seneyu.entity.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Appointment Mapper Tests")
class AppointmentMapperTest {

    private AppointmentMapper appointmentMapper;
    private Patient testPatient;
    private Appointment testAppointment;
    private AppointmentRequest testAppointmentRequest;
    private AppointmentResponse testAppointmentResponse;

    @BeforeEach
    void setUp() {
        appointmentMapper = new AppointmentMapper();

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

    @Test
    @DisplayName("Should correctly map AppointmentRequest and Patient to Appointment entity")
    void shouldMapToEntity() {

        final Appointment result = appointmentMapper.toEntity(testAppointmentRequest, testPatient);

        assertNotNull(result);

        assertAll("Appointment toEntity Field Mappings",
                () -> assertEquals(testAppointmentRequest.startTime(), result.getStartTime()),
                () -> assertEquals(testAppointmentRequest.endTime(), result.getEndTime()),
                () -> assertEquals(testAppointmentRequest.providerId(), result.getProviderId()),
                () -> assertSame(testPatient, result.getPatient()),
                () -> assertEquals(testAppointmentRequest.roomId(), result.getRoomId()),
                () -> assertEquals(testAppointmentRequest.appointmentTypes(), result.getAppointmentTypes()),
                () -> assertEquals(AppointmentStatus.SCHEDULED, result.getAppointmentStatus())
        );
    }

    @Test
    @DisplayName("")
    void shouldConvertEntityToResponse() {

        final AppointmentResponse result = appointmentMapper.toResponse(testAppointment);

        assertNotNull(result);

        assertAll("Appointment toResponse Field Mappings",
                () -> assertEquals(testAppointmentResponse.id(), result.id()),
                () -> assertEquals(testAppointmentResponse.startTime(), result.startTime()),
                () -> assertEquals(testAppointmentResponse.endTime(), result.endTime()),
                () -> assertEquals(testAppointmentRequest.providerId(), result.providerId()),
                () -> assertSame(testPatient.getId(), result.patientId()),
                () -> assertEquals(testAppointmentRequest.roomId(), result.roomId()),
                () -> assertEquals(testAppointmentRequest.appointmentTypes(), result.appointmentTypes()),
                () -> assertEquals(testAppointmentResponse.appointmentStatus(), result.appointmentStatus())
        );
    }

    @Test
    @DisplayName("Should update entity state with request and patient data")
    void shouldUpdateEntityFromRequest() {

        // set up starting state and the new updates
        Patient testUpdatePatient = new Patient();
        testUpdatePatient.setId(99L);
        testUpdatePatient.setFirstName("Jane");

        AppointmentRequest testUpdateAppointmentRequest = new AppointmentRequest(
                Instant.parse("2028-02-27T09:00:00Z"),
                Instant.parse("2028-02-27T09:30:00Z"),
                5,
                66L,
                3,
                Set.of(AppointmentType.EMERGENCY, AppointmentType.EXTRACTION)
        );

        appointmentMapper.updateEntityFromRequest(testAppointment, testUpdateAppointmentRequest, testUpdatePatient);

        assertAll("Appointment Update Entity From Request Mappings",
                () -> assertEquals(testUpdateAppointmentRequest.startTime(), testAppointment.getStartTime()),
                () -> assertEquals(testUpdateAppointmentRequest.endTime(), testAppointment.getEndTime()),
                () -> assertEquals(testUpdateAppointmentRequest.providerId(), testAppointment.getProviderId()),
                () -> assertSame(testUpdatePatient, testAppointment.getPatient()),
                () -> assertEquals(testUpdateAppointmentRequest.roomId(), testAppointment.getRoomId()),
                () -> assertEquals(testUpdateAppointmentRequest.appointmentTypes(), testAppointment.getAppointmentTypes())
        );
    }

    @Test
    @DisplayName("Should modify list entities to a response list")
    void toResponseList() {

        Appointment second = new Appointment();
        second.setId(86L);
        second.setStartTime(Instant.parse("2027-04-15T11:00:00Z"));
        second.setEndTime(Instant.parse("2027-04-15T12:00:00Z"));
        second.setProviderId(2);
        second.setPatient(testPatient);
        second.setRoomId(3);
        second.setAppointmentTypes(Set.of(AppointmentType.CROWN));
        second.setAppointmentStatus(AppointmentStatus.SCHEDULED);

        final List<AppointmentResponse> result = appointmentMapper.toResponseList(List.of(testAppointment, second));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertAll("Appointments toResponseList Field Mappings",
                () -> assertEquals(testAppointment.getId(), result.getFirst().id()),
                () -> assertEquals(testAppointment.getStartTime(), result.getFirst().startTime()),
                () -> assertEquals(testAppointment.getEndTime(), result.getFirst().endTime()),
                () -> assertEquals(testAppointment.getProviderId(), result.getFirst().providerId()),
                () -> assertEquals(testAppointment.getPatient().getId(), result.getFirst().patientId()),
                () -> assertEquals(testAppointment.getRoomId(), result.getFirst().roomId()),
                () -> assertEquals(testAppointment.getAppointmentTypes(), result.getFirst().appointmentTypes()),
                () -> assertEquals(testAppointment.getAppointmentStatus(), result.getFirst().appointmentStatus())
        );
    }
}