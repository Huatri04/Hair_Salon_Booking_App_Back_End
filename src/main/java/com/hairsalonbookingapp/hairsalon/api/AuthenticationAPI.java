package com.hairsalonbookingapp.hairsalon.api;

import com.hairsalonbookingapp.hairsalon.entity.AccountForCustomer;
import com.hairsalonbookingapp.hairsalon.entity.AccountForEmployee;
import com.hairsalonbookingapp.hairsalon.model.AccountForCustomerResponse;
import com.hairsalonbookingapp.hairsalon.model.AccountForEmployeeResponse;
import com.hairsalonbookingapp.hairsalon.model.RegisterRequestForCustomer;
import com.hairsalonbookingapp.hairsalon.model.RegisterRequestForEmloyee;
import com.hairsalonbookingapp.hairsalon.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin("*") // cho phep tat ca truy cap, ket noi FE va BE vs nhau
public class AuthenticationAPI {
    @Autowired
    AuthenticationService authenticationService;

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
    public ResponseEntity updatedAccountCustomer(@Valid @RequestBody AccountForCustomer account, @PathVariable String id){ //@PathVariable de tim thang id tu FE
        AccountForCustomer oldAccount = authenticationService.updatedAccount(account, id);
        return ResponseEntity.ok(oldAccount);
    }

    //update profile cua employee
    @PutMapping("/profileEmployee/{id}")
    public ResponseEntity updatedAccountEmployee(@Valid @RequestBody AccountForEmployee account, @PathVariable String id){ //@PathVariable de tim thang id tu FE
        AccountForEmployee oldAccount = authenticationService.updatedAccount(account, id);
        return ResponseEntity.ok(oldAccount);
    }
}
