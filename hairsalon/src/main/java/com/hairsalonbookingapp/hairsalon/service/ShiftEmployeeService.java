package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.AccountForEmployee;
import com.hairsalonbookingapp.hairsalon.entity.ShiftEmployee;
import com.hairsalonbookingapp.hairsalon.entity.ShiftInWeek;
import com.hairsalonbookingapp.hairsalon.exception.DuplicateEntity;
import com.hairsalonbookingapp.hairsalon.exception.EntityNotFoundException;
import com.hairsalonbookingapp.hairsalon.model.ShiftEmployeeResponse;
import com.hairsalonbookingapp.hairsalon.repository.EmployeeRepository;
import com.hairsalonbookingapp.hairsalon.repository.ShiftEmployeeRepository;
import com.hairsalonbookingapp.hairsalon.repository.ShiftWeekRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShiftEmployeeService {

    @Autowired
    ShiftEmployeeRepository shiftEmployeeRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ShiftWeekRepository shiftWeekRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    AuthenticationService authenticationService;

    //tạo shift employee -> STYLIST LÀM
    public ShiftEmployeeResponse createNewShiftEmployee(String dayOfWeek){
        ShiftEmployee shift = new ShiftEmployee();
        shift.setStatus(true);
        shift.setAccountForEmployee(authenticationService.getCurrentAccountForEmployee());
        shift.setName(authenticationService.getCurrentAccountForEmployee().getName());
        shift.setShiftInWeek(shiftWeekRepository.findShiftInWeekByDayOfWeekAndStatusTrue(dayOfWeek));
        ShiftEmployee newShift = shiftEmployeeRepository.save(shift);

        // GENERATE RESPONSE
        ShiftEmployeeResponse shiftEmployeeResponse = modelMapper.map(newShift, ShiftEmployeeResponse.class);
        shiftEmployeeResponse.setDayInWeek(newShift.getShiftInWeek().getDayOfWeek());
        shiftEmployeeResponse.setEmployeeId(newShift.getAccountForEmployee().getId());
        shiftEmployeeResponse.setName(newShift.getName());

        return shiftEmployeeResponse;
    }

    //xóa shift -> STYLIST LÀM
    public ShiftEmployeeResponse deleteShiftEmployee(long idShift){
        ShiftEmployee shift = shiftEmployeeRepository.findShiftEmployeeById(idShift);
        if(shift != null){
            shift.setStatus(false);
            ShiftEmployee shiftEmployee = shiftEmployeeRepository.save(shift);

            // GENERATE RESPONSE
            ShiftEmployeeResponse shiftEmployeeResponse = modelMapper.map(shiftEmployee, ShiftEmployeeResponse.class);
            shiftEmployeeResponse.setDayInWeek(shiftEmployee.getShiftInWeek().getDayOfWeek());
            shiftEmployeeResponse.setEmployeeId(shiftEmployee.getAccountForEmployee().getId());
            shiftEmployeeResponse.setName(shiftEmployee.getAccountForEmployee().getName());

            return shiftEmployeeResponse;
        } else {
            throw new EntityNotFoundException("Shift not found!");
        }
    }

    //get shift -> STYLIST LÀM
    public List<ShiftEmployeeResponse> getEmployeeShift(){
        List<ShiftEmployee> list = shiftEmployeeRepository.findShiftEmployeesByAccountForEmployee_IdAndStatusTrue(authenticationService.getCurrentAccountForEmployee().getId());
        List<ShiftEmployeeResponse> shiftEmployeeResponses = new ArrayList<>();
        for(ShiftEmployee shiftEmployee : list){

            // GENERATE RESPONSE
            ShiftEmployeeResponse shiftEmployeeResponse = modelMapper.map(shiftEmployee, ShiftEmployeeResponse.class);
            shiftEmployeeResponse.setDayInWeek(shiftEmployee.getShiftInWeek().getDayOfWeek());
            shiftEmployeeResponse.setEmployeeId(shiftEmployee.getAccountForEmployee().getId());
            shiftEmployeeResponse.setName(shiftEmployee.getAccountForEmployee().getName());

            shiftEmployeeResponses.add(shiftEmployeeResponse);
        }

        return shiftEmployeeResponses;
    }

    // get shift -> CUSTOMER LÀM -> DÙNG BÊN APPOINTMENT SERVICE
    public List<ShiftEmployee> getShiftsOfEmployee(String stylistId){
        List<ShiftEmployee> shiftEmployeeList = shiftEmployeeRepository.findShiftEmployeesByAccountForEmployee_IdAndStatusTrue(stylistId);
        if(shiftEmployeeList != null){
            return shiftEmployeeList;
        } else {
            throw new EntityNotFoundException("Shift not found!");
        }
    }


    // XÁC NHẬN HOÀN THÀNH TOÀN BỘ SHIFT CỦA MỌI STYLIST TRONG NGÀY -> MANAGER LÀM MỖI NGÀY
    /*public void completeAllShiftEmployeeInDay(String day){
        shiftEmployeeRepository.
    }*/

}
