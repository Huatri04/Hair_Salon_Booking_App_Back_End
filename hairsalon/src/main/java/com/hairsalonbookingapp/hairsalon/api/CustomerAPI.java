package com.hairsalonbookingapp.hairsalon.api;

import com.hairsalonbookingapp.hairsalon.model.CustomerAccountInfo;
import com.hairsalonbookingapp.hairsalon.model.CustomerResponsePage;
import com.hairsalonbookingapp.hairsalon.service.CustomerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@SecurityRequirement(name = "api")
public class CustomerAPI {

    @Autowired
    CustomerService customerService;

    @GetMapping
    public ResponseEntity getAllCustomers(@RequestParam int page, @RequestParam(defaultValue = "2") int size){
        CustomerResponsePage customerResponsePage = customerService.getAllCustomerAccounts(page, size);
        return ResponseEntity.ok(customerResponsePage);
    }

    @GetMapping("/deleted")
    public ResponseEntity getAllBanedCustomers(@RequestParam int page, @RequestParam(defaultValue = "2") int size){
        CustomerResponsePage customerResponsePage = customerService.getAllBanedCustomerAccounts(page, size);
        return ResponseEntity.ok(customerResponsePage);
    }
}
