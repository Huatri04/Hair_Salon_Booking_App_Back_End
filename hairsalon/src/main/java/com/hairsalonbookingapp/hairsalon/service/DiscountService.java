package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.DiscountProgram;
import com.hairsalonbookingapp.hairsalon.repository.DiscountCodeRepository;
import com.hairsalonbookingapp.hairsalon.repository.DiscountProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscountService {

    @Autowired
    DiscountProgramRepository discountProgramRepository;

    @Autowired
    DiscountCodeRepository discountCodeRepository;

    //tạo mới discount program
    public DiscountProgram createNewProgram(DiscountProgram discountProgram){
        DiscountProgram newProgram = discountProgramRepository.save(discountProgram);
        return newProgram;
    }

    //update discount program
    public DiscountProgram updateProgram(DiscountProgram discountProgram, int id){
        DiscountProgram oldProgram = discountProgramRepository.findDiscountProgramById(id);
        /*if(oldProgram != null){
            oldProgram.setAmount(discountProgram.getAmount());
            oldProgram.setName(discountProgram.getName());
            oldProgram.setDescription(discountProgram.getDescription());
            oldProgram.setStartDate(discountProgram.getStartDate());
            oldProgram.setEndDate(discountProgram.getEndDate());
            oldProgram.setStatus(discountProgram.getStatus());*/
        return null;

    }

}
