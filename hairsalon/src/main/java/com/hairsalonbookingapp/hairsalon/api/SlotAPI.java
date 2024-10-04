package com.hairsalonbookingapp.hairsalon.api;

import com.hairsalonbookingapp.hairsalon.entity.Slot;
import com.hairsalonbookingapp.hairsalon.model.SlotRequest;
import com.hairsalonbookingapp.hairsalon.model.SlotResponse;
import com.hairsalonbookingapp.hairsalon.service.SlotService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "api")
public class SlotAPI {

    @Autowired
    SlotService slotService;

    @PostMapping("/slot")
    public ResponseEntity createNewSlot(@Valid @RequestBody SlotRequest slotRequest){
        List<SlotResponse> slots = slotService.createSlots(slotRequest);
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/slot/{shiftEmployeeId}")
    public ResponseEntity getAllSLots(@PathVariable long shiftEmployeeId){
        List<SlotResponse> slots = slotService.getAllSlots(shiftEmployeeId);
        return ResponseEntity.ok(slots);
    }

    @DeleteMapping("/slot/{slotId}")
    public ResponseEntity deleteSlot(@PathVariable long slotId){
        SlotResponse slot = slotService.deleteSLot(slotId);
        return ResponseEntity.ok(slot);
    }

    @PutMapping("/slot/{slotId}")
    public ResponseEntity completeSlot(@PathVariable long slotId){
        SlotResponse slot = slotService.updateSlot(slotId);
        return ResponseEntity.ok(slot);
    }

}
