package com.hairsalonbookingapp.hairsalon.api;

import com.hairsalonbookingapp.hairsalon.entity.Appointment;
import com.hairsalonbookingapp.hairsalon.entity.HairSalonService;
import com.hairsalonbookingapp.hairsalon.entity.ShiftEmployee;
import com.hairsalonbookingapp.hairsalon.entity.Slot;
import com.hairsalonbookingapp.hairsalon.model.AppointmentRequest;
import com.hairsalonbookingapp.hairsalon.model.AppointmentUpdate;
import com.hairsalonbookingapp.hairsalon.model.StylistInfo;
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

    @GetMapping("/serviceForAppointment")
    public ResponseEntity getAllAvailableServices(){
        List<HairSalonService> list = appointmentService.getServiceList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/stylistForAppointment")
    public ResponseEntity getAllAvailableStylist(){
        List<StylistInfo> list = appointmentService.getAllStylistInFo();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/shiftForAppointment")
    public ResponseEntity getAllAvailableStylistShift(@PathVariable String stylistId){
        List<ShiftEmployee> list = appointmentService.getShiftEmployees(stylistId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/slotForAppointment")
    public ResponseEntity getAllAvailableStylistSlot(@PathVariable long shiftEmployeeId){
        List<Slot> list = appointmentService.viewAvailableSlots(shiftEmployeeId);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/appointment")
    public ResponseEntity createNewAppointment(@Valid @RequestBody AppointmentRequest appointmentRequest){
        Appointment appointment = appointmentService.createNewAppointment(appointmentRequest);
        return ResponseEntity.ok(appointment);
    }

    @PutMapping("/appointment/id")
    public ResponseEntity createNewAppointment(@Valid @RequestBody AppointmentUpdate appointmentUpdate, @PathVariable long id){
        Appointment appointment = appointmentService.updateAppointment(appointmentUpdate, id);
        return ResponseEntity.ok(appointment);
    }

    @DeleteMapping("/appointment/id")
    public ResponseEntity deleteAppointment(@PathVariable long id){
        Appointment appointment = appointmentService.deleteAppointment(id);
        return ResponseEntity.ok(appointment);
    }


}
