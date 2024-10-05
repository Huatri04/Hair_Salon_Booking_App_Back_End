package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.*;
import com.hairsalonbookingapp.hairsalon.exception.Duplicate;
import com.hairsalonbookingapp.hairsalon.model.*;
import com.hairsalonbookingapp.hairsalon.repository.FeedbackRepository;
import com.hairsalonbookingapp.hairsalon.repository.TransactionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {
    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    ModelMapper modelMapper;
    // create Transaction
    public TransactionResponse createTransaction(RequestTransaction requestTransaction){
        Transaction transaction = modelMapper.map(requestTransaction, Transaction.class);
        try{
//            String newId = generateId();
//            feedback.setFeedbackId(newId);
            AccountForEmployee accountForEmployee = authenticationService.getCurrentAccountForEmployee();
            if(accountForEmployee == null){
                throw new Duplicate("No current employee found.");
            }
            transaction.setEmployee(accountForEmployee);
            Transaction newTransaction = transactionRepository.save(transaction);
            return modelMapper.map(newTransaction, TransactionResponse.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            if(e.getMessage().contains(transaction.getEmployee() + "")){
                throw new Duplicate("duplicate employee! ");
            }
        }
        return null;
    }

//    public String generateId() {
//        // Tìm ID cuối cùng theo vai trò
//        Optional<Feedback> lastFeedback = feedbackRepository.findTopByOrderByFeedbackIdDesc();
//        int newIdNumber = 1; // Mặc định bắt đầu từ 1
//
//        // Nếu có tài khoản cuối cùng, lấy ID
//        if (lastFeedback.isPresent()) {
//            String lastId = lastFeedback.get().getFeedbackId();
//            newIdNumber = Integer.parseInt(lastId.replaceAll("\\D+", "")) + 1; // Tăng số lên 1
//        }
//
//
//        String prefix = "FB";
//
//        return String.format("%s%06d", prefix, newIdNumber); // Tạo ID mới với format
//    }


    //delete Transaction
    public TransactionResponse deleteTransaction(int transactionId){
        // tim toi id ma FE cung cap
        Transaction transactionNeedDelete = transactionRepository.findTransactionByTransactionId(transactionId);
        if(transactionNeedDelete == null){
            throw new Duplicate("Feedback not found!"); // dung tai day
        }

        transactionNeedDelete.setDeleted(true);
        Transaction deletedTransaction = transactionRepository.save(transactionNeedDelete);
        return modelMapper.map(deletedTransaction, TransactionResponse.class);
    }

    // show list of Transaction
    public List<Transaction> getAllTransaction(){
        List<Transaction> transactions = transactionRepository.findTransactionsByIsDeletedFalse();
        return transactions;
    }

    //GET PROFILE SalaryMonth
    public TransactionResponse getInfoTransaction(int id){
        Transaction transaction = transactionRepository.findTransactionByTransactionId(id);
        return modelMapper.map(transaction, TransactionResponse.class);
    }
}
