package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.*;
import com.hairsalonbookingapp.hairsalon.exception.Duplicate;
import com.hairsalonbookingapp.hairsalon.exception.EntityNotFoundException;
import com.hairsalonbookingapp.hairsalon.model.request.CompleteAppointmentRequest;
import com.hairsalonbookingapp.hairsalon.model.request.RequestTransaction;
import com.hairsalonbookingapp.hairsalon.model.response.TransactionListResponse;
import com.hairsalonbookingapp.hairsalon.model.response.TransactionResponse;
import com.hairsalonbookingapp.hairsalon.repository.AppointmentRepository;
import com.hairsalonbookingapp.hairsalon.repository.EmployeeRepository;
import com.hairsalonbookingapp.hairsalon.repository.PaymentRepository;
import com.hairsalonbookingapp.hairsalon.repository.TransactionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TransactionService {
    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    AppointmentService appointmentService;

    @Autowired
    PaymentRepository paymentRepository;





    @Autowired
    ModelMapper modelMapper;
    // create Transaction
    public TransactionResponse createTransactionInCast(CompleteAppointmentRequest orderRequest) {
        Appointment appointment = appointmentService.completeAppointment(orderRequest);
        AccountForEmployee accountForEmployee = authenticationService.getCurrentAccountForEmployee();
        try {
            // Tạo payment
            Payment payment = new Payment();
            payment.setAppointment(appointment);
            payment.setCreateAt(new Date());
            payment.setTypePayment("Cash");

            List<Transaction> transactions = new ArrayList<>();

            // Tạo giao dịch
            Transaction transaction = new Transaction();
            transaction.setMoney(appointment.getCost());
            transaction.setDate(new Date());
            transaction.setEmployee(accountForEmployee);

            transaction.setCustomer(appointment.getAccountForCustomer());
            transaction.setPayment(payment);
            transaction.setStatus("Success");
            transaction.setDescription("Thanh toán trực tiếp tại quầy");
            transactions.add(transaction);

            // Thiết lập giao dịch trong payment
            payment.setTransactions(transactions);

            // Lưu payment trước
            paymentRepository.save(payment);

            // Không cần lưu giao dịch riêng biệt nếu đã sử dụng CascadeType.ALL
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
    public TransactionListResponse getAllTransaction(int page, int size){
//        List<Transaction> transactions = transactionRepository.findTransactionsByIsDeletedFalse();
//        return transactions;
        Page transactionPage = transactionRepository.findTransactionsByIsDeletedFalseOrderByDateDesc(PageRequest.of(page, size));
        TransactionListResponse transactionListResponse = new TransactionListResponse();
        transactionListResponse.setTotalPage(transactionPage.getTotalPages());
        transactionListResponse.setContent(transactionPage.getContent());
        transactionListResponse.setPageNumber(transactionPage.getNumber());
        transactionListResponse.setTotalElement(transactionPage.getTotalElements());
        return transactionListResponse;
    }

    //GET PROFILE SalaryMonth
    public TransactionResponse getInfoTransaction(int id){
        Transaction transaction = transactionRepository.findTransactionByTransactionId(id);
        return modelMapper.map(transaction, TransactionResponse.class);
    }
}
