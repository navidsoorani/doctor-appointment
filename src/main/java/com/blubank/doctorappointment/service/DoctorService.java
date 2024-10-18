package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.Exceptions.InvalidDateTimeDoctorSlot;
import com.blubank.doctorappointment.Exceptions.NotOpenAppointment;
import com.blubank.doctorappointment.enties.Appointment;
import com.blubank.doctorappointment.enties.Doctor;
import com.blubank.doctorappointment.repository.AppointmentRepository;
import com.blubank.doctorappointment.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public void addAvailableDoctorAppointmentTimeSlots(Long doctorId, LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new InvalidDateTimeDoctorSlot("End time must be after start time.");
        }

        if (start.plusMinutes(30).isAfter(end)) {
            return;
        }

        while (start.plusMinutes(30).isBefore(end) || start.plusMinutes(30).isEqual(end)) {
            Appointment appointment =  Appointment.builder().
                    doctor(doctorRepository.findById(doctorId).orElseThrow())
                    .startTime(start).endTime(end).build();
            appointmentRepository.save(appointment);
            start = start.plusMinutes(30);
        }
    }

    public List<Appointment> getAllAppointmentsForDoctor(Long doctorId , LocalDateTime start, LocalDateTime end){

        return appointmentRepository.findAllStartTimeBetweenAppointmentByDoctorId(doctorId , start , end);
    }

    public List<Appointment> getAllOpenAppointmentsForDoctor(Long doctorId , LocalDateTime start, LocalDateTime end){

        return appointmentRepository.findAllStartTimeBetweenOpenAppointmentByDoctorId(doctorId , start , end);
    }

    public Integer deleteAppointmentForDoctor(Long doctorId , Long appointmentId){

        List<Appointment> optionalAppointment = appointmentRepository.findByIdAndDoctorId(appointmentId , doctorId);

        if(optionalAppointment != null && optionalAppointment.isEmpty()){
            throw new NotOpenAppointment("there is no open appointment");
        }

        return  appointmentRepository.deleteDoctorOpenAppointment(doctorId , appointmentId);
    }

    public Doctor save(Doctor doctor){
        return  doctorRepository.save(doctor);
    }




}