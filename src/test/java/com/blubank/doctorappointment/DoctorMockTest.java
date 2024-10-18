package com.blubank.doctorappointment;
import com.blubank.doctorappointment.enties.Appointment;
import com.blubank.doctorappointment.enties.Doctor;
import com.blubank.doctorappointment.enties.Patient;
import com.blubank.doctorappointment.repository.AppointmentRepository;
import com.blubank.doctorappointment.repository.PatientRepository;
import com.blubank.doctorappointment.service.DoctorService;
import com.blubank.doctorappointment.service.PatientService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.time.LocalDateTime;
import java.util.List;

@AutoConfigureMockMvc
@SpringBootTest
public class DoctorMockTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private Doctor doctorEntity;

    @BeforeEach
    public void setUp() {

        Doctor doctor = Doctor.builder().
                fullName("navid soorani").Expertise("brain surgery")
                .createdAt(LocalDateTime.now()).build();
        doctorEntity = doctorService.save(doctor);
    }

    @Test
    public void addDoctorSlotSuccess() throws Exception{

        String jsonString = "{  \"start\": \"2024-09-24T19:12:43.750\"," +
                " \"end\": \"2024-09-24T22:12:43.750\" }";

        mockMvc.perform(post("/api/v1/doctor/{doctorId}/slots" ,
                        doctorEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isOk());

        List<Appointment> appointmentList =  appointmentRepository.
                findByDoctorIdAndStartTimeBetween(doctorEntity.getId() ,
                    LocalDateTime.parse("2024-09-24T19:12:43.750") ,
                    LocalDateTime.parse("2024-09-24T22:12:43.750")
        );

        assertEquals(appointmentList.size() , 6);
    }

   @Test
    public void addDoctorSlotEndSoonerThanStart() throws Exception{

        String jsonString = "{  \"end\": \"2024-09-24T19:12:43.750\", " +
                "\"start\": \"2024-09-24T22:12:43.750\" }";

        mockMvc.perform(post("/api/v1/doctor/{doctorId}/slots" ,
                        doctorEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().is(400))
                .andExpect(content().string("End time must be after start time."));

    }

    @Test
    public void addDoctorSlotWithLessThan30Minutes() throws Exception{

        String jsonString = "{  \"start\": \"2024-09-24T19:12:43.750\"," +
                " \"end\": \"2024-09-24T19:12:44.750\" }";

        mockMvc.perform(post("/api/v1/doctor/{doctorId}/slots" ,
                        doctorEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isOk());

        List<Appointment> appointmentList =  appointmentRepository.
                findByDoctorIdAndStartTimeBetween(doctorEntity.getId() ,
                LocalDateTime.parse("2024-09-24T19:12:43.750") ,
                LocalDateTime.parse("2024-09-24T19:12:44.750")
        );

        assertEquals(appointmentList.size() , 0);
    }

    @Test
    public void getAppointmentsByDoctorEmptyList() throws Exception{

        String jsonString = "{  \"start\": \"2024-09-24T19:12:43.750\"," +
                " \"end\": \"2024-09-24T22:12:43.750\" }";

        mockMvc.perform(
                get("/api/v1/doctor/allAppointment/{doctorId}"
                        ,doctorEntity.getId()).
                        contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString)
                        ).andExpect(status().isOk());

        List<Appointment> appointmentList =  appointmentRepository.
                findByDoctorIdAndStartTimeBetween(doctorEntity.getId() ,
                        LocalDateTime.parse("2024-09-24T19:12:43.750") ,
                        LocalDateTime.parse("2024-09-24T22:12:43.750")
                );

        assertEquals(appointmentList.size() , 0);

    }

    @Test
    public void getAppointmentsByDoctor() throws Exception{

        String jsonString = "{  \"start\": \"2024-09-24T19:12:43.750\"," +
                " \"end\": \"2024-09-24T22:12:43.750\" }";

        doctorService.addAvailableDoctorAppointmentTimeSlots(doctorEntity.getId() ,
                LocalDateTime.parse("2024-09-24T19:12:43.750") ,
                LocalDateTime.parse("2024-09-24T22:12:43.750") );

        Patient patientEntity = Patient.builder().name("amo reza")
                        .phoneNumber("98913111111").build();
        patientService.addPatientDoctorAppointment(1L  ,
                patientEntity , doctorEntity.getId());

        String resultJson = mockMvc.perform(
                get("/api/v1/doctor/allAppointment/{doctorId}"
                        ,doctorEntity.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString)
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        List<Appointment> myAppointmentList = mapper.readValue(resultJson,
                new TypeReference<>() {
                });

        List<Appointment> appointmentList =  appointmentRepository.
                findByDoctorIdAndStartTimeBetween(doctorEntity.getId() ,
                        LocalDateTime.parse("2024-09-24T19:12:43.750") ,
                        LocalDateTime.parse("2024-09-24T22:12:43.750")
                );

        boolean isExistPatient = myAppointmentList.stream().anyMatch(
                (appointment -> (!appointment.getPatient().getName().isEmpty() &&
                        !appointment.getPatient().getPhoneNumber().isEmpty())));
        assertTrue(isExistPatient);

        assertEquals(appointmentList.size() , myAppointmentList.size());

    }

    @Test
    public void deleteAppointmentsByDoctorSuccess() throws Exception{


        doctorService.addAvailableDoctorAppointmentTimeSlots(doctorEntity.getId() ,
                LocalDateTime.parse("2024-09-24T19:12:43.750") ,
                LocalDateTime.parse("2024-09-24T22:12:43.750") );

        mockMvc.perform(
                delete("/api/v1/doctor/{doctorId}/{appointmentId}" ,
                        doctorEntity.getId() , 3L)
        ).andExpect(status().isOk());

        List<Appointment> appointmentList =  appointmentRepository.
                findByDoctorIdAndStartTimeBetween(doctorEntity.getId() ,
                        LocalDateTime.parse("2024-09-24T19:12:43.750") ,
                        LocalDateTime.parse("2024-09-24T22:12:43.750")
                );

        assertEquals(appointmentList.size() , 5);
    }

    @Test
    public void deleteAppointmentsByDoctorError404() throws Exception{

        doctorService.addAvailableDoctorAppointmentTimeSlots(doctorEntity.getId() ,
                LocalDateTime.parse("2024-09-24T19:12:43.750") ,
                LocalDateTime.parse("2024-09-24T22:12:43.750") );

        mockMvc.perform(
                delete("/api/v1/doctor/{doctorId}/{appointmentId}" ,
                        doctorEntity.getId() , -1)
        ).andExpect(status().is(404));
    }

    @Test
    public void deleteAppointmentsByDoctorError406() throws Exception{

        doctorService.addAvailableDoctorAppointmentTimeSlots(doctorEntity.getId() ,
                LocalDateTime.parse("2024-09-24T19:12:43.750") ,
                LocalDateTime.parse("2024-09-24T22:12:43.750") );

        Patient patientEntity = Patient.builder().name("amo reza")
                .phoneNumber("98913111111").build();
        patientService.addPatientDoctorAppointment(1L  , patientEntity ,
                doctorEntity.getId());

        mockMvc.perform(
                delete("/api/v1/doctor/{doctorId}/{appointmentId}" ,
                        doctorEntity.getId() , 1)
        ).andExpect(status().is(406));
    }

}
