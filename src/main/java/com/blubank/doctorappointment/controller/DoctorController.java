package com.blubank.doctorappointment.controller;

import com.blubank.doctorappointment.Exceptions.InvalidDateTimeDoctorSlot;
import com.blubank.doctorappointment.Exceptions.NotOpenAppointment;
import com.blubank.doctorappointment.dto.TimeSlotRequest;
import com.blubank.doctorappointment.enties.Appointment;
import com.blubank.doctorappointment.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

@RestController
@RequestMapping("/api/v1/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @PostMapping("/{doctorId}/slots")
    public ResponseEntity<?> addTimeSlots(@PathVariable Long doctorId,
                                          @RequestBody TimeSlotRequest request) {
        try {

            doctorService.addAvailableDoctorAppointmentTimeSlots(doctorId,
                    request.getStart(),
                    request.getEnd());
            return ResponseEntity.ok().build();

        } catch (InvalidDateTimeDoctorSlot e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }

    @GetMapping("allAppointment/{doctorId}")
    public List<Appointment> getAllAppointmentsForDoctor(@PathVariable Long doctorId  ,
                                                            @RequestBody TimeSlotRequest request ) {

        List<Appointment> appointmentList =  new ArrayList<>();

        try {

            appointmentList =  doctorService.getAllAppointmentsForDoctor(doctorId ,
                              request.getStart(),
                              request.getEnd());
        }catch (RuntimeException e) {
            return appointmentList;
        }
        return appointmentList;
    }

    @DeleteMapping("{doctorId}/{appointmentId}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long doctorId ,
                                               @PathVariable Long appointmentId){
        try {

            if(doctorService.deleteAppointmentForDoctor(doctorId , appointmentId) == 0)
                return ResponseEntity.status(NOT_ACCEPTABLE).build();
            return ResponseEntity.ok().build();
        }catch (NotOpenAppointment e){
            return ResponseEntity.notFound().build();
        }
    }

}