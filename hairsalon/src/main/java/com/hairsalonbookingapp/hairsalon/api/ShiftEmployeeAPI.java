package com.hairsalonbookingapp.hairsalon.api;

import com.hairsalonbookingapp.hairsalon.entity.ShiftEmployee;
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

    @PostMapping("/shiftEmployee")
    public ResponseEntity createShiftEmployee(@PathVariable String day){
        ShiftEmployee shift = shiftEmployeeService.createNewShiftEmployee(day);
        return ResponseEntity.ok(shift);
    }

    @DeleteMapping("/shiftEmployee/id")
    public ResponseEntity deleteShiftEmployee(@PathVariable long id){
        ShiftEmployee shift = shiftEmployeeService.deleteShiftEmployee(id);
        return ResponseEntity.ok(shift);
    }

    @GetMapping("/shiftEmployee")
    public ResponseEntity getAllShiftEmployee(){
        List<ShiftEmployee> shiftEmployeeList = shiftEmployeeService.getEmployeeShift();
        return ResponseEntity.ok(shiftEmployeeList);
    }


}
