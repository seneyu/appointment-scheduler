package com.seneyu.repository;

import com.seneyu.AppointmentStatus;
import com.seneyu.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("""
        select count(a) > 0 from Appointment a
            where a.appointmentStatus in :blockingStatuses
                and a.startTime < :endTime
                and a.endTime > :startTime
                and (:excludeId is null or a.id <> :excludeId)
                and (a.providerId = :providerId or a.roomId = :roomId)
    """)
    boolean existsConflict(@Param("startTime") Instant startTime,
                           @Param("endTime") Instant endTime,
                           @Param("providerId") Integer providerId,
                           @Param("roomId") Integer roomId,
                           @Param("blockingStatuses") List<AppointmentStatus> blockingStatuses,
                           @Param("excludeId") Long excludeId);
}
