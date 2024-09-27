package com.hairsalonbookingapp.hairsalon.api;

import com.hairsalonbookingapp.hairsalon.entity.AccountForCustomer;
import com.hairsalonbookingapp.hairsalon.entity.AccountForEmployee;
import com.hairsalonbookingapp.hairsalon.model.*;
import com.hairsalonbookingapp.hairsalon.service.AuthenticationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin("*") // cho phep tat ca truy cap, ket noi FE va BE vs nhau
@SecurityRequirement(name = "api") // tao controller moi nho copy qua
public class AuthenticationAPI {
    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/loginCustomer")
    public ResponseEntity LoginForCustomer(@Valid @RequestBody LoginRequestForCustomer loginRequestForCustomer){
        AccountForCustomerResponse CustomerAccount = authenticationService.loginForCustomer(loginRequestForCustomer);
        return ResponseEntity.ok(CustomerAccount);
    }

    @PostMapping("/loginEmployee")
    public ResponseEntity LoginForEmployee(@Valid @RequestBody LoginRequestForEmployee loginRequestForEmployee){
        AccountForEmployeeResponse EmployeeAccount = authenticationService.loginForEmployee(loginRequestForEmployee);
        return ResponseEntity.ok(EmployeeAccount);
        //return ResponseEntity.ok("Hi");
    }

    // dang ki cua customer
    @PostMapping("/register")
    public ResponseEntity register(@Valid @RequestBody RegisterRequestForCustomer account){
        //nho thang nay tao dum acc
        AccountForCustomerResponse newAccount = authenticationService.register(account);
        return ResponseEntity.ok(newAccount);
    }

    //dangki cua employee
    @PostMapping("/registerEmployee")
    public ResponseEntity registerEmployee(@Valid @RequestBody RegisterRequestForEmloyee account) {
        // Gọi service để đăng ký nhân viên
        AccountForEmployeeResponse newAccount = authenticationService.register(account);

        return ResponseEntity.ok(newAccount);

    }

    // update profile cua customer
    @PutMapping("/profile/{id}")
    public ResponseEntity updatedAccountCustomer(@Valid @RequestBody RequestEditProfileCustomer account, @PathVariable String id){ //@PathVariable de tim thang id tu FE
        EditProfileCustomerResponse oldAccount = authenticationService.updatedAccount(account, id);
        return ResponseEntity.ok(oldAccount);
    }

    //update profile cua employee
    @PutMapping("/profileEmployee/{id}")
    public ResponseEntity updatedAccountEmployee(@Valid @RequestBody RequestEditProfileEmployee account, @PathVariable String id){ //@PathVariable de tim thang id tu FE
        EditProfileEmployeeResponse oldAccount = authenticationService.updatedAccount(account, id);
        return ResponseEntity.ok(oldAccount);
    }
}
