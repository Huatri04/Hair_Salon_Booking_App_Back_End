package com.hairsalonbookingapp.hairsalon.api;

import com.hairsalonbookingapp.hairsalon.entity.ShiftInWeek;
import com.hairsalonbookingapp.hairsalon.service.ShiftWeekService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "api")
public class ShiftWeekAPI {

    @Autowired
    ShiftWeekService shiftWeekService;

    @PostMapping("/shiftInWeek")
    public ResponseEntity createNewShiftWeek(@Valid @RequestBody ShiftInWeek shiftInWeek){
        ShiftInWeek shift = shiftWeekService.createWeekShift(shiftInWeek);
        return ResponseEntity.ok(shift);
    }

    @PutMapping("/shiftInWeek/day")
    public ResponseEntity updateShiftInWeek(@Valid @RequestBody ShiftInWeek shiftInWeek, @PathVariable String day){
        ShiftInWeek shift = shiftWeekService.updateShift(shiftInWeek, day);
        return ResponseEntity.ok(shift);
    }

    @DeleteMapping("/shiftInWeek/day")
    public ResponseEntity deleteShiftInWeek(@PathVariable String day){
        ShiftInWeek shift = shiftWeekService.deleteShift(day);
        return ResponseEntity.ok(shift);
    }

    @GetMapping("/shiftInWeek")
    public ResponseEntity getAllShiftInWeek(){
        List<ShiftInWeek> shiftInWeekList = shiftWeekService.getAllShift();
        return ResponseEntity.ok(shiftInWeekList);
    }

    @GetMapping("/shiftInWeek/day")
    public ResponseEntity getshiftInWeekByday(@PathVariable String day){
        ShiftInWeek shift = shiftWeekService.getShift(day);
        return ResponseEntity.ok(shift);
    }

}
