package com.hairsalonbookingapp.hairsalon.repository;

import com.hairsalonbookingapp.hairsalon.entity.Feedback;
import com.hairsalonbookingapp.hairsalon.entity.SalaryMonth;
import com.hairsalonbookingapp.hairsalon.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    Optional<Transaction> findTopByOrderByTransactionIdDesc();
    Transaction findTransactionByTransactionId(int transactionId);
    List<Transaction> findTransactionsByIsDeletedFalse();
    Page<Transaction> findTransactionsByIsDeletedFalseOrderByDateDesc(Pageable pageable);
}
