package com.hairsalonbookingapp.hairsalon.api;

import com.hairsalonbookingapp.hairsalon.entity.Appointment;
import com.hairsalonbookingapp.hairsalon.entity.HairSalonService;
import com.hairsalonbookingapp.hairsalon.entity.ShiftEmployee;
import com.hairsalonbookingapp.hairsalon.entity.Slot;
import com.hairsalonbookingapp.hairsalon.model.*;
import com.hairsalonbookingapp.hairsalon.service.AppointmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointment")
@SecurityRequirement(name = "api")
public class AppointmentAPI {

    @Autowired
    AppointmentService appointmentService;

    /*@PostMapping("/appointment")
    public ResponseEntity createNewAppointment(@Valid @RequestBody AppointmentRequest appointmentRequest){
        long appointment = appointmentService.getAppoint(appointmentRequest);
        return ResponseEntity.ok(appointment);
    }*/


    @PostMapping
    public ResponseEntity createNewAppointment(@Valid @RequestBody AppointmentRequest appointmentRequest){
        AppointmentResponse appointment = appointmentService.createNewAppointment(appointmentRequest);
        return ResponseEntity.ok(appointment);
    }

    @PutMapping("/delete")
    public ResponseEntity deleteAppointment(@Valid @RequestBody DeleteAppointmentRequest deleteAppointmentRequest){
        String message = appointmentService.deleteAppointment(deleteAppointmentRequest);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/deleteAll")
    public ResponseEntity daleteAllAppointments(@Valid @RequestBody DeleteAllAppointmentsRequest deleteAllAppointmentsRequest){
        String message = appointmentService.deleteAppointmentsOfStylist(deleteAllAppointmentsRequest);
        return ResponseEntity.ok(message);
    }

    @GetMapping
    public ResponseEntity getAppointmentHistory(){
        List<AppointmentResponse> appointments = appointmentService.checkAppointmentHistory();
        return ResponseEntity.ok(appointments);
    }

    @PutMapping("/complete")
    public ResponseEntity completeAppointment(@Valid @RequestBody CompleteAppointmentRequest completeAppointmentRequest){
        String message = appointmentService.completeAppointment(completeAppointmentRequest);
        return ResponseEntity.ok(message);
    }

}
