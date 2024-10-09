package com.hairsalonbookingapp.hairsalon.model;

import com.hairsalonbookingapp.hairsalon.entity.SalaryMonth;
import com.hairsalonbookingapp.hairsalon.entity.Transaction;
import lombok.Data;

import java.util.List;

@Data
public class TransactionListResponse {
    private List<Transaction> content;
    private int pageNumber;
    private long totalElement;
    private int totalPage;
}
