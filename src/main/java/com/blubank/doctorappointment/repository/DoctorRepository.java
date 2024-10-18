package com.blubank.doctorappointment.repository;

import com.blubank.doctorappointment.enties.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}
