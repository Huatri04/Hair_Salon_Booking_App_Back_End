package com.hairsalonbookingapp.hairsalon.api;

import com.hairsalonbookingapp.hairsalon.entity.DiscountProgram;
import com.hairsalonbookingapp.hairsalon.entity.Feedback;
import com.hairsalonbookingapp.hairsalon.model.DiscountProgramResponse;
import com.hairsalonbookingapp.hairsalon.model.FeedbackResponse;
import com.hairsalonbookingapp.hairsalon.model.RequestDiscountprogram;
import com.hairsalonbookingapp.hairsalon.model.RequestFeedback;
import com.hairsalonbookingapp.hairsalon.repository.DiscountProgramRepository;
import com.hairsalonbookingapp.hairsalon.service.DiscountProgramService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discountProgram")
@CrossOrigin("*")
@SecurityRequirement(name = "api") // tao controller moi nho copy qua
public class DiscountProgramAPI {
    @Autowired
    DiscountProgramService discountProgramService;
    @PostMapping
    public ResponseEntity createDiscountProgram(@Valid @RequestBody RequestDiscountprogram requestDiscountprogram){
        DiscountProgramResponse discountProgramResponse = discountProgramService.createDiscountProgram(requestDiscountprogram);
        return ResponseEntity.ok(discountProgramResponse);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteDiscountProgram(@PathVariable String id){
        DiscountProgramResponse discountProgramResponse = discountProgramService.deleteDiscountProgram(id);
        return ResponseEntity.ok(discountProgramResponse);
    }

    @GetMapping
    public ResponseEntity getAllDiscountProgram(){
        List<DiscountProgram> discountPrograms = discountProgramService.getAllDiscountProgram();
        return ResponseEntity.ok(discountPrograms);
    }
}
