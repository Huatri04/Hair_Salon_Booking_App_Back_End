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

    @PostMapping("/system")
    public ResponseEntity createNewAppointmentBySystem(@Valid @RequestBody AppointmentRequestSystem appointmentRequestSystem){
        AppointmentResponse appointment = appointmentService.createNewAppointmentBySystem(appointmentRequestSystem);
        return ResponseEntity.ok(appointment);
    }

    @PostMapping("/staff")
    public ResponseEntity createNewAppointmentByStaff(@Valid @RequestBody AppointmentRequest appointmentRequest){
        AppointmentResponse appointment = appointmentService.createNewAppointmentByStaff(appointmentRequest);
        return ResponseEntity.ok(appointment);
    }

    @PostMapping("/system/staff")
    public ResponseEntity createNewAppointmentBySystemStaff(@Valid @RequestBody AppointmentRequestSystem appointmentRequestSystem){
        AppointmentResponse appointment = appointmentService.createNewAppointmentBySystemStaff(appointmentRequestSystem);
        return ResponseEntity.ok(appointment);
    }

    @DeleteMapping("/staffDelete/{slotId}")
    public ResponseEntity deleteAppointmentByStaff(@PathVariable long slotId){
        String message = appointmentService.deleteAppointmentByStaff(slotId);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/customerDelete/{idAppointment}")
    public ResponseEntity deleteAppointmentByCus(@PathVariable long idAppointment){
        String message = appointmentService.deleteAppointmentByCustomer(idAppointment);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity daleteAllAppointments(@Valid @RequestBody DeleteAllAppointmentsRequest deleteAllAppointmentsRequest){
        List<String> messages = appointmentService.deleteAppointmentsOfStylist(deleteAllAppointmentsRequest);
        return ResponseEntity.ok(messages);
    }

    @GetMapping
    public ResponseEntity getAppointmentHistory(){
        List<AppointmentResponseInfo> appointments = appointmentService.checkAppointmentHistory();
        return ResponseEntity.ok(appointments);
    }

    /*@PutMapping("/complete")
    public ResponseEntity completeAppointment(@Valid @RequestBody CompleteAppointmentRequest completeAppointmentRequest){
        String message = appointmentService.completeAppointment(completeAppointmentRequest);
        return ResponseEntity.ok(message);
    }*/

    @GetMapping("/KPI")
    public ResponseEntity viewKPI(){
        List<KPITotal> kpiTotalList = appointmentService.getAllKPI();
        return ResponseEntity.ok(kpiTotalList);
    }

    @PutMapping("/{appointmentId}")
    public ResponseEntity completeAppointment(@Valid @RequestBody AppointmentUpdate appointmentUpdate, @PathVariable long appointmentId){
        AppointmentResponse appointmentResponse = appointmentService.updateAppointment(appointmentUpdate, appointmentId);
        return ResponseEntity.ok(appointmentResponse);
    }

    @GetMapping("/fun")
    public ResponseEntity viewFun(){
        long a = appointmentService.forFun();
        return ResponseEntity.ok(a);
    }

    @GetMapping("/{date}/{phone}")
    public ResponseEntity getAllAppointmentsByDateAndPhone(@PathVariable String date, @PathVariable String phone){
        List<AppointmentResponseInfo> appointmentResponseInfoList = appointmentService.getAppointmentBySĐT(phone, date);
        return ResponseEntity.ok(appointmentResponseInfoList);
    }

    @PutMapping("/complete/{appointmentId}")
    public ResponseEntity completeAppointment(@PathVariable long appointmentId){
        String message = appointmentService.completeAppointmentById(appointmentId);
        return ResponseEntity.ok(message);
    }

}
