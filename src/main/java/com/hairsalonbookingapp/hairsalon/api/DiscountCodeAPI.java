package com.hairsalonbookingapp.hairsalon.api;

import com.hairsalonbookingapp.hairsalon.entity.DiscountCode;
import com.hairsalonbookingapp.hairsalon.entity.Feedback;
import com.hairsalonbookingapp.hairsalon.model.*;
import com.hairsalonbookingapp.hairsalon.service.DiscountCodeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discountCode")
@CrossOrigin("*")
@SecurityRequirement(name = "api") // tao controller moi nho copy qua
public class DiscountCodeAPI {
    @Autowired
    DiscountCodeService discountCodeService;
    @PostMapping("{id}")
    public ResponseEntity createDiscountCode(@Valid @RequestBody RequestDiscountCode requestDiscountCode, @PathVariable int id){
        DiscountCodeResponse discountCodeResponse = discountCodeService.createDiscountCode(requestDiscountCode, id);
        return ResponseEntity.ok(discountCodeResponse);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteDiscountCode(@PathVariable String id){
        DiscountCodeResponse discountCodeResponse = discountCodeService.deleteDiscountCode(id);
        return ResponseEntity.ok(discountCodeResponse);
    }

    @GetMapping
    public ResponseEntity getAllFeedback(){
        List<DiscountCode> discountCodes = discountCodeService.getAllDiscountCode();
        return ResponseEntity.ok(discountCodes);
    }

    @PutMapping("{id}")
    public ResponseEntity updatedDiscountCode(@Valid @RequestBody RequestUpdateDiscountCode requestUpdateDiscountCode, @PathVariable String id){ //@PathVariable de tim thang id tu FE
        UpdateDiscountCodeResponse oldDiscountCode = discountCodeService.updatedDiscountCode(requestUpdateDiscountCode, id);
        return ResponseEntity.ok(oldDiscountCode);
    }

    @GetMapping("{id}")
    public ResponseEntity getDiscountCodeInfo(@PathVariable String id){
        DiscountCodeInfResponse discountCodeInfResponse = discountCodeService.getInfoDiscountCode(id);
        return ResponseEntity.ok(discountCodeInfResponse);
    }
}
