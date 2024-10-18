package com.blubank.doctorappointment.repository;

import com.blubank.doctorappointment.enties.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient>findByPhoneNumber(String phoneNumber);
    Optional<Patient> findById(Long Id);
}