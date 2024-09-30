package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.AccountForCustomer;
import com.hairsalonbookingapp.hairsalon.entity.AccountForEmployee;
import com.hairsalonbookingapp.hairsalon.entity.Feedback;
import com.hairsalonbookingapp.hairsalon.entity.SalaryMonth;
import com.hairsalonbookingapp.hairsalon.exception.Duplicate;
import com.hairsalonbookingapp.hairsalon.model.FeedbackResponse;
import com.hairsalonbookingapp.hairsalon.model.RequestFeedback;
import com.hairsalonbookingapp.hairsalon.model.RequestSalaryMonth;
import com.hairsalonbookingapp.hairsalon.model.SalaryMonthResponse;
import com.hairsalonbookingapp.hairsalon.repository.EmployeeRepository;
import com.hairsalonbookingapp.hairsalon.repository.FeedbackRepository;
import com.hairsalonbookingapp.hairsalon.repository.SalaryMonthRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaryMonthService {
    @Autowired
    SalaryMonthRepository salaryMonthRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ModelMapper modelMapper;
    // create feedback
    public SalaryMonthResponse createSalaryMonth(RequestSalaryMonth requestSalaryMonth, String employeeId){
        SalaryMonth salaryMonth = modelMapper.map(requestSalaryMonth, SalaryMonth.class);
        try{
//            String newId = generateId();
//            feedback.setFeedbackId(newId);
            AccountForEmployee accountForEmployee = employeeRepository.findAccountForEmployeeByEmployeeId(employeeId);
            if(accountForEmployee == null){
                throw new Duplicate("No current employee found.");
            }
            salaryMonth.setEmployee(accountForEmployee);
            SalaryMonth newSalaryMonth = salaryMonthRepository.save(salaryMonth);
            return modelMapper.map(newSalaryMonth, SalaryMonthResponse.class);
        } catch (Exception e) {
            if(e.getMessage().contains(salaryMonth.getEmployee() + "")){
                throw new Duplicate("duplicate employee! ");
            }
        }
        return null;
    }

//    public String generateId() {
//        // Tìm ID cuối cùng theo vai trò
//        Optional<Feedback> lastFeedback = feedbackRepository.findTopByOrderByFeedbackIdDesc();
//        int newIdNumber = 1; // Mặc định bắt đầu từ 1
//
//        // Nếu có tài khoản cuối cùng, lấy ID
//        if (lastFeedback.isPresent()) {
//            String lastId = lastFeedback.get().getFeedbackId();
//            newIdNumber = Integer.parseInt(lastId.replaceAll("\\D+", "")) + 1; // Tăng số lên 1
//        }
//
//
//        String prefix = "FB";
//
//        return String.format("%s%06d", prefix, newIdNumber); // Tạo ID mới với format
//    }


    //delete feedback
    public SalaryMonthResponse deleteSalaryMonth(int salaryMonthId){
        // tim toi id ma FE cung cap
        SalaryMonth salaryMonthNeedDelete = salaryMonthRepository.findSalaryMonthBySalaryMonthId(salaryMonthId);
        if(salaryMonthNeedDelete == null){
            throw new Duplicate("SalaryMonth not found!"); // dung tai day
        }

        salaryMonthNeedDelete.setDeleted(true);
        SalaryMonth deletedSalaryMonth = salaryMonthRepository.save(salaryMonthNeedDelete);
        return modelMapper.map(deletedSalaryMonth, SalaryMonthResponse.class);
    }

    // show list of feedback
    public List<SalaryMonth> getAllSalaryMonth(){
        List<SalaryMonth> salaryMonths = salaryMonthRepository.findSalaryMonthsByIsDeletedFalse();
        return salaryMonths;
    }
}
