package com.hairsalonbookingapp.hairsalon.api;

import com.hairsalonbookingapp.hairsalon.entity.Feedback;
import com.hairsalonbookingapp.hairsalon.entity.SoftwareSupportApplication;
import com.hairsalonbookingapp.hairsalon.model.FeedbackResponse;
import com.hairsalonbookingapp.hairsalon.model.RequestFeedback;
import com.hairsalonbookingapp.hairsalon.model.RequestSoftwareSupportApplication;
import com.hairsalonbookingapp.hairsalon.model.SoftwareSupportApplicationResponse;
import com.hairsalonbookingapp.hairsalon.service.FeedbackService;
import com.hairsalonbookingapp.hairsalon.service.SoftwareSupportApplicationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/softwareSupportApplication")
@CrossOrigin("*")
@SecurityRequirement(name = "api") // tao controller moi nho copy qua
public class SoftwareSupportApplicationAPI {
    @Autowired
    SoftwareSupportApplicationService softwareSupportApplicationService;

    @PostMapping
//    @PreAuthorize("hasAuthority('customer')")
    public ResponseEntity createSoftwareSupportApplication(@Valid @RequestBody RequestSoftwareSupportApplication requestSoftwareSupportApplication){
        SoftwareSupportApplicationResponse softwareSupportApplicationResponse = softwareSupportApplicationService.createSoftwareSupportApplication(requestSoftwareSupportApplication);
        return ResponseEntity.ok(softwareSupportApplicationResponse);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteSoftwareSupportApplication(@PathVariable int id){
        SoftwareSupportApplicationResponse softwareSupportApplicationResponse = softwareSupportApplicationService.deleteSoftwareSupportApplication(id);
        return ResponseEntity.ok(softwareSupportApplicationResponse);
    }

    @GetMapping
    public ResponseEntity getAllSoftwareSupportApplication(){
        List<SoftwareSupportApplication> softwareSupportApplications = softwareSupportApplicationService.getAllSoftwareSupportApplication();
        return ResponseEntity.ok(softwareSupportApplications);
    }
}
