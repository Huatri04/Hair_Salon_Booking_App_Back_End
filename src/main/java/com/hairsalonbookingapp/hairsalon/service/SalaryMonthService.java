package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.*;
import com.hairsalonbookingapp.hairsalon.exception.AccountNotFoundException;
import com.hairsalonbookingapp.hairsalon.exception.Duplicate;
import com.hairsalonbookingapp.hairsalon.model.*;
import com.hairsalonbookingapp.hairsalon.repository.EmployeeRepository;
import com.hairsalonbookingapp.hairsalon.repository.FeedbackRepository;
import com.hairsalonbookingapp.hairsalon.repository.SalaryMonthRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
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
            // Tìm nhân viên theo ID
            AccountForEmployee employee = employeeRepository.findAccountForEmployeeByEmployeeId(employeeId);
            if (employee == null) {
                System.out.println("employee empty");
                throw new Duplicate("Employee not found");
            }
            salaryMonth.setEmployee(employee);

            Month currentMonth = LocalDate.now().getMonth();
            salaryMonth.setMonth(currentMonth);

            double commessionOverratedFromKPI = 1;
            if(employee.getCommessionOverratedFromKPI() != null){
                commessionOverratedFromKPI = employee.getCommessionOverratedFromKPI();
            }

            double fineUnderatedFromKPI = 1;
            if(employee.getFineUnderatedFromKPI() != null){
                fineUnderatedFromKPI = employee.getFineUnderatedFromKPI();
            }

            salaryMonth.setCommessionOveratedFromKPI(employee.getKPI() * commessionOverratedFromKPI);
            salaryMonth.setFineUnderatedFromKPI(employee.getKPI() * fineUnderatedFromKPI);
            salaryMonth.setSumSalary(employee.getBasicSalary() + salaryMonth.getCommessionOveratedFromKPI() - salaryMonth.getFineUnderatedFromKPI());


            SalaryMonth newSalaryMonth = salaryMonthRepository.save(salaryMonth);
            return modelMapper.map(newSalaryMonth, SalaryMonthResponse.class);
        } catch (Exception e) {
//            if(e.getMessage().contains(salaryMonth.get)){
//                throw new Duplicate("duplicate employee! ");
//            }
            System.out.println(e.getMessage());
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

    // show list of SalaryMonth
    public List<SalaryMonth> getAllSalaryMonth(){
        List<SalaryMonth> salaryMonths = salaryMonthRepository.findSalaryMonthsByIsDeletedFalse();
        return salaryMonths;
    }

    // show list of SalaryMonth chi acc do thay
    public List<SalaryMonth> getAllSalaryMonthOfAnEmployee(String employeeId){
        List<SalaryMonth> salaryMonths = salaryMonthRepository.findSalaryMonthsByEmployee_EmployeeIdAndIsDeletedFalse(employeeId);
        return salaryMonths;
    }

    //GET PROFILE SalaryMonth
    public SalaryMonthResponse getInfoSalaryMonth(int id){
        SalaryMonth salaryMonth = salaryMonthRepository.findSalaryMonthBySalaryMonthId(id);
        return modelMapper.map(salaryMonth, SalaryMonthResponse.class);
    }
}
