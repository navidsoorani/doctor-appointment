package com.blubank.doctorappointment.config;

import com.blubank.doctorappointment.enties.Appointment;
import com.blubank.doctorappointment.enties.Doctor;
import com.blubank.doctorappointment.repository.AppointmentRepository;
import com.blubank.doctorappointment.repository.DoctorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(DoctorRepository doctorRepository ,
                                   AppointmentRepository appointmentRepository) {

        return args -> {
            Doctor doctorEntity =  Doctor.builder().
                    fullName("navid soorani").Expertise("brain surgery")
                    .createdAt(LocalDateTime.now()).build();
            log.info("Preloading " + doctorRepository.save(doctorEntity));

    /*        Appointment appointmentEntity = Appointment.builder().patient(null).
                    doctor(doctorEntity).startTime(LocalDateTime.parse(
                            "2024-09-24T19:12:43.750"))
                    .endTime(LocalDateTime.parse
                            ("2024-09-24T19:42:43.750")).build();

            log.info("Preloading " +
                    appointmentRepository.save(appointmentEntity));

            Appointment appointmentEntity2 = Appointment.builder().patient(null).
                    doctor(doctorEntity).startTime(LocalDateTime.parse(
                            "2024-09-24T19:42:43.750"))
                    .endTime(LocalDateTime.parse
                            ("2024-09-24T20:12:43.750")).build();

            log.info("Preloading " +
                    appointmentRepository.save(appointmentEntity2));*/
        };
    }
}