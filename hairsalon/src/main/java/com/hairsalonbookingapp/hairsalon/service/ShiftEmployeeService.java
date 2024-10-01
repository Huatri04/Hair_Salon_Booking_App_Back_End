package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.AccountForEmployee;
import com.hairsalonbookingapp.hairsalon.entity.ShiftEmployee;
import com.hairsalonbookingapp.hairsalon.entity.ShiftInWeek;
import com.hairsalonbookingapp.hairsalon.exception.DuplicateEntity;
import com.hairsalonbookingapp.hairsalon.exception.EntityNotFoundException;
import com.hairsalonbookingapp.hairsalon.repository.EmployeeRepository;
import com.hairsalonbookingapp.hairsalon.repository.ShiftEmployeeRepository;
import com.hairsalonbookingapp.hairsalon.repository.ShiftWeekRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public ShiftEmployee createNewShiftEmployee(String dayOfWeek){
        ShiftEmployee shift = new ShiftEmployee();
        shift.setStatus(true);
        shift.setAccountForEmployee(authenticationService.getCurrentAccountForEmployee());
        shift.setShiftInWeek(shiftWeekRepository.findShiftInWeekByDayOfWeekAndStatusTrue(dayOfWeek));
        ShiftEmployee newShift = shiftEmployeeRepository.save(shift);
        return newShift;
    }

    //xóa shift -> STYLIST LÀM
    public ShiftEmployee deleteShiftEmployee(long idShift){
        ShiftEmployee shift = shiftEmployeeRepository.findShiftEmployeeById(idShift);
        if(shift != null){
            shift.setStatus(false);
            ShiftEmployee shiftEmployee = shiftEmployeeRepository.save(shift);
            return shiftEmployee;
        } else {
            throw new EntityNotFoundException("Shift not found!");
        }
    }

    //get shift -> STYLIST LÀM
    public List<ShiftEmployee> getEmployeeShift(){
        List<ShiftEmployee> list = shiftEmployeeRepository.findShiftEmployeesByEmployeeId(authenticationService.getCurrentAccountForEmployee().getId());
        return list;
    }

}
