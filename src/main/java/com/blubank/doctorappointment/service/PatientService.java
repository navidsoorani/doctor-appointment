package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.Exceptions.NotOpenAppointment;
import com.blubank.doctorappointment.enties.Appointment;
import com.blubank.doctorappointment.enties.Doctor;
import com.blubank.doctorappointment.enties.Patient;
import com.blubank.doctorappointment.repository.AppointmentRepository;
import com.blubank.doctorappointment.repository.DoctorRepository;
import com.blubank.doctorappointment.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public void addPatientDoctorAppointment(Long appointmentId , Patient patient ,Long doctorId) {

     Optional<Appointment> optionalAppointment = appointmentRepository.
                findByIdAndPatientNotSet(appointmentId , doctorId);

        if(optionalAppointment.isEmpty()){
            throw new NotOpenAppointment("appointment is not found!");
        }

        Optional<Patient> optionalPatient =
                patientRepository.findByPhoneNumber(patient.getPhoneNumber());

        optionalPatient.ifPresent(value -> patient.setId(value.getId()));

        optionalAppointment.get().setPatient(patient);

        appointmentRepository.save(optionalAppointment.get());

    }

    public List<Appointment> getAllOwnAppointment(String phoneNumber) {

        List<Appointment> appointmentList = new ArrayList<>();

        Optional<Patient> optionalPatient = patientRepository.findByPhoneNumber(phoneNumber);

        if (optionalPatient.isPresent()) {

            appointmentList =  appointmentRepository.findAllByPatientId(optionalPatient.get().getId());

        }

        return  appointmentList;
    }
    public Patient save(Patient patient){
        return  patientRepository.save(patient);
    }

}