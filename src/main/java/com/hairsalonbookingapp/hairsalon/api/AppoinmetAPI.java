package com.hairsalonbookingapp.hairsalon.api;

import com.hairsalonbookingapp.hairsalon.entity.Appointment;
import com.hairsalonbookingapp.hairsalon.entity.Feedback;
import com.hairsalonbookingapp.hairsalon.model.AppointmentResponse;
import com.hairsalonbookingapp.hairsalon.model.FeedbackResponse;
import com.hairsalonbookingapp.hairsalon.model.RequestAppointment;
import com.hairsalonbookingapp.hairsalon.model.RequestFeedback;
import com.hairsalonbookingapp.hairsalon.service.AppontmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appoinment")
@CrossOrigin("*")
@SecurityRequirement(name = "api") // tao controller moi nho copy qua
public class AppoinmetAPI {
    @Autowired
    AppontmentService appontmentService;

    @PostMapping
    public ResponseEntity createAppoinmet(@Valid @RequestBody RequestAppointment requestAppointment){
        AppointmentResponse appointmentResponse = appontmentService.createAppointment(requestAppointment);
        return ResponseEntity.ok(appointmentResponse);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteAppoinmet(@PathVariable String id){
        AppointmentResponse appointmentResponse = appontmentService.deleteAppointment(id);
        return ResponseEntity.ok(appointmentResponse);
    }

    @GetMapping
    public ResponseEntity getAllAppoinmet(){
        List<Appointment> appointments = appontmentService.getAllAppointment();
        return ResponseEntity.ok(appointments);
    }
}
