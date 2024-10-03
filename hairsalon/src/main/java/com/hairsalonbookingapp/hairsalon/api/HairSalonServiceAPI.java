package com.hairsalonbookingapp.hairsalon.api;

import com.hairsalonbookingapp.hairsalon.entity.HairSalonService;
import com.hairsalonbookingapp.hairsalon.model.HairSalonServiceRequest;
import com.hairsalonbookingapp.hairsalon.model.HairSalonServiceUpdate;
import com.hairsalonbookingapp.hairsalon.service.HairSalonBookingAppService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "api")
public class HairSalonServiceAPI {

    @Autowired
    HairSalonBookingAppService hairSalonBookingAppService;

    @PostMapping("/service")
    public ResponseEntity createNewHairSalonService(@Valid @RequestBody HairSalonServiceRequest hairSalonServiceRequest){
        HairSalonService hairSalonService = hairSalonBookingAppService.createNewService(hairSalonServiceRequest);
        return ResponseEntity.ok(hairSalonService);
    }

    @PutMapping("/service/id")
    public ResponseEntity updateHairSalonService(@Valid @RequestBody HairSalonServiceUpdate hairSalonServiceUpdate, @PathVariable long id){
        HairSalonService hairSalonService = hairSalonBookingAppService.updateService(hairSalonServiceUpdate,id);
        return ResponseEntity.ok(hairSalonService);
    }

    @DeleteMapping("/service/id")
    public ResponseEntity deleteHairSalonService(@PathVariable long id){
        HairSalonService hairSalonService = hairSalonBookingAppService.deleteService(id);
        return ResponseEntity.ok(hairSalonService);
    }

    @PutMapping("/service/id")
    public ResponseEntity restartHairSalonService(@PathVariable long id){
        HairSalonService hairSalonService = hairSalonBookingAppService.startService(id);
        return ResponseEntity.ok(hairSalonService);
    }
}
