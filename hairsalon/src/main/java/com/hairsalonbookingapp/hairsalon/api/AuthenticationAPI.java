package com.hairsalonbookingapp.hairsalon.api;

import com.hairsalonbookingapp.hairsalon.model.*;
import com.hairsalonbookingapp.hairsalon.service.AuthenticationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "api")
public class AuthenticationAPI {

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/registerCustomer")
    public ResponseEntity register(@Valid @RequestBody RegisterRequestForCustomer registerRequestForCustomer){
        // nhờ thằng authenticationService => tạo dùm cái account
        AccountResponseForCustomer newAccount = authenticationService.registerCustomer(registerRequestForCustomer);
        return ResponseEntity.ok(newAccount);
    }

    @PostMapping("/loginCustomer")
    public ResponseEntity LoginForCustomer(@Valid @RequestBody LoginRequestForCustomer loginRequestForCustomer){
        AccountResponseForCustomer CustomerAccount = authenticationService.loginForCustomer(loginRequestForCustomer);
        return ResponseEntity.ok(CustomerAccount);
    }

    @PostMapping("/registerEmployee")
    public ResponseEntity registerForEmployee(@Valid @RequestBody RegisterRequestForEmployee registerRequestForEmployee){
        // nhờ thằng authenticationService => tạo dùm cái account
        AccountResponseForEmployee newAccount = authenticationService.registerEmployee(registerRequestForEmployee);
        return ResponseEntity.ok(newAccount);
        //return ResponseEntity.ok("Hi");
    }

    @PostMapping("/loginEmployee")
    public ResponseEntity LoginForEmployee(@Valid @RequestBody LoginRequestForEmployee loginRequestForEmployee){
        AccountResponseForEmployee EmployeeAccount = authenticationService.loginForEmployee(loginRequestForEmployee);
        return ResponseEntity.ok(EmployeeAccount);
        //return ResponseEntity.ok("Hi");
    }

    @GetMapping("/ProfileCustomer")
    public ResponseEntity getProfileCustomer(){
        ProfileCustomer profileCustomer = authenticationService.getProfileCustomer();
        return ResponseEntity.ok(profileCustomer);
    }

    @GetMapping("/ProfileEmployee")
    public ResponseEntity getProfileEmployee(){
        ProfileEmployee profileEmployee = authenticationService.getProfileEmployee();
        return ResponseEntity.ok(profileEmployee);
    }

    @GetMapping("/ProfileCustomer/{phoneNumber}")
    public ResponseEntity getProfileCustomerByPhone(@PathVariable String phoneNumber){
        ProfileCustomer profileCustomer = authenticationService.getProfileCusById(phoneNumber);
        return ResponseEntity.ok(profileCustomer);
    }

    @GetMapping("/ProfileEmployee/{id}")
    public ResponseEntity getProfileEmployeeByID(@PathVariable String id){
        ProfileEmployee profileEmployee = authenticationService.getProfileEmpById(id);
        return ResponseEntity.ok(profileEmployee);
    }

}
