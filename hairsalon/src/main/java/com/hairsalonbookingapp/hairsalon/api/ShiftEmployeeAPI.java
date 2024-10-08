package com.hairsalonbookingapp.hairsalon.api;

import com.hairsalonbookingapp.hairsalon.entity.ShiftEmployee;
import com.hairsalonbookingapp.hairsalon.model.ShiftEmployeeResponse;
import com.hairsalonbookingapp.hairsalon.service.ShiftEmployeeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "api")
public class ShiftEmployeeAPI {

    @Autowired
    ShiftEmployeeService shiftEmployeeService;

    // [STYLIST]
    @PostMapping("/shiftEmployee/{day}")
    public ResponseEntity createShiftEmployee(@PathVariable String day){
        ShiftEmployeeResponse shift = shiftEmployeeService.createNewShiftEmployee(day);
        return ResponseEntity.ok(shift);
    }

    // [STYLIST]
    @DeleteMapping("/shiftEmployee/{day}")
    public ResponseEntity deleteShiftEmployee(@PathVariable String day){
        List<String> shifts = shiftEmployeeService.deleteShiftEmployee(day);
        return ResponseEntity.ok(shifts);
    }

    // [STYLIST]
    @PutMapping("/shiftEmployee/restart/{day}")
    public ResponseEntity restartShiftEmployee(@PathVariable String day){
        ShiftEmployeeResponse shift = shiftEmployeeService.restartShiftEmployee(day);
        return ResponseEntity.ok(shift);
    }

    // [STYLIST]
    @GetMapping("/shiftEmployee")
    public ResponseEntity getAllShiftEmployee(){
        List<ShiftEmployeeResponse> shiftEmployeeList = shiftEmployeeService.getEmployeeShift();
        return ResponseEntity.ok(shiftEmployeeList);
    }

    // [MANAGER]
    @PutMapping("/shiftEmployee/completeAll/{day}")
    public ResponseEntity completeAllShiftEmployeeInDay(@PathVariable String day){
        List<String> shift = shiftEmployeeService.completeAllShiftEmployeeInDay(day);
        return ResponseEntity.ok(shift);
    }

    // [CUSTOMER]
    @GetMapping("/availableShiftEmployee/{stylistId}")
    public ResponseEntity getAvailableShiftEmployee(@PathVariable String stylistId){
        List<ShiftEmployeeResponse> shiftEmployeeList = shiftEmployeeService.getAvailableShiftEmployees(stylistId);
        return ResponseEntity.ok(shiftEmployeeList);
    }


}
