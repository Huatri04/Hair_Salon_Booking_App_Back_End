package com.hairsalonbookingapp.hairsalon.api;

import com.hairsalonbookingapp.hairsalon.model.EmployeeInfo;
import com.hairsalonbookingapp.hairsalon.model.EmployeeResponsePage;
import com.hairsalonbookingapp.hairsalon.model.FindEmployeeRequest;
import com.hairsalonbookingapp.hairsalon.model.StylistInfo;
import com.hairsalonbookingapp.hairsalon.service.EmployeeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "api")
public class EmployeeAPI {

    @Autowired
    EmployeeService employeeService;

    @PostMapping("/employee")
    public ResponseEntity getEmployeeByRole(@Valid @RequestBody FindEmployeeRequest findEmployeeRequest, @RequestParam int page, @RequestParam(defaultValue = "2") int size){
        EmployeeResponsePage employeeResponsePage = employeeService.getEmployeeByRole(findEmployeeRequest, page, size);
        return ResponseEntity.ok(employeeResponsePage);
    }

    @GetMapping("/stylist")
    public ResponseEntity getAllStylist(){
        List<EmployeeInfo> stylistInfos = employeeService.getAllAvailableStylist();
        return ResponseEntity.ok(stylistInfos);
    }

    @GetMapping("/employee")
    public ResponseEntity getAllEmployees(){
        List<EmployeeInfo> employeeInfoList = employeeService.getAllEmployees();
        return ResponseEntity.ok(employeeInfoList);
    }

    @GetMapping("/stylist/workDayNull")
    public ResponseEntity checkStylistHasNull(){
        List<String> stylistIdList = employeeService.getStylistsThatWorkDaysNull();
        return ResponseEntity.ok(stylistIdList);
    }

    @GetMapping("/employee/deleted")
    public ResponseEntity getAllBanedEmployees(){
        List<EmployeeInfo> employeeInfoList = employeeService.getAllBanedEmployees();
        return ResponseEntity.ok(employeeInfoList);
    }
}
