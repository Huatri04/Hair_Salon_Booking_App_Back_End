package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.AccountForEmployee;
import com.hairsalonbookingapp.hairsalon.entity.ShiftEmployee;
import com.hairsalonbookingapp.hairsalon.entity.ShiftInWeek;
import com.hairsalonbookingapp.hairsalon.exception.AccountNotFoundException;
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

    private final int MAXSHIFT = 15;        // SỐ SHIFT STYLIST CẦN LÀM ĐỂ NHẬN LƯƠNG

    //tạo shift employee -> STYLIST LÀM
    public ShiftEmployeeResponse createNewShiftEmployee(String dayOfWeek){
        ShiftEmployee shift = new ShiftEmployee();

        shift.setAccountForEmployee(authenticationService.getCurrentAccountForEmployee());
        shift.setName(authenticationService.getCurrentAccountForEmployee().getName());
        shift.setShiftInWeek(shiftWeekRepository.findShiftInWeekByDayOfWeekAndIsAvailableTrue(dayOfWeek));
        ShiftEmployee newShift = shiftEmployeeRepository.save(shift);

        // GENERATE RESPONSE
        ShiftEmployeeResponse shiftEmployeeResponse = modelMapper.map(newShift, ShiftEmployeeResponse.class);
        shiftEmployeeResponse.setDayInWeek(newShift.getShiftInWeek().getDayOfWeek());
        shiftEmployeeResponse.setEmployeeId(newShift.getAccountForEmployee().getId());
        shiftEmployeeResponse.setName(newShift.getName());

        return shiftEmployeeResponse;
    }

    //xóa shift -> STYLIST LÀM KHI NÓ MUỐN HỦY SHIFT, NHƯNG PHẢI THÔNG BÁO CHO MANAGER TRƯỚC
    public ShiftEmployeeResponse deleteShiftEmployee(long idShift){
        ShiftEmployee shift = shiftEmployeeRepository.findShiftEmployeeById(idShift);
        if(shift != null){
            shift.setAvailable(false);
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

    // RESTART SHIFT -> STYLIST LÀM
    public ShiftEmployeeResponse restartShiftEmployee(long idShift){
        ShiftEmployee shift = shiftEmployeeRepository.findShiftEmployeeById(idShift);
        if(shift != null){
            shift.setAvailable(true);
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
        List<ShiftEmployee> list = shiftEmployeeRepository.findShiftEmployeesByAccountForEmployee_Id(authenticationService.getCurrentAccountForEmployee().getId());
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
        List<ShiftEmployee> shiftEmployeeList = shiftEmployeeRepository.findShiftEmployeesByAccountForEmployee_IdAndIsAvailableTrue(stylistId);
        if(shiftEmployeeList != null){
            return shiftEmployeeList;
        } else {
            throw new EntityNotFoundException("Shift not found!");
        }
    }


    // HÀM LẤY SỐ SLOT STYLIST LÀM ĐỂ SO SÁNH KPI -> HỖ TRỢ HÀM DƯỚI
    public String compareToKPI(String stylistID){
        AccountForEmployee account = employeeRepository.findAccountForEmployeeByIdAndStatusAndIsDeletedFalse(stylistID, "Workday");
        if(account != null){
            int completedSlot = account.getCompletedSlot();
            int KPI = account.getKPI();
            String message = "STYLIST = " + account.getName() + ", ID = " + account.getId() + ", KPI = " + KPI + ", SLOT COMPLETE = " + completedSlot;
            return message;
        } else {
            throw new AccountNotFoundException("Stylist not found!");
        }
    }


    // XÁC NHẬN HOÀN THÀNH TOÀN BỘ SHIFT CỦA MỌI STYLIST TRONG NGÀY -> MANAGER LÀM MỖI NGÀY
    public List<String> completeAllShiftEmployeeInDay(String day){
        List<ShiftEmployee> shiftEmployeeList = shiftEmployeeRepository.findShiftEmployeesByShiftInWeek_DayOfWeekAndIsAvailableTrue(day);
        List<String> StylistGetSalary = new ArrayList<>();  // DANH SÁCH STYLIST NHẬN LƯƠNG
        String notification = "Complete all shift in " + day;
        if(shiftEmployeeList != null){
            StylistGetSalary.add(notification);
            for(ShiftEmployee shiftEmployee : shiftEmployeeList){
                shiftEmployee.setCompleted(true);
                ShiftEmployee newShift = shiftEmployeeRepository.save(shiftEmployee);
                AccountForEmployee account = newShift.getAccountForEmployee();

                account.setCompletedShift(account.getCompletedShift() + 1);

                if(account.getCompletedShift() == MAXSHIFT){        // CÓ NGƯỜI ĐẠT ĐỦ 15 CA
                    String message = compareToKPI(account.getId());
                    StylistGetSalary.add(message);
                    account.setCompletedShift(0);   // RESET VỀ 0
                    account.setCompletedSlot(0);    // RESET VỀ 0
                }

                AccountForEmployee newAccount = employeeRepository.save(account);
            }

            return StylistGetSalary;

        } else {
            throw new EntityNotFoundException("Shift not found!");
        }
    }

    //HÀM NÀY LẤY RA TOÀN BỘ DANH SÁCH CA LÀM VIỆC CỦA STYLIST, TOÀN BỘ CA TRONG TUẦN MÀ KHÔNG QUAN TÂM HIỆN TẠI LÀ THỨ MẤY
    //KHÁCH HÀNG TỰ HIỂU QUY TẮC LÀ KHÔNG ĐƯỢC CHỌN TRONG NGÀY VÀ TRƯỚC NGÀY
    public List<ShiftEmployeeResponse> getAvailableShiftEmployees(String stylistId) {     //CUSTOMER TÌM CÁC CA LÀM VIỆC KHẢ DỤNG CỦA STYLIST
        List<ShiftEmployee> shiftEmployeeList = getShiftsOfEmployee(stylistId);
        List<ShiftEmployeeResponse> shiftEmployeeResponseList = new ArrayList<>();
        for(ShiftEmployee shiftEmployee : shiftEmployeeList){
            ShiftEmployeeResponse shiftEmployeeResponse = modelMapper.map(shiftEmployee, ShiftEmployeeResponse.class);
            shiftEmployeeResponse.setEmployeeId(stylistId);
            shiftEmployeeResponse.setDayInWeek(shiftEmployee.getShiftInWeek().getDayOfWeek());
            shiftEmployeeResponseList.add(shiftEmployeeResponse);
        }
        return shiftEmployeeResponseList;
    }

}
