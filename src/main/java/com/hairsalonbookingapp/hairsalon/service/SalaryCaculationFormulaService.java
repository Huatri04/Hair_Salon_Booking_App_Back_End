package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.DiscountCode;
import com.hairsalonbookingapp.hairsalon.entity.DiscountProgram;
import com.hairsalonbookingapp.hairsalon.entity.SalaryCaculationFormula;
import com.hairsalonbookingapp.hairsalon.exception.CreateException;
import com.hairsalonbookingapp.hairsalon.exception.Duplicate;
import com.hairsalonbookingapp.hairsalon.exception.UpdatedException;
import com.hairsalonbookingapp.hairsalon.model.*;
import com.hairsalonbookingapp.hairsalon.repository.DiscountProgramRepository;
import com.hairsalonbookingapp.hairsalon.repository.SalaryCaculatorFormulaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SalaryCaculationFormulaService {

    @Autowired
    SalaryCaculatorFormulaRepository salaryCaculatorFormulaRepository;

    @Autowired
    ModelMapper modelMapper;


    // create SalaryCaculationFormula
    public SalaryCaculationFormulaResponse createSalaryCaculationFormula(RequestSalaryCaculatorFormula requestSalaryCaculatorFormula){
        SalaryCaculationFormula salaryCaculationFormula = modelMapper.map(requestSalaryCaculatorFormula, SalaryCaculationFormula.class);
        try{
//            String newId = generateId();
//            discountProgram.setDiscountProgramId(newId);
            SalaryCaculationFormula newSalaryCaculationFormula = salaryCaculatorFormulaRepository.save(salaryCaculationFormula);
            return modelMapper.map(newSalaryCaculationFormula, SalaryCaculationFormulaResponse.class);
        } catch (Exception e) {
            if(e.getMessage().contains(salaryCaculationFormula.getBasicSalary() + "")){
                throw new CreateException("error basic salary!");
            } else if (e.getMessage().contains(salaryCaculationFormula.getCommessionOveratedBasedService() + "")) {
                throw new CreateException("error Commession Overated Based Service!");
            } else if (e.getMessage().contains(salaryCaculationFormula.getFineUnderatedBasedService() + "")) {
                throw new CreateException("error Fine Underated Based Service!");
            }
        }
        return null;
    }

//    public String generateId() {
//        // Tìm ID cuối cùng theo vai trò
//        Optional<DiscountProgram> lasDiscountProgram = discountProgramRepository.findTopByOrderByDiscountProgramIdDesc();
//        int newIdNumber = 1; // Mặc định bắt đầu từ 1
//
//        // Nếu có tài khoản cuối cùng, lấy ID
//        if (lasDiscountProgram.isPresent()) {
//            String lastId = lasDiscountProgram.get().getDiscountProgramId();
//            newIdNumber = Integer.parseInt(lastId.replaceAll("\\D+", "")) + 1; // Tăng số lên 1
//        }
//
//
//        String prefix = "DP";
//
//        return String.format("%s%06d", prefix, newIdNumber); // Tạo ID mới với format
//    }


    //delete SalaryCaculationFormula
    public SalaryCaculationFormulaResponse deleteSalaryCaculationFormula(int salaryCaculationFormulaId){
        // tim toi id ma FE cung cap
        SalaryCaculationFormula salaryCaculationFormulaNeedDelete = salaryCaculatorFormulaRepository.findSalaryCaculationFormulaBySalaryCaculationFormulaId(salaryCaculationFormulaId);
        if(salaryCaculationFormulaNeedDelete == null){
            throw new Duplicate("Salary Caculation Formula not found!"); // dung tai day
        }

        salaryCaculationFormulaNeedDelete.setDeleted(true);
        SalaryCaculationFormula deletedSalaryCaculationFormula = salaryCaculatorFormulaRepository.save(salaryCaculationFormulaNeedDelete);
        return modelMapper.map(deletedSalaryCaculationFormula, SalaryCaculationFormulaResponse.class);
    }

    // show list of SalaryCaculationFormula
    public List<SalaryCaculationFormula> getAllSalaryCaculationFormula(){
        List<SalaryCaculationFormula> salaryCaculationFormulas = salaryCaculatorFormulaRepository.findSalaryCaculationFormulasByIsDeletedFalse();
        return salaryCaculationFormulas;
    }

    //    public DiscountProgram getCurrentDiscountProgram(){
//        DiscountProgram discountProgram = (DiscountProgram) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        return discountProgramRepository.findDiscountProgramByDiscountProgramId(discountProgram.getDiscountProgramId());
//    }

    public UpdateSalaryCaculatorFormulaResponse updatedSalaryCaculationFormula(RequestUpdateSalaryCaculationFormula requestUpdateSalaryCaculationFormula, int id) {
        SalaryCaculationFormula salaryCaculationFormula = modelMapper.map(requestUpdateSalaryCaculationFormula, SalaryCaculationFormula.class);
//        List<DiscountProgram> discountPrograms = discountProgramRepository.findDiscountProgramByName(name);
        SalaryCaculationFormula oldSalaryCaculationFormula = salaryCaculatorFormulaRepository.findSalaryCaculationFormulaBySalaryCaculationFormulaId(id);
        if (oldSalaryCaculationFormula == null) {
            throw new Duplicate("Salary Caculation Formula not found!");// cho dung luon
        } else {
            try{
//                if (oldDiscountCode.getCode() != null && !oldDiscountCode.getCode().isEmpty()) {
//                    oldDiscountCode.setCode(oldDiscountCode.getCode());
//                }
                if (salaryCaculationFormula.getBasicSalary() != null) {
                    oldSalaryCaculationFormula.setBasicSalary(salaryCaculationFormula.getBasicSalary());
                }

                if (salaryCaculationFormula.getCommessionOveratedBasedService() != null) {
                    oldSalaryCaculationFormula.setCommessionOveratedBasedService(salaryCaculationFormula.getCommessionOveratedBasedService());
                }

                if (salaryCaculationFormula.getFineUnderatedBasedService() != null) {
                    oldSalaryCaculationFormula.setFineUnderatedBasedService(salaryCaculationFormula.getFineUnderatedBasedService());
                }

                // Lưu cập nhật vào cơ sở dữ liệu
                SalaryCaculationFormula updatedSalaryCaculationFormula = salaryCaculatorFormulaRepository.save(oldSalaryCaculationFormula);
                return modelMapper.map(updatedSalaryCaculationFormula, UpdateSalaryCaculatorFormulaResponse.class);
            } catch (Exception e) {
                if(e.getMessage().contains(salaryCaculationFormula.getBasicSalary() + "")){
                    throw new CreateException("error basic salary!");
                } else if (e.getMessage().contains(salaryCaculationFormula.getCommessionOveratedBasedService() + "")) {
                    throw new CreateException("error Commession Overated Based Service!");
                } else if (e.getMessage().contains(salaryCaculationFormula.getFineUnderatedBasedService() + "")) {
                    throw new CreateException("error Fine Underated Based Service!");
                }
            }
            return null;
        }
    }

            //GET PROFILE SalaryCaculationFormula
    public SalaryCaculationFormulaInfoResponse getInfoSalaryCaculationFormula(int id){
        SalaryCaculationFormula salaryCaculationFormula = salaryCaculatorFormulaRepository.findSalaryCaculationFormulaBySalaryCaculationFormulaId(id);
        return modelMapper.map(salaryCaculationFormula, SalaryCaculationFormulaInfoResponse.class);
    }
}
