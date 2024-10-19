package com.hairsalonbookingapp.hairsalon.api;

import com.hairsalonbookingapp.hairsalon.model.CustomerAccountInfo;
import com.hairsalonbookingapp.hairsalon.service.CustomerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "api")
public class CustomerAPI {

    @Autowired
    CustomerService customerService;

    @GetMapping("/customer")
    public ResponseEntity getAllCustomers(){
        List<CustomerAccountInfo> customerAccountInfoList = customerService.getAllCustomerAccounts();
        return ResponseEntity.ok(customerAccountInfoList);
    }
}
