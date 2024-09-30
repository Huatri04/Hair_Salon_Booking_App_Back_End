package com.hairsalonbookingapp.hairsalon.api;

import com.hairsalonbookingapp.hairsalon.entity.Feedback;
import com.hairsalonbookingapp.hairsalon.entity.Transaction;
import com.hairsalonbookingapp.hairsalon.model.FeedbackResponse;
import com.hairsalonbookingapp.hairsalon.model.RequestFeedback;
import com.hairsalonbookingapp.hairsalon.model.RequestTransaction;
import com.hairsalonbookingapp.hairsalon.model.TransactionResponse;
import com.hairsalonbookingapp.hairsalon.service.FeedbackService;
import com.hairsalonbookingapp.hairsalon.service.TransactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction")
@CrossOrigin("*")
@SecurityRequirement(name = "api") // tao controller moi nho copy qua
public class TransactionAPI {
    @Autowired
    TransactionService transactionService;

    @PostMapping
//    @PreAuthorize("hasAuthority('customer')")
    public ResponseEntity createTransaction(@Valid @RequestBody RequestTransaction requestTransaction){
        TransactionResponse transactionResponse = transactionService.createTransaction(requestTransaction);
        return ResponseEntity.ok(transactionResponse);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteTransaction(@PathVariable int id){
        TransactionResponse transactionResponse = transactionService.deleteTransaction(id);
        return ResponseEntity.ok(transactionResponse);
    }

    @GetMapping
    public ResponseEntity getAllTransaction(){
        List<Transaction> transactions = transactionService.getAllTransaction();
        return ResponseEntity.ok(transactions);
    }
}
