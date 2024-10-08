package com.hairsalonbookingapp.hairsalon.model;

import com.hairsalonbookingapp.hairsalon.entity.Feedback;
import com.hairsalonbookingapp.hairsalon.entity.SalaryMonth;
import lombok.Data;

import java.util.List;

@Data
public class SalaryMonthListResponse {
    private List<SalaryMonth> content;
    private int pageNumber;
    private long totalElement;
    private int totalPage;
}
