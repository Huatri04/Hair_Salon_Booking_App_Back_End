package com.hairsalonbookingapp.hairsalon.model;

import com.hairsalonbookingapp.hairsalon.entity.DiscountProgram;
import com.hairsalonbookingapp.hairsalon.entity.Feedback;
import lombok.Data;

import java.util.List;

@Data
public class DiscountProgramListResponse {
    private List<DiscountProgram> content;
    private int pageNumber;
    private long totalElement;
    private int totalPage;
}
