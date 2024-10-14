package com.hairsalonbookingapp.hairsalon.api;

import com.hairsalonbookingapp.hairsalon.entity.Appointment;
import com.hairsalonbookingapp.hairsalon.model.request.AppointmentRequest;
import com.hairsalonbookingapp.hairsalon.model.request.CompleteAppointmentRequest;
import com.hairsalonbookingapp.hairsalon.model.request.DeleteAllAppointmentsRequest;
import com.hairsalonbookingapp.hairsalon.model.response.AppointmentResponse;
import com.hairsalonbookingapp.hairsalon.model.response.KPITotal;
import com.hairsalonbookingapp.hairsalon.service.AppointmentService;
import com.hairsalonbookingapp.hairsalon.service.PayService;
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

    @Autowired
    PayService payService;

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

    @PutMapping("/staffDelete/{slotId}")
    public ResponseEntity deleteAppointmentByStaff(@PathVariable long slotId){
        String message = appointmentService.deleteAppointmentByStaff(slotId);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/customerDelete")
    public ResponseEntity deleteAppointmentByCus(@Valid @RequestBody long slotId){
        String message = appointmentService.deleteAppointmentByCustomer(slotId);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/deleteAll")
    public ResponseEntity daleteAllAppointments(@Valid @RequestBody DeleteAllAppointmentsRequest deleteAllAppointmentsRequest){
        List<String> messages = appointmentService.deleteAppointmentsOfStylist(deleteAllAppointmentsRequest);
        return ResponseEntity.ok(messages);
    }

    @GetMapping
    public ResponseEntity getAppointmentHistory(){
        List<AppointmentResponse> appointments = appointmentService.checkAppointmentHistory();
        return ResponseEntity.ok(appointments);
    }

    @PutMapping("/complete")
    public ResponseEntity completeAppointment(@Valid @RequestBody CompleteAppointmentRequest completeAppointmentRequest) throws Exception {
//        Appointment appointment = appointmentService.completeAppointment(completeAppointmentRequest);
        String urlVNPay = payService.createUrl(completeAppointmentRequest);
        return ResponseEntity.ok(urlVNPay);
    }

    @GetMapping("/KPI")
    public ResponseEntity viewKPI(){
        List<KPITotal> kpiTotalList = appointmentService.getAllKPI();
        return ResponseEntity.ok(kpiTotalList);
    }

}
