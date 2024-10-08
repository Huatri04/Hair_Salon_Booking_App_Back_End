package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.*;
import com.hairsalonbookingapp.hairsalon.exception.AccountNotFoundException;
import com.hairsalonbookingapp.hairsalon.exception.DuplicateEntity;
import com.hairsalonbookingapp.hairsalon.exception.EntityNotFoundException;
import com.hairsalonbookingapp.hairsalon.model.ShiftEmployeeResponse;
import com.hairsalonbookingapp.hairsalon.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShiftEmployeeService {

    @Autowired
    ShiftEmployeeRepository shiftEmployeeRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    SlotRepository slotRepository;

    @Autowired
    ShiftWeekRepository shiftWeekRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    AppointmentRepository appointmentRepository;

    private final int MAXSHIFT = 2;        // SỐ SHIFT STYLIST CẦN LÀM ĐỂ NHẬN LƯƠNG

    //tạo shift employee -> STYLIST LÀM
    public ShiftEmployeeResponse createNewShiftEmployee(String dayOfWeek){
        ShiftEmployee shift = new ShiftEmployee();
        ShiftInWeek shiftInWeek = shiftWeekRepository.findShiftInWeekByDayOfWeekAndIsAvailableTrue(dayOfWeek);
        if(shiftInWeek != null){   // CHECK COI ĐẦU VÀO NHẬP ĐÚNG KHÔNG
            shift.setShiftInWeek(shiftInWeek);
        } else {
            throw new EntityNotFoundException("Invalid day!");
        }
        shift.setAccountForEmployee(authenticationService.getCurrentAccountForEmployee());
        shift.setName(authenticationService.getCurrentAccountForEmployee().getName());
        String name = authenticationService.getCurrentAccountForEmployee().getName();
        // CHECK COI SHIFT ĐÃ TỒN TẠI CHƯA
        ShiftEmployee checkExistedShift = shiftEmployeeRepository.findShiftEmployeeByShiftInWeek_DayOfWeekAndNameAndIsAvailableTrue(dayOfWeek, name);
        if(checkExistedShift != null){
            throw new DuplicateEntity("Shift has existed!");
        }
        ShiftEmployee newShift = shiftEmployeeRepository.save(shift);

        // GENERATE RESPONSE
        //ShiftEmployeeResponse shiftEmployeeResponse = modelMapper.map(newShift, ShiftEmployeeResponse.class);
        ShiftEmployeeResponse shiftEmployeeResponse = new ShiftEmployeeResponse();
        shiftEmployeeResponse.setCompleted(false);
        shiftEmployeeResponse.setAvailable(true);
        shiftEmployeeResponse.setId(newShift.getId());
        shiftEmployeeResponse.setDayInWeek(newShift.getShiftInWeek().getDayOfWeek());
        shiftEmployeeResponse.setEmployeeId(newShift.getAccountForEmployee().getId());
        shiftEmployeeResponse.setName(newShift.getName());

        return shiftEmployeeResponse;
    }

    //xóa shift -> STYLIST LÀM KHI NÓ MUỐN HỦY SHIFT, NHƯNG PHẢI THÔNG BÁO CHO MANAGER TRƯỚC
    // APP TỰ HỦY CÁC SLOT CỦA STYLIST
    //NẾU CÓ LỊCH HẸN TRONG SLOT ĐÓ -> STAFF SET CÁI LỊCH ĐÓ LÀ HỦY -> STAFF THÔNG BÁO ĐẾN KHÁCH
    public List<String> deleteShiftEmployee(String day){
        String name = authenticationService.getCurrentAccountForEmployee().getName();
        ShiftEmployee shift = shiftEmployeeRepository.findShiftEmployeeByShiftInWeek_DayOfWeekAndNameAndIsAvailableTrue(day, name);
        if(shift != null){
            List<String> appointment = new ArrayList<>(); //LIST CÁC APPOINTMENT TÌM THẤY KHI XÓA
            appointment.add("DELETE COMPLETE");

            shift.setAvailable(false);
            ShiftEmployee shiftEmployee = shiftEmployeeRepository.save(shift);

            // STYLIST XÓA SHIFT -> APP TỰ ĐỘNG XÓA SLOT
            List<Slot> slotList = slotRepository.findSlotsByShiftEmployee_Id(shiftEmployee.getId());
            for(Slot slot : slotList){
                slot.setAvailable(false);
                Slot savedSlot = slotRepository.save(slot);

                Appointment checkAppointment = appointmentRepository.findAppointmentBySlot_Id(savedSlot.getId());
                if(checkAppointment != null){
                    String appointmentID = String.valueOf(checkAppointment.getId());
                    String message = "FOUND " + appointmentID;
                    appointment.add(message);
                }
            }

            if (appointment.size() == 1){
                appointment.add("NO APPOINTMENT FOUND");
            }
            // GENERATE RESPONSE
            /*ShiftEmployeeResponse shiftEmployeeResponse = modelMapper.map(shiftEmployee, ShiftEmployeeResponse.class);
            shiftEmployeeResponse.setDayInWeek(shiftEmployee.getShiftInWeek().getDayOfWeek());
            shiftEmployeeResponse.setEmployeeId(shiftEmployee.getAccountForEmployee().getId());
            shiftEmployeeResponse.setName(shiftEmployee.getAccountForEmployee().getName());*/

            return appointment;  // TRẢ VỀ THÔNG BÁO XÓA THÀNH CÔNG VÀ DANH SÁCH CÁC APPOINTMENT (NẾU CÓ)
        } else {
            throw new EntityNotFoundException("Shift not found!");
        }
    }

    // RESTART SHIFT -> STYLIST LÀM
    public ShiftEmployeeResponse restartShiftEmployee(String day){
        String name = authenticationService.getCurrentAccountForEmployee().getName();
        ShiftEmployee shift = shiftEmployeeRepository.findShiftEmployeeByShiftInWeek_DayOfWeekAndName(day, name);
        if(shift != null){
            shift.setAvailable(true);
            ShiftEmployee shiftEmployee = shiftEmployeeRepository.save(shift);

            List<Slot> slotList = slotRepository.findSlotsByShiftEmployee_Id(shiftEmployee.getId());
            for(Slot slot : slotList){
                slot.setAvailable(true);
                Slot savedSlot = slotRepository.save(slot);
            }

            // GENERATE RESPONSE
            //ShiftEmployeeResponse shiftEmployeeResponse = modelMapper.map(shiftEmployee, ShiftEmployeeResponse.class);
            ShiftEmployeeResponse shiftEmployeeResponse = new ShiftEmployeeResponse();
            shiftEmployeeResponse.setCompleted(false);
            shiftEmployeeResponse.setAvailable(true);
            shiftEmployeeResponse.setId(shiftEmployee.getId());
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
            ShiftEmployeeResponse shiftEmployeeResponse = new ShiftEmployeeResponse();
            shiftEmployeeResponse.setCompleted(shiftEmployee.isCompleted());
            shiftEmployeeResponse.setAvailable(shiftEmployee.isAvailable());
            shiftEmployeeResponse.setId(shiftEmployee.getId());
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
            ShiftEmployeeResponse shiftEmployeeResponse = new ShiftEmployeeResponse();
            shiftEmployeeResponse.setCompleted(shiftEmployee.isCompleted());
            shiftEmployeeResponse.setAvailable(shiftEmployee.isAvailable());
            shiftEmployeeResponse.setId(shiftEmployee.getId());
            shiftEmployeeResponse.setDayInWeek(shiftEmployee.getShiftInWeek().getDayOfWeek());
            shiftEmployeeResponse.setEmployeeId(shiftEmployee.getAccountForEmployee().getId());
            shiftEmployeeResponse.setName(shiftEmployee.getAccountForEmployee().getName());

            shiftEmployeeResponseList.add(shiftEmployeeResponse);
        }
        return shiftEmployeeResponseList;
    }

}
