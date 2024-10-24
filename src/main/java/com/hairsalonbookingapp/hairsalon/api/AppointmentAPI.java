package com.hairsalonbookingapp.hairsalon.api;

import com.hairsalonbookingapp.hairsalon.entity.Appointment;
import com.hairsalonbookingapp.hairsalon.model.request.*;
import com.hairsalonbookingapp.hairsalon.model.response.AppointmentResponse;
import com.hairsalonbookingapp.hairsalon.model.response.AppointmentResponseInfo;
import com.hairsalonbookingapp.hairsalon.model.response.KPITotal;
import com.hairsalonbookingapp.hairsalon.service.AppointmentService;
import com.hairsalonbookingapp.hairsalon.service.PayService;
import com.hairsalonbookingapp.hairsalon.service.TransactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Autowired
    TransactionService transactionService;

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

    @PutMapping("/{appointmentId}")
    public ResponseEntity completeAppointment(@Valid @RequestBody AppointmentUpdate appointmentUpdate, @PathVariable long appointmentId){
        AppointmentResponse appointmentResponse = appointmentService.updateAppointment(appointmentUpdate, appointmentId);
        return ResponseEntity.ok(appointmentResponse);
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

    @PutMapping("/complete")
    public ResponseEntity completeAppointment(@Valid @RequestBody CompleteAppointmentRequest completeAppointmentRequest) throws Exception {
        try {
            String paymentType = completeAppointmentRequest.getPaymentType();

            if ("Banking".equalsIgnoreCase(paymentType)) {
                String urlVNPay = payService.createUrl(completeAppointmentRequest);
                // Tạo giao dịch VNPay
                payService.createTransaction(completeAppointmentRequest.getAppointmentId());
                return ResponseEntity.ok(urlVNPay);
            } else if ("Cash".equalsIgnoreCase(paymentType)) {
                // Xử lý thanh toán tiền mặt
                transactionService.createTransactionInCast(completeAppointmentRequest);
                return ResponseEntity.ok("Thanh toán tiền mặt thành công.");
            } else {
                return ResponseEntity.badRequest().body("Loại thanh toán không hợp lệ.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/KPI")
    public ResponseEntity viewKPI(){
        List<KPITotal> kpiTotalList = appointmentService.getAllKPI();
        return ResponseEntity.ok(kpiTotalList);
    }

    @PostMapping("/system")
    public ResponseEntity createNewAppointmentBySystem(@Valid @RequestBody AppointmentRequestSystem appointmentRequestSystem){
        AppointmentResponse appointment = appointmentService.createNewAppointmentBySystem(appointmentRequestSystem);
        return ResponseEntity.ok(appointment);
    }

    @PostMapping("/staff")
    public ResponseEntity createNewAppointmentByStaff(@Valid @RequestBody AppointmentRequest appointmentRequest, @RequestParam String phoneNumber){
        AppointmentResponse appointment = appointmentService.createNewAppointmentByStaff(appointmentRequest, phoneNumber);
        return ResponseEntity.ok(appointment);
    }

    @PostMapping("/system/staff")
    public ResponseEntity createNewAppointmentBySystemStaff(@Valid @RequestBody AppointmentRequestSystem appointmentRequestSystem, @RequestParam String phoneNumber){
        AppointmentResponse appointment = appointmentService.createNewAppointmentBySystemStaff(appointmentRequestSystem, phoneNumber);
        return ResponseEntity.ok(appointment);
    }
}
