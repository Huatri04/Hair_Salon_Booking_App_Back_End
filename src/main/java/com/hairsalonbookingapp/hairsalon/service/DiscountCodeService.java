package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.AccountForCustomer;
import com.hairsalonbookingapp.hairsalon.entity.DiscountCode;
import com.hairsalonbookingapp.hairsalon.entity.DiscountProgram;
import com.hairsalonbookingapp.hairsalon.entity.Feedback;
import com.hairsalonbookingapp.hairsalon.exception.Duplicate;
import com.hairsalonbookingapp.hairsalon.exception.UpdatedException;
import com.hairsalonbookingapp.hairsalon.model.*;
import com.hairsalonbookingapp.hairsalon.repository.DiscountCodeRepository;
import com.hairsalonbookingapp.hairsalon.repository.DiscountProgramRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiscountCodeService {
    @Autowired
    DiscountCodeRepository discountCodeRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    DiscountProgramService discountProgramService;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    DiscountProgramRepository discountProgramRepository;

    // create feedback
    public DiscountCodeResponse createDiscountCode(RequestDiscountCode requestDiscountCode, int id){
        DiscountCode discountCode = modelMapper.map(requestDiscountCode, DiscountCode.class);
        try{
            String newId = generateId();
            discountCode.setDiscountCodeId(newId);
//            DiscountProgram discountProgram = discountProgramService.getCurrentDiscountProgram();
//            if(discountProgram == null){
//                throw new Duplicate("No current Discount program found.");
//            }
//            discountCode.setDiscountProgram(discountProgram);

            DiscountProgram discountProgram = discountProgramRepository.findDiscountProgramByDiscountProgramId(id);
            if(discountProgram == null){
                System.out.println("No current Discount program found.");
                throw new Duplicate("No current Discount program found.");
            }
            discountCode.setDiscountProgram(discountProgram);


            AccountForCustomer accountForCustomer = authenticationService.getCurrentAccountForCustomer();
            if(accountForCustomer != null){
                discountCode.setCustomer(accountForCustomer);
            }



            DiscountCode newDiscountCode = discountCodeRepository.save(discountCode);
            return modelMapper.map(newDiscountCode, DiscountCodeResponse.class);
        } catch (Exception e) {
            if(e.getMessage().contains(discountCode.getDiscountCodeId())){
                throw new Duplicate("duplicate start! ");
            }
            System.out.println(e.getMessage());
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
    public DiscountCodeListResponse getAllDiscountCode(int page, int size){
//        List<DiscountCode> discountCodes = discountCodeRepository.findDiscountCodesByIsDeletedFalse();
//        return discountCodes;
        Page discountCodePage = discountCodeRepository.findDiscountCodesByIsDeletedFalseOrderByDiscountProgramEndedDateAsc(PageRequest.of(page, size));
        DiscountCodeListResponse discountCodeListResponse = new DiscountCodeListResponse();
        discountCodeListResponse.setTotalPage(discountCodePage.getTotalPages());
        discountCodeListResponse.setContent(discountCodePage.getContent());
        discountCodeListResponse.setPageNumber(discountCodePage.getNumber());
        discountCodeListResponse.setTotalElement(discountCodePage.getTotalElements());
        return discountCodeListResponse;
    }

    public UpdateDiscountCodeResponse updatedDiscountCode(RequestUpdateDiscountCode requestUpdateDiscountCode, String id) {
        DiscountCode discountCode = modelMapper.map(requestUpdateDiscountCode, DiscountCode.class);
//        List<DiscountProgram> discountPrograms = discountProgramRepository.findDiscountProgramByName(name);
        DiscountCode oldDiscountCode = discountCodeRepository.findDiscountCodeByDiscountCodeId(id);
        if (oldDiscountCode == null) {
            throw new Duplicate("Discount program not found!");// cho dung luon
        } else {
            try{
//                if (oldDiscountCode.getCode() != null && !oldDiscountCode.getCode().isEmpty()) {
//                    oldDiscountCode.setCode(oldDiscountCode.getCode());
//                }

                // Lưu cập nhật vào cơ sở dữ liệu
                DiscountCode updatedDiscountCode = discountCodeRepository.save(oldDiscountCode);
                return modelMapper.map(updatedDiscountCode, UpdateDiscountCodeResponse.class);
            } catch (Exception e) {
                throw new UpdatedException("Discount Program can not update!");
            }
        }
    }

    //GET PROFILE DiscountCode
    public DiscountCodeInfResponse getInfoDiscountCode(String id){
        DiscountCode discountCode = discountCodeRepository.findDiscountCodeByDiscountCodeId(id);
        return modelMapper.map(discountCode, DiscountCodeInfResponse.class);
    }
}
