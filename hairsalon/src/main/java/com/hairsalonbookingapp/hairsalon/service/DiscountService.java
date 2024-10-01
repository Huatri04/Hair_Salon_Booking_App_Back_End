package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.DiscountProgram;
import com.hairsalonbookingapp.hairsalon.exception.EntityNotFoundException;
import com.hairsalonbookingapp.hairsalon.model.DiscountProgramUpdateRequest;
import com.hairsalonbookingapp.hairsalon.repository.DiscountCodeRepository;
import com.hairsalonbookingapp.hairsalon.repository.DiscountProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public DiscountProgram updateProgram(DiscountProgramUpdateRequest discountProgramUpdateRequest, int id){
        DiscountProgram oldProgram = discountProgramRepository.findDiscountProgramById(id);
        if(oldProgram != null){
            if(!discountProgramUpdateRequest.getName().isEmpty()){
                oldProgram.setName(discountProgramUpdateRequest.getName());
            }

            if(!discountProgramUpdateRequest.getDescription().isEmpty()){
                oldProgram.setDescription(discountProgramUpdateRequest.getDescription());
            }

            if(!discountProgramUpdateRequest.getStartDate().isEmpty()){
                oldProgram.setStartDate(discountProgramUpdateRequest.getStartDate());
            }

            if(!discountProgramUpdateRequest.getEndDate().isEmpty()){
                oldProgram.setEndDate(discountProgramUpdateRequest.getEndDate());
            }

            DiscountProgram newProgram = discountProgramRepository.save(oldProgram);
            return newProgram;
        } else {
            throw new EntityNotFoundException("Program not found!");
        }
    }

    //Start program
    public DiscountProgram startProgram(int id){
        DiscountProgram oldProgram = discountProgramRepository.findDiscountProgramById(id);
        if(oldProgram != null){
            oldProgram.setStatus("In process");
            DiscountProgram newProgram = discountProgramRepository.save(oldProgram);
            return newProgram;
        } else {
            throw new EntityNotFoundException("Program not found!");
        }
    }

    //End program
    public DiscountProgram deleteProgram(int id){
        DiscountProgram oldProgram = discountProgramRepository.findDiscountProgramById(id);
        if(oldProgram != null){
            oldProgram.setStatus("Ended");
            DiscountProgram newProgram = discountProgramRepository.save(oldProgram);
            return newProgram;
        } else {
            throw new EntityNotFoundException("Program not found!");
        }
    }

    //get all program
    public List<DiscountProgram> getAllProgram(){
        List<DiscountProgram> list = discountProgramRepository.findAll();
        return list;
    }

}
