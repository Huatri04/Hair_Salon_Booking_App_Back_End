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
@RequestMapping("/api")
@SecurityRequirement(name = "api")
public class AppointmentAPI {

    @Autowired
    AppointmentService appointmentService;

    @PostMapping("/appointment")
    public ResponseEntity createNewAppointment(@Valid @RequestBody AppointmentRequest appointmentRequest){
        AppointmentResponse appointment = appointmentService.createNewAppointment(appointmentRequest);
        return ResponseEntity.ok(appointment);
    }

    @PutMapping("/appointment/id")
    public ResponseEntity updateNewAppointment(@Valid @RequestBody AppointmentUpdate appointmentUpdate, @PathVariable long id){
        AppointmentResponse appointment = appointmentService.updateAppointment(appointmentUpdate, id);
        return ResponseEntity.ok(appointment);
    }

    @DeleteMapping("/appointment/id")
    public ResponseEntity deleteAppointment(@PathVariable long id){
        AppointmentResponse appointment = appointmentService.deleteAppointment(id);
        return ResponseEntity.ok(appointment);
    }

    @GetMapping("/availableAppointment")
    public ResponseEntity getAvailableAppointment(){
        List<AppointmentResponse> appointments = appointmentService.viewAllAvailableAppointment();
        return ResponseEntity.ok(appointments);
    }

    @PutMapping("/appointment/approve")
    public ResponseEntity approveAppointment(@PathVariable long appointmentID){
        AppointmentResponse appointment = appointmentService.approveAppointment(appointmentID);
        return ResponseEntity.ok(appointment);
    }

    @GetMapping("/appointment/check")
    public ResponseEntity checkAppointment(@PathVariable long appointmentID){
        AppointmentResponse appointment = appointmentService.checkAppointment(appointmentID);
        return ResponseEntity.ok(appointment);
    }


}
