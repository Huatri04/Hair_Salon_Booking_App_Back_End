package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.AccountForCustomer;
import com.hairsalonbookingapp.hairsalon.entity.DiscountCode;
import com.hairsalonbookingapp.hairsalon.entity.DiscountProgram;
import com.hairsalonbookingapp.hairsalon.exception.DuplicateEntity;
import com.hairsalonbookingapp.hairsalon.exception.EntityNotFoundException;
import com.hairsalonbookingapp.hairsalon.model.DiscountCodeResponse;
import com.hairsalonbookingapp.hairsalon.model.DiscountProgramRequest;
import com.hairsalonbookingapp.hairsalon.model.DiscountProgramResponse;
import com.hairsalonbookingapp.hairsalon.model.DiscountProgramUpdate;
import com.hairsalonbookingapp.hairsalon.repository.DiscountCodeRepository;
import com.hairsalonbookingapp.hairsalon.repository.DiscountProgramRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Service
public class DiscountService {

    @Autowired
    DiscountProgramRepository discountProgramRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    DiscountCodeRepository discountCodeRepository;

    @Autowired
    ModelMapper modelMapper;

    //HÀM GENERATE DISCOUNT CODE NGẪU NHIÊN GỒM 5 KÝ TỰ BAO GỒM CÁC SỐ , KÝ TỰ THƯỜNG, HOA VÀ ĐẶC BIỆT
    private final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                            + "abcdefghijklmnopqrstuvwxyz"
                                            + "0123456789"
                                            + "!@#$%^&*()-_+=<>?";

    private final int CODE_LENGTH = 5;

    public String generateRandomCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(randomIndex));
        }

        return code.toString();
    }

    //----------------------------------------------------------------------------------------

    //tạo mới discount program -> MANAGER LÀM
    public DiscountProgramResponse createNewProgram(DiscountProgramRequest discountProgramRequest){
        try{
            DiscountProgram newProgram = modelMapper.map(discountProgramRequest, DiscountProgram.class);
            DiscountProgram savedProgram = discountProgramRepository.save(newProgram);
            return modelMapper.map(savedProgram, DiscountProgramResponse.class);
        } catch (Exception e){
            throw new DuplicateEntity("Duplicate name!");
        }
    }

    //update discount program -> MANAGER LÀM
    public DiscountProgramResponse updateProgram(DiscountProgramUpdate discountProgramUpdate, long id){
        DiscountProgram oldProgram = discountProgramRepository.findDiscountProgramById(id);
        if(oldProgram != null){
            try{
                if(!discountProgramUpdate.getName().isEmpty()){
                    oldProgram.setName(discountProgramUpdate.getName());
                }

                if(!discountProgramUpdate.getDescription().isEmpty()){
                    oldProgram.setDescription(discountProgramUpdate.getDescription());
                }

                if(!discountProgramUpdate.getStartDate().isEmpty()){
                    oldProgram.setStartDate(discountProgramUpdate.getStartDate());
                }

                if(!discountProgramUpdate.getEndDate().isEmpty()){
                    oldProgram.setEndDate(discountProgramUpdate.getEndDate());
                }

                if(discountProgramUpdate.getPercentage() > 0 && discountProgramUpdate.getPercentage() < 100){
                    oldProgram.setPercentage(discountProgramUpdate.getPercentage());
                }

                if(discountProgramUpdate.getPointRequest() > 0){
                    oldProgram.setPointRequest(discountProgramUpdate.getPointRequest());
                }

                DiscountProgram newProgram = discountProgramRepository.save(oldProgram);
                return modelMapper.map(newProgram, DiscountProgramResponse.class);
            } catch (Exception e){
                throw new DuplicateEntity("Duplicate name!");
            }
        } else {
            throw new EntityNotFoundException("Program not found!");
        }
    }

    //Start program -> MANAGER LÀM
    public DiscountProgramResponse startProgram(long id){
        DiscountProgram oldProgram = discountProgramRepository.findDiscountProgramByIdAndStatus(id, "Not Start");
        if(oldProgram != null){
            oldProgram.setStatus("In Process");
            DiscountProgram newProgram = discountProgramRepository.save(oldProgram);
            return modelMapper.map(newProgram, DiscountProgramResponse.class);
        } else {
            throw new EntityNotFoundException("Program not found!");
        }
    }

    //End program -> MANAGER LÀM
    public DiscountProgramResponse endProgram(long id){
        DiscountProgram oldProgram = discountProgramRepository.findDiscountProgramByIdAndStatus(id, "In Process");
        if(oldProgram != null){
            oldProgram.setStatus("Ended");
            DiscountProgram newProgram = discountProgramRepository.save(oldProgram);
            return modelMapper.map(newProgram, DiscountProgramResponse.class);
        } else {
            throw new EntityNotFoundException("Program not found!");
        }
    }

    //get all program -> MANAGER LÀM
    public List<DiscountProgramResponse> getAllProgram(){
        List<DiscountProgram> list = discountProgramRepository.findAll();
        List<DiscountProgramResponse> responseList = new ArrayList<>();
        for(DiscountProgram discountProgram : list){
            DiscountProgramResponse discountProgramResponse = modelMapper.map(discountProgram, DiscountProgramResponse.class);
            responseList.add(discountProgramResponse);
        }
        return responseList;
    }

    //get program by id -> cái này có vẻ không dùng
    public DiscountProgram getProgram(long id){
        DiscountProgram oldProgram = discountProgramRepository.findDiscountProgramById(id);
        if(oldProgram != null){
            return oldProgram;
        } else {
            throw new EntityNotFoundException("Program not found!");
        }
    }

    //tạo mới discount code -> MANAGER LÀM
    /*public DiscountCodeResponse createNewCode(String code, long programId){
        try{
            DiscountCode discountCode = new DiscountCode();
            discountCode.setId(code);
            discountCode.setDiscountProgram(discountProgramRepository.findDiscountProgramById(programId));
            discountCode.setAccountForCustomer(null);
            DiscountCode newDiscountCode = discountCodeRepository.save(discountCode);

            DiscountCodeResponse discountCodeResponse = modelMapper.map(newDiscountCode, DiscountCodeResponse.class);
            discountCodeResponse.setDiscountProgramId(newDiscountCode.getDiscountProgram().getId());
            discountCodeResponse.setCustomerId(newDiscountCode.getAccountForCustomer().getPhoneNumber());
            return discountCodeResponse;
        } catch (Exception e) {
            throw new DuplicateEntity("Duplicate code!");
        }
    }*/

    // CUSTOMER ĐỔI ĐIỂM LẤY CODE
    // numberOfTrade LÀ SỐ CODE MUỐN ĐỔI, VD: AN CÓ 1000 ĐIỂM, 1 CODE GIÁ 200, AN NHẬP 4 ĐỂ ĐỔI 800 LẤY 4 CODE
    public List<DiscountCodeResponse> getDiscountCodes(int numberOfTrade, long programId){
        AccountForCustomer accountForCustomer = authenticationService.getCurrentAccountForCustomer();
        DiscountProgram discountProgram = discountProgramRepository.findDiscountProgramById(programId);
        double percentage = discountProgram.getPercentage();
        long pointRequest = discountProgram.getPointRequest();

        long pointToTrade = pointRequest * numberOfTrade;
        while (pointToTrade > accountForCustomer.getPoint()) {
            pointToTrade -= pointRequest;
            numberOfTrade -= 1;
        }

        List<DiscountCode> discountCodeList = new ArrayList<>();
        for(int i = 1; i <= numberOfTrade; i++){
            DiscountCode discountCode = new DiscountCode();
            discountCode.setAccountForCustomer(accountForCustomer);
            discountCode.setDiscountCode(generateRandomCode());
            discountCode.setDiscountProgram(discountProgram);
            DiscountCode savedCode = discountCodeRepository.save(discountCode);
            discountCodeList.add(savedCode);
        }

        // GENERATE RESPONSE
        List<DiscountCodeResponse> discountCodeResponseList = new ArrayList<>();
        for(DiscountCode code : discountCodeList){
            DiscountCodeResponse discountCodeResponse = new DiscountCodeResponse();
            discountCodeResponse.setId(code.getId());
            discountCodeResponse.setCustomerName(code.getAccountForCustomer().getCustomerName());
            discountCodeResponse.setDiscountCode(code.getDiscountCode());
            discountCodeResponse.setPercentage(code.getDiscountProgram().getPercentage());
            discountCodeResponse.setProgramName(code.getDiscountProgram().getName());

            discountCodeResponseList.add(discountCodeResponse);
        }

        return discountCodeResponseList;
    }



}
