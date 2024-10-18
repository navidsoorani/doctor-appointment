package com.blubank.doctorappointment.controller;
import com.blubank.doctorappointment.Exceptions.NotOpenAppointment;
import com.blubank.doctorappointment.dto.TimeSlotRequest;
import com.blubank.doctorappointment.enties.Appointment;
import com.blubank.doctorappointment.enties.Patient;
import com.blubank.doctorappointment.service.DoctorService;
import com.blubank.doctorappointment.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;


    @GetMapping("allOpenAppointment/{doctorId}")
    public List<Appointment> getAllOpenAppointmentForDoctor(@PathVariable Long doctorId  ,
                                                            @RequestBody TimeSlotRequest request ) {
        try {

            return doctorService.getAllOpenAppointmentsForDoctor(doctorId ,
                    request.getStart(),
                    request.getEnd());
        }catch (RuntimeException e) {
            return null;
        }
    }

    @GetMapping("/allOwnAppointment/{phoneNumber}")
    public List<Appointment> allOwnAppointment(@PathVariable String phoneNumber) {
        return patientService.getAllOwnAppointment(phoneNumber);
    }

    @PostMapping("/add/{doctorId}/{appointmentId}")
    public ResponseEntity<?> addPatientToDoctorAppointment(@PathVariable Long doctorId,
                                                           @PathVariable Long appointmentId
                                                           ,@Valid @RequestBody Patient request) {
       try {
          patientService.addPatientDoctorAppointment(appointmentId, request
           ,doctorId);
           return ResponseEntity.ok().build();
       } catch (NotOpenAppointment e) {
           return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
       }
   }
}