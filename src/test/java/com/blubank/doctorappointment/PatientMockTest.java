package com.blubank.doctorappointment;

import com.blubank.doctorappointment.enties.Appointment;
import com.blubank.doctorappointment.enties.Doctor;
import com.blubank.doctorappointment.enties.Patient;
import com.blubank.doctorappointment.repository.AppointmentRepository;
import com.blubank.doctorappointment.service.DoctorService;
import com.blubank.doctorappointment.service.PatientService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class PatientMockTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private Doctor doctorEntity;

    public void addAvailableDoctorTimeSlots(){

        doctorService.addAvailableDoctorAppointmentTimeSlots(doctorEntity.getId() ,
                LocalDateTime.parse("2024-09-24T19:12:43.750") ,
                LocalDateTime.parse("2024-09-24T22:12:43.750") );

    }

    @BeforeEach
    public void setUp() {

        Doctor doctor = Doctor.builder().
                fullName("navid soorani").Expertise("brain surgery")
                .createdAt(LocalDateTime.now()).build();
        doctorEntity = doctorService.save(doctor);
    }

    @Test
    public void getAllOpenAppointmentDoctorSuccess() throws Exception{

        String jsonString = "{  \"start\": \"2024-09-24T19:12:43.750\"," +
                " \"end\": \"2024-09-24T22:12:43.750\" }";

        addAvailableDoctorTimeSlots();

        String resultJson = mockMvc.perform(get("/api/v1/patient/allOpenAppointment/{doctorId}" ,
                        doctorEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();;

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        List<Appointment> myAppointmentList = mapper.readValue(resultJson,
                new TypeReference<>() {
                });

        assertEquals(myAppointmentList.size() , 6);

    }

    @Test
    public void getAllOpenAppointmentDoctorEmptyList() throws Exception{

        String jsonString = "{  \"start\": \"2024-09-24T19:12:43.750\"," +
                " \"end\": \"2024-09-24T19:12:43.750\" }";

        addAvailableDoctorTimeSlots();

        String resultJson = mockMvc.perform(get("/api/v1/patient/allOpenAppointment/{doctorId}" ,
                        doctorEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        List<Appointment> myAppointmentList = mapper.readValue(resultJson,
                new TypeReference<>() {
                });

        assertEquals(myAppointmentList.size() , 0);

    }

    @Test
    public void getAllOwnAppointmentSuccess() throws Exception{

        String jsonString = "{  \"start\": \"2024-09-24T19:12:43.750\"," +
                " \"end\": \"2024-09-24T22:12:43.750\" }";

        addAvailableDoctorTimeSlots();

        Patient patientEntity = Patient.builder().name("amo reza")
                .phoneNumber("98913111111").build();
        patientService.addPatientDoctorAppointment(1L  , patientEntity
                , doctorEntity.getId());

        String resultJson = mockMvc.perform(get("/api/v1/patient/allOwnAppointment/{phoneNumber}" ,
                        "98913111111")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();;

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        List<Appointment> myAppointmentList = mapper.readValue(resultJson,
                new TypeReference<>() {
                });

        assertEquals(myAppointmentList.size() , 1);

    }

    @Test
    public void getAllOwnAppointmentEmptyList() throws Exception{

        String jsonString = "{  \"start\": \"2024-09-24T19:12:43.750\"," +
                " \"end\": \"2024-09-24T22:12:43.750\" }";

        addAvailableDoctorTimeSlots();

        String resultJson = mockMvc.perform(get("/api/v1/patient/allOwnAppointment/{phoneNumber}" ,
                        "98913111111")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();;

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        List<Appointment> myAppointmentList = mapper.readValue(resultJson,
                new TypeReference<>() {
                });

        assertEquals(myAppointmentList.size() , 0);

    }

    @Test
    public void addPatientAppointmentSuccess() throws Exception{

        String jsonString = "{\"name\": \" reza\" , \"phoneNumber\": \"989313111112\"}";

        addAvailableDoctorTimeSlots();

        mockMvc.perform(post("/api/v1/patient/add/{doctorId}/{appointmentId}" ,
                        doctorEntity.getId() , 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isOk());

        List<Appointment> appointmentList = appointmentRepository.
                findAllStartTimeBetweenOpenAppointmentByDoctorId(doctorEntity.getId() ,
                LocalDateTime.parse("2024-09-24T19:12:43.750") ,
                LocalDateTime.parse("2024-09-24T22:12:43.750"));

        assertEquals(appointmentList.size() , 5);

    }

    @Test
    public void addPatientAppointmentError() throws Exception {

        String jsonStringNameNull = "{\"name\": \" amo reza\" , \"phoneNumber\": \"989313111112\"}";

        mockMvc.perform(post("/api/v1/patient/add/{doctorId}/{appointmentId}" ,
                        doctorEntity.getId() , 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonStringNameNull))
                .andExpect(status().is(406)).andReturn();
    }

    @Test
    public void addPatientAppointmentErrorWhenNameIsEmpty() throws Exception {

        String jsonStringEmptyName = "{\"name\": \" \" , \"phoneNumber\": \"989313111112\"}";

        String response = mockMvc.perform(post("/api/v1/patient/add/{doctorId}/{appointmentId}" ,
                        doctorEntity.getId() , 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonStringEmptyName))
                .andExpect(status().is(400)).andReturn().getResponse().getContentAsString();;

        ObjectMapper mapper = new ObjectMapper();
        HashMap hashMap =  mapper.readValue(response , HashMap.class);

        assertEquals(hashMap.get("name") , "name is mandatory");

    }

    @Test
    public void addPatientAppointmentErrorWhenNameIsNull() throws Exception {

        String jsonStringEmptyName = "{\"phoneNumber\": \"989313111112\"}";

        String response = mockMvc.perform(post("/api/v1/patient/add/{doctorId}/{appointmentId}" ,
                        doctorEntity.getId() , 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonStringEmptyName))
                .andExpect(status().is(400)).andReturn().getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        HashMap hashMap =  mapper.readValue(response , HashMap.class);

        assertEquals(hashMap.get("name") , "name is mandatory");

    }

    @Test
    public void addPatientAppointmentErrorWhenPhoneNumberIsEmpty() throws Exception {

        String jsonStringEmptyName = "{\"name\": \" reza\" , \"phoneNumber\": \"\"}";

        String response = mockMvc.perform(post("/api/v1/patient/add/{doctorId}/{appointmentId}" ,
                        doctorEntity.getId() , 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonStringEmptyName))
                .andExpect(status().is(400)).andReturn().getResponse().getContentAsString();;

        ObjectMapper mapper = new ObjectMapper();
        HashMap hashMap =  mapper.readValue(response , HashMap.class);

        assertEquals(hashMap.get("phoneNumber") , "phoneNumber is mandatory");

    }

    @Test
    public void addPatientAppointmentErrorWhenPhoneNumberIsNull() throws Exception {

        String jsonStringNullPhoneNumber = "{\"name\": \" reza\" }";

        String response = mockMvc.perform(post("/api/v1/patient/add/{doctorId}/{appointmentId}" ,
                        doctorEntity.getId() , 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonStringNullPhoneNumber))
                .andExpect(status().is(400)).andReturn().getResponse().getContentAsString();;

        ObjectMapper mapper = new ObjectMapper();
        HashMap hashMap =  mapper.readValue(response , HashMap.class);

        assertEquals(hashMap.get("phoneNumber") , "phoneNumber is mandatory");

    }

}
