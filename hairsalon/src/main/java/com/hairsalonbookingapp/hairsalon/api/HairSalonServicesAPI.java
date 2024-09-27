package com.hairsalonbookingapp.hairsalon.api;

import com.hairsalonbookingapp.hairsalon.entity.HairSalonService;
import com.hairsalonbookingapp.hairsalon.service.HairSalonCRUDService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/HairSalonService")
@SecurityRequirement(name = "api")
public class HairSalonServicesAPI {

    @Autowired
    HairSalonCRUDService hairSalonCRUDService;

    //tạo mới service
    @PostMapping
    public ResponseEntity createNewService(@Valid @RequestBody HairSalonService hairSalonService){
        HairSalonService newService = hairSalonCRUDService.createNewService(hairSalonService);
        return ResponseEntity.ok(newService);
    }

    //cập nhật service
    @PutMapping("id")
    public ResponseEntity updateService(@Valid @RequestBody HairSalonService hairSalonService, @PathVariable long id){
        HairSalonService updatedService = hairSalonCRUDService.updateService(hairSalonService, id);
        return ResponseEntity.ok(updatedService);
    }

    //xem tất cả service
    @GetMapping
    public ResponseEntity getAllService(){
        List<HairSalonService> list = hairSalonCRUDService.getAllServices();
        return ResponseEntity.ok(list);
    }

    //xóa service
    @DeleteMapping("id")
    public ResponseEntity deleteService(@PathVariable long id){
        HairSalonService deletedService = hairSalonCRUDService.deleteService(id);
        return ResponseEntity.ok(deletedService);
    }


}
