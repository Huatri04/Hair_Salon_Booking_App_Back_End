package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.DiscountCode;
import com.hairsalonbookingapp.hairsalon.entity.DiscountProgram;
import com.hairsalonbookingapp.hairsalon.exception.DuplicateEntity;
import com.hairsalonbookingapp.hairsalon.exception.EntityNotFoundException;
import com.hairsalonbookingapp.hairsalon.model.DiscountProgramUpdateRequest;
import com.hairsalonbookingapp.hairsalon.repository.DiscountCodeRepository;
import com.hairsalonbookingapp.hairsalon.repository.DiscountProgramRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscountService {

    @Autowired
    DiscountProgramRepository discountProgramRepository;

    @Autowired
    DiscountCodeRepository discountCodeRepository;

    @Autowired
    ModelMapper modelMapper;

    //tạo mới discount program
    public DiscountProgram createNewProgram(DiscountProgramUpdateRequest discountProgramUpdateRequest){
        try{
            DiscountProgram newProgram = discountProgramRepository.save(modelMapper.map(discountProgramUpdateRequest, DiscountProgram.class));
            return newProgram;
        } catch (Exception e){
            throw new DuplicateEntity("Duplicate name!");
        }
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

    //get program by id
    public DiscountProgram getProgram(int id){
        DiscountProgram oldProgram = discountProgramRepository.findDiscountProgramById(id);
        if(oldProgram != null){
            return oldProgram;
        } else {
            throw new EntityNotFoundException("Program not found!");
        }
    }

    //tạo mới discount code


}
