package com.hairsalonbookingapp.hairsalon.api;

import com.hairsalonbookingapp.hairsalon.entity.Feedback;
import com.hairsalonbookingapp.hairsalon.entity.SalaryMonth;
import com.hairsalonbookingapp.hairsalon.model.FeedbackResponse;
import com.hairsalonbookingapp.hairsalon.model.RequestFeedback;
import com.hairsalonbookingapp.hairsalon.model.RequestSalaryMonth;
import com.hairsalonbookingapp.hairsalon.model.SalaryMonthResponse;
import com.hairsalonbookingapp.hairsalon.service.FeedbackService;
import com.hairsalonbookingapp.hairsalon.service.SalaryMonthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salaryMonth")
@CrossOrigin("*")
@SecurityRequirement(name = "api") // tao controller moi nho copy qua
public class SalaryMonthAPI {
    @Autowired
    SalaryMonthService salaryMonthService;

    @PostMapping("{id}")
//    @PreAuthorize("hasAuthority('customer')")
    public ResponseEntity createSalaryMonth(@Valid @RequestBody RequestSalaryMonth requestSalaryMonth, @PathVariable String id){
        SalaryMonthResponse salaryMonthResponse = salaryMonthService.createSalaryMonth(requestSalaryMonth, id);
        return ResponseEntity.ok(salaryMonthResponse);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteSalaryMonth(@PathVariable int id){
        SalaryMonthResponse salaryMonthResponse = salaryMonthService.deleteSalaryMonth(id);
        return ResponseEntity.ok(salaryMonthResponse);
    }

    @GetMapping
    public ResponseEntity getAllSalaryMonth(){
        List<SalaryMonth> salaryMonths = salaryMonthService.getAllSalaryMonth();
        return ResponseEntity.ok(salaryMonths);
    }
}
