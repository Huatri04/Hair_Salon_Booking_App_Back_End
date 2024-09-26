package com.hairsalonbookingapp.hairsalon.api;

import com.hairsalonbookingapp.hairsalon.entity.Feedback;
import com.hairsalonbookingapp.hairsalon.model.FeedbackResponse;
import com.hairsalonbookingapp.hairsalon.model.RequestFeedback;
import com.hairsalonbookingapp.hairsalon.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin("*")
public class FeedbackAPI {
    @Autowired
    FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity createFeedback(@Valid @RequestBody RequestFeedback requestFeedback){
        FeedbackResponse feedbackResponse = feedbackService.createFeedback(requestFeedback);
        return ResponseEntity.ok(feedbackResponse);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteFeedback(@PathVariable String id){
        FeedbackResponse feedbackResponse = feedbackService.deleteFeedback(id);
        return ResponseEntity.ok(feedbackResponse);
    }

    @GetMapping
    public ResponseEntity getAllFeedback(){
        List<Feedback> feedbacks = feedbackService.getAllFeedback();
        return ResponseEntity.ok(feedbacks);
    }
}
