package com.hairsalonbookingapp.hairsalon.api;

import com.hairsalonbookingapp.hairsalon.entity.DiscountCode;
import com.hairsalonbookingapp.hairsalon.entity.DiscountProgram;
import com.hairsalonbookingapp.hairsalon.model.DiscountProgramRequest;
import com.hairsalonbookingapp.hairsalon.model.DiscountProgramUpdate;
import com.hairsalonbookingapp.hairsalon.service.DiscountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "api")
public class DiscountProgramAPI {

    @Autowired
    DiscountService discountService;

    @PostMapping("/discountProgram")
    public ResponseEntity createNewDiscountProgram(@Valid @RequestBody DiscountProgramRequest discountProgramRequest){
        DiscountProgram discountProgram = discountService.createNewProgram(discountProgramRequest);
        return ResponseEntity.ok(discountProgram);
    }

    @PutMapping("/discountProgram/id")
    public ResponseEntity updateDiscountProgram(@Valid @RequestBody DiscountProgramUpdate discountProgramUpdate, @PathVariable long id){
        DiscountProgram discountProgram = discountService.updateProgram(discountProgramUpdate, id);
        return ResponseEntity.ok(discountProgram);
    }

    @PutMapping("/start-discountProgram")
    public ResponseEntity startDiscountProgram(@PathVariable long id){
        DiscountProgram discountProgram = discountService.startProgram(id);
        return ResponseEntity.ok(discountProgram);
    }

    @DeleteMapping("/end-discountProgram")
    public ResponseEntity endDiscountProgram(@PathVariable long id){
        DiscountProgram discountProgram = discountService.deleteProgram(id);
        return ResponseEntity.ok(discountProgram);
    }

    @GetMapping("/discountProgram")
    public ResponseEntity getDiscountPrograms(){
        List<DiscountProgram> discountProgramList = discountService.getAllProgram();
        return ResponseEntity.ok(discountProgramList);
    }

    @PostMapping("/discountCode")
    public ResponseEntity createNewDiscountCode(@RequestBody String code, @PathVariable long programId){
        DiscountCode discountCode = discountService.createNewCode(code, programId);
        return ResponseEntity.ok(discountCode);
    }

}
