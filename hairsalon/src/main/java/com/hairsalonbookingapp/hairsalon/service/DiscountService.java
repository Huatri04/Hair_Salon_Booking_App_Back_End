package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.DiscountProgram;
import com.hairsalonbookingapp.hairsalon.repository.DiscountCodeRepository;
import com.hairsalonbookingapp.hairsalon.repository.DiscountProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscountService {

    @Autowired
    DiscountCodeRepository discountCodeRepository;

    @Autowired
    DiscountProgramRepository discountProgramRepository;

    //tạo mới discount program

}
