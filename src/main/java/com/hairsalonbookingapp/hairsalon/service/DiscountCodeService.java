package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.DiscountCode;
import com.hairsalonbookingapp.hairsalon.entity.Feedback;
import com.hairsalonbookingapp.hairsalon.exception.Duplicate;
import com.hairsalonbookingapp.hairsalon.model.DiscountCodeResponse;
import com.hairsalonbookingapp.hairsalon.model.FeedbackResponse;
import com.hairsalonbookingapp.hairsalon.model.RequestDiscountCode;
import com.hairsalonbookingapp.hairsalon.model.RequestFeedback;
import com.hairsalonbookingapp.hairsalon.repository.DiscountCodeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiscountCodeService {
    @Autowired
    DiscountCodeRepository discountCodeRepository;
    @Autowired
    ModelMapper modelMapper;

    // create feedback
    public DiscountCodeResponse createDiscountCode(RequestDiscountCode requestDiscountCode){
        DiscountCode discountCode = modelMapper.map(requestDiscountCode, DiscountCode.class);
        try{
            String newId = generateId();
            discountCode.setDiscountCodeId(newId);
            DiscountCode newDiscountCode = discountCodeRepository.save(discountCode);
            return modelMapper.map(newDiscountCode, DiscountCodeResponse.class);
        } catch (Exception e) {
            if(e.getMessage().contains(discountCode.getDiscountCodeId())){
                throw new Duplicate("duplicate start! ");
            }
        }
        return null;
    }

    public String generateId() {
        // Tìm ID cuối cùng theo vai trò
        Optional<DiscountCode> lastDiscountCode = discountCodeRepository.findTopByOrderByDiscountCodeIdDesc();
        int newIdNumber = 1; // Mặc định bắt đầu từ 1

        // Nếu có tài khoản cuối cùng, lấy ID
        if (lastDiscountCode.isPresent()) {
            String lastId = lastDiscountCode.get().getDiscountCodeId();
            newIdNumber = Integer.parseInt(lastId.replaceAll("\\D+", "")) + 1; // Tăng số lên 1
        }


        String prefix = "DC";

        return String.format("%s%06d", prefix, newIdNumber); // Tạo ID mới với format
    }


    //delete feedback
    public DiscountCodeResponse deleteDiscountCode(String discountCodeId){
        // tim toi id ma FE cung cap
        DiscountCode discountCodeNeedDelete = discountCodeRepository.findDiscountCodeByDiscountCodeId(discountCodeId);
        if(discountCodeNeedDelete == null){
            throw new Duplicate("Feedback not found!"); // dung tai day
        }

        discountCodeNeedDelete.setDeleted(true);
        DiscountCode deletedDiscountCode = discountCodeRepository.save(discountCodeNeedDelete);
        return modelMapper.map(deletedDiscountCode, DiscountCodeResponse.class);
    }

    // show list of feedback
    public List<DiscountCode> getAllDiscountCode(){
        List<DiscountCode> discountCodes = discountCodeRepository.findDiscountCodesByIsDeletedFalse();
        return discountCodes;
    }
}
