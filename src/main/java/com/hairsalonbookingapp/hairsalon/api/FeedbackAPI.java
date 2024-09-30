package com.hairsalonbookingapp.hairsalon.api;

import com.hairsalonbookingapp.hairsalon.entity.Feedback;
import com.hairsalonbookingapp.hairsalon.model.FeedbackResponse;
import com.hairsalonbookingapp.hairsalon.model.RequestFeedback;
import com.hairsalonbookingapp.hairsalon.service.FeedbackService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin("*")
@SecurityRequirement(name = "api") // tao controller moi nho copy qua
public class FeedbackAPI {
    @Autowired
    FeedbackService feedbackService;

    @PostMapping
//    @PreAuthorize("hasAuthority('customer')")
    public ResponseEntity createFeedback(@Valid @RequestBody RequestFeedback requestFeedback){
        FeedbackResponse feedbackResponse = feedbackService.createFeedback(requestFeedback);
        return ResponseEntity.ok(feedbackResponse);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteFeedback(@PathVariable int id){
        FeedbackResponse feedbackResponse = feedbackService.deleteFeedback(id);
        return ResponseEntity.ok(feedbackResponse);
    }

    @GetMapping
    public ResponseEntity getAllFeedback(){
        List<Feedback> feedbacks = feedbackService.getAllFeedback();
        return ResponseEntity.ok(feedbacks);
    }
}
