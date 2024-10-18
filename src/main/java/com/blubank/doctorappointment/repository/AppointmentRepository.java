package com.blubank.doctorappointment.repository;

import com.blubank.doctorappointment.enties.Appointment;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctorIdAndStartTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end);


    @Lock(LockModeType.PESSIMISTIC_READ)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value ="5000")})
    List<Appointment> findByIdAndDoctorId(Long appointmentId , Long doctorId);

    List<Appointment> findAllByPatientId(Long patientId);

    @Query("SELECT p FROM Appointment p where p.doctor.id = :doctorId " +
            "and p.startTime >= :startTime and p.endTime <= :endTime ")
    List<Appointment> findAllStartTimeBetweenAppointmentByDoctorId(@Param("doctorId")Long doctorId,
                                                        @Param("startTime") LocalDateTime startTime,
                                                        @Param("endTime") LocalDateTime endTime);

    @Query("SELECT p FROM Appointment p where p.doctor.id = :doctorId and p.patient is null " +
            "and p.startTime >= :startTime and p.endTime <= :endTime ")
    List<Appointment> findAllStartTimeBetweenOpenAppointmentByDoctorId(@Param("doctorId")Long doctorId,
                                                                   @Param("startTime") LocalDateTime startTime,
                                                                   @Param("endTime") LocalDateTime endTime);
    @Modifying
    @Query("delete  FROM Appointment p where p.doctor.id = :doctorId and p.id = :appointmentId" +
            " and p.patient = null ")
    Integer deleteDoctorOpenAppointment(@Param("doctorId") Long doctorId,
                                                  @Param("appointmentId") Long appointmentId);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value ="5000")})
    @Query("SELECT p FROM Appointment p where p.doctor.id = :doctorId and p.patient = null" +
            " and p.id = :appointmentId ")
    Optional<Appointment> findByIdAndPatientNotSet(@Param("appointmentId") Long appointmentId ,@Param("doctorId") Long doctorId);


}
