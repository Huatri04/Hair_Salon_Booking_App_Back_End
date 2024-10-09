package com.hairsalonbookingapp.hairsalon.model;

import com.hairsalonbookingapp.hairsalon.entity.DiscountCode;
import com.hairsalonbookingapp.hairsalon.entity.Feedback;
import lombok.Data;

import java.util.List;

@Data
public class DiscountCodeListResponse {
    private List<DiscountCode> content;
    private int pageNumber;
    private long totalElement;
    private int totalPage;
}
