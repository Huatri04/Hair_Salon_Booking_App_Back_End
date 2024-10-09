package com.hairsalonbookingapp.hairsalon.model;

import com.hairsalonbookingapp.hairsalon.entity.Feedback;
import com.hairsalonbookingapp.hairsalon.entity.SoftwareSupportApplication;
import lombok.Data;

import java.util.List;

@Data
public class SoftwareSupportApplicationListResponse {
    private List<SoftwareSupportApplication> content;
    private int pageNumber;
    private long totalElement;
    private int totalPage;
}
