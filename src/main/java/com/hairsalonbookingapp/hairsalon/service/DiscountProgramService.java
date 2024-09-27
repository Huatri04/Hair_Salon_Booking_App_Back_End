package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.AccountForCustomer;
import com.hairsalonbookingapp.hairsalon.entity.DiscountProgram;
import com.hairsalonbookingapp.hairsalon.entity.Feedback;
import com.hairsalonbookingapp.hairsalon.exception.Duplicate;
import com.hairsalonbookingapp.hairsalon.model.DiscountProgramResponse;
import com.hairsalonbookingapp.hairsalon.model.FeedbackResponse;
import com.hairsalonbookingapp.hairsalon.model.RequestDiscountprogram;
import com.hairsalonbookingapp.hairsalon.model.RequestFeedback;
import com.hairsalonbookingapp.hairsalon.repository.DiscountProgramRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiscountProgramService {
    @Autowired
    DiscountProgramRepository discountProgramRepository;

    @Autowired
    ModelMapper modelMapper;

    // create feedback
    public DiscountProgramResponse createDiscountProgram(RequestDiscountprogram requestDiscountprogram){
        DiscountProgram discountProgram = modelMapper.map(requestDiscountprogram, DiscountProgram.class);
        try{
            String newId = generateId();
            discountProgram.setDiscountProgramId(newId);
            DiscountProgram newDiscountProgram = discountProgramRepository.save(discountProgram);
            return modelMapper.map(newDiscountProgram, DiscountProgramResponse.class);
        } catch (Exception e) {
            if(e.getMessage().contains(discountProgram.getDiscountProgramId())){
                throw new Duplicate("duplicate discount program id! ");
            }
        }
        return null;
    }

    public String generateId() {
        // Tìm ID cuối cùng theo vai trò
        Optional<DiscountProgram> lasDiscountProgram = discountProgramRepository.findTopByOrderByDiscountProgramIdDesc();
        int newIdNumber = 1; // Mặc định bắt đầu từ 1

        // Nếu có tài khoản cuối cùng, lấy ID
        if (lasDiscountProgram.isPresent()) {
            String lastId = lasDiscountProgram.get().getDiscountProgramId();
            newIdNumber = Integer.parseInt(lastId.replaceAll("\\D+", "")) + 1; // Tăng số lên 1
        }


        String prefix = "DP";

        return String.format("%s%06d", prefix, newIdNumber); // Tạo ID mới với format
    }


    //delete feedback
    public DiscountProgramResponse deleteDiscountProgram(String discountProgramId){
        // tim toi id ma FE cung cap
        DiscountProgram discountProgramNeedDelete = discountProgramRepository.findDiscountProgramByDiscountProgramId(discountProgramId);
        if(discountProgramNeedDelete == null){
            throw new Duplicate("Feedback not found!"); // dung tai day
        }

        discountProgramNeedDelete.setDeleted(true);
        DiscountProgram deletedDiscountProgramm = discountProgramRepository.save(discountProgramNeedDelete);
        return modelMapper.map(deletedDiscountProgramm, DiscountProgramResponse.class);
    }

    // show list of feedback
    public List<DiscountProgram> getAllDiscountProgram(){
        List<DiscountProgram> discountPrograms = discountProgramRepository.findDiscountProgramsByIsDeletedFalse();
        return discountPrograms;
    }

    public DiscountProgram getCurrentDiscountProgram(){
        DiscountProgram discountProgram = (DiscountProgram) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return discountProgramRepository.findDiscountProgramByDiscountProgramId(discountProgram.getDiscountProgramId());
    }


}
