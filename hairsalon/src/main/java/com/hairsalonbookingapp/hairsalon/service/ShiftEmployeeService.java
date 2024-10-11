package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.*;
import com.hairsalonbookingapp.hairsalon.exception.AccountNotFoundException;
import com.hairsalonbookingapp.hairsalon.exception.DuplicateEntity;
import com.hairsalonbookingapp.hairsalon.exception.EntityNotFoundException;
import com.hairsalonbookingapp.hairsalon.model.*;
import com.hairsalonbookingapp.hairsalon.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.DayOfWeek;
import java.time.LocalDate;
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

    @Autowired
    TimeService timeService;

    @Autowired
    SlotService slotService;


    //ĐĂNG KÝ SHIFT EMPLOYEE (STYLIST) -> MANAGER LÀM
    public AccountResponseForEmployee registerShifts(StylistShiftRequest stylistShiftRequest) {
        // CẬP NHẬT VÀO ACCOUNT FOR EMPLOYEE
        String days = stylistShiftRequest.getDay1() + "," + stylistShiftRequest.getDay2() + "," + stylistShiftRequest.getDay3();
        String stylistID = stylistShiftRequest.getStylistID();
        AccountForEmployee accountForEmployee = employeeRepository.findAccountForEmployeeById(stylistID);

        if (accountForEmployee == null) {
            throw new AccountNotFoundException("Stylist not found!");
        }

        accountForEmployee.setDays(days);
        AccountForEmployee newAccount = employeeRepository.save(accountForEmployee);
        // GENERATE RESPONSE
        AccountResponseForEmployee accountResponseForEmployee = modelMapper.map(newAccount, AccountResponseForEmployee.class);
        return accountResponseForEmployee;
    }


        /*ShiftEmployee shiftEmployee = new ShiftEmployee();

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
        ShiftEmployeeResponse shiftEmployeeResponse = new ShiftEmployeeResponse();
        shiftEmployeeResponse.setCompleted(false);
        shiftEmployeeResponse.setAvailable(true);
        shiftEmployeeResponse.setId(newShift.getId());
        shiftEmployeeResponse.setDayInWeek(newShift.getShiftInWeek().getDayOfWeek());
        shiftEmployeeResponse.setEmployeeId(newShift.getAccountForEmployee().getId());
        shiftEmployeeResponse.setName(newShift.getName());

        return shiftEmployeeResponse;
    }*/

    // TẠO SHIFT CHO STYLIST -> DÙNG CHO HÀM DƯỚI
    public List<ShiftEmployeeResponse> generateShiftEmployee(AccountForEmployee accountForEmployee){
        String days = accountForEmployee.getDays(); // LẤY CÁC NGÀY STYLIST CHỌN
        String[] daysOfWeek = days.split(","); // LIST CÁC NGÀY STYLIST CHỌN
        List<LocalDate> nextWeekDays = timeService.getNextWeekDays(timeService.today); // LIST CÁC NGÀY TUẦN SAU
        List<ShiftEmployee> shiftEmployeeList = new ArrayList<>();
        List<ShiftEmployeeResponse> shiftEmployeeResponseList = new ArrayList<>();
        for(String day : daysOfWeek){
            // TẠO MỚI SHIFT EMPLOYEE
            ShiftEmployee shiftEmployee = new ShiftEmployee();
            ShiftInWeek shiftInWeek = shiftWeekRepository
                    .findShiftInWeekByDayOfWeekAndIsAvailableTrue(day);
            shiftEmployee.setShiftInWeek(shiftInWeek);
            shiftEmployee.setAccountForEmployee(accountForEmployee);
            // SET DAY
            DayOfWeek dayOfWeek = DayOfWeek.valueOf(day);
            for(LocalDate date : nextWeekDays){
                if(date.getDayOfWeek() == dayOfWeek){
                    shiftEmployee.setDate(date.toString());
                    break;
                }
            }
            // SAVE VÀO DB
            ShiftEmployee newShiftEmployee = shiftEmployeeRepository.save(shiftEmployee);
            // TẠO CÁC SLOT
            SlotRequest slotRequest = new SlotRequest();
            slotRequest.setDate(newShiftEmployee.getDate());
            slotRequest.setShiftEmployeeId(newShiftEmployee.getId());
            slotRequest.setStartHour(timeService.startHour);
            slotRequest.setEndHour(timeService.endHour);
            slotRequest.setDuration(timeService.duration);
            List<Slot> slotList = slotService.generateSlots(slotRequest);
            newShiftEmployee.setSlots(slotList);
            // SAVE LẠI VÀO DB
            ShiftEmployee savedShift = shiftEmployeeRepository.save(newShiftEmployee);

            //ADD TO ACCOUNT FOR EMPLOYEE => MỘT STYLIST CÓ NHIỀU SHIFTS
            shiftEmployeeList.add(savedShift);

            // GENERATE RESPONSE
            ShiftEmployeeResponse shiftEmployeeResponse = new ShiftEmployeeResponse();
            shiftEmployeeResponse.setId(savedShift.getId());
            shiftEmployeeResponse.setAvailable(savedShift.isAvailable());
            shiftEmployeeResponse.setEmployeeId(savedShift.getAccountForEmployee().getId());
            shiftEmployeeResponse.setName(savedShift.getAccountForEmployee().getName());
            shiftEmployeeResponse.setDayInWeek(savedShift.getShiftInWeek().getDayOfWeek());
            shiftEmployeeResponse.setDate(savedShift.getDate());

            shiftEmployeeResponseList.add(shiftEmployeeResponse);
        }

        // LƯU LẠI ACCOUNT FOR EMPLOYEE
        accountForEmployee.setShiftEmployees(shiftEmployeeList);
        AccountForEmployee savedAccount = employeeRepository.save(accountForEmployee);

        return shiftEmployeeResponseList;
    }



    // TẠO ALL SHIFTS CHO ALL STYLISTS -> MANAGER LÀM
    public List<ShiftEmployeeResponse> generateAllShiftEmployees(){
        String role = "Stylist";
        String status = "Workday";
        List<ShiftEmployeeResponse> allShiftEmployeeResponseList = new ArrayList<>();
        List<AccountForEmployee> accountForEmployeeList = employeeRepository
                .findAccountForEmployeesByRoleAndStatusAndIsDeletedFalse(role, status);
        if(accountForEmployeeList != null) {
            for(AccountForEmployee accountForEmployee : accountForEmployeeList){
                List<ShiftEmployeeResponse> shiftEmployeeResponseList = generateShiftEmployee(accountForEmployee);
                allShiftEmployeeResponseList.addAll(shiftEmployeeResponseList);
            }
            return allShiftEmployeeResponseList;
        } else {
            throw new EntityNotFoundException("Can not execute!");
        }
    }

    // 2 HÀM DƯỚI TEST CHO VUI
    public List<ShiftEmployee> getAllShift(){
        List<ShiftEmployee> shiftEmployeeList = shiftEmployeeRepository.findAll();
        return shiftEmployeeList;
    }

    public String getShift(long id){
        ShiftEmployee shiftEmployee = shiftEmployeeRepository.findShiftEmployeeById(id);
        if(shiftEmployee.getSlots() != null){
            Slot slot = shiftEmployee.getSlots().get(0);
            String a = slot.getStartSlot();
            return a;
        }
        return null;
    }

    // HÀM LẤY DANH SÁCH CÁC STYLIST VÀ THỜI GIAN KHẢ DỤNG DỰA TRÊN NGÀY
    public List<AvailableSlot> getAllAvailableSlots(String date){
        List<ShiftEmployee> shiftEmployeeList = shiftEmployeeRepository
                .findShiftEmployeesByDateAndIsAvailableTrue(date);
        List<AvailableSlot> availableSlotList = new ArrayList<>();
        for (ShiftEmployee shiftEmployee : shiftEmployeeList) {
            List<Slot> slotList = shiftEmployee.getSlots();
            for (Slot slot : slotList) {
                if(slot.isAvailable()) {
                    // GENERATE RESPONSE
                    AvailableSlot availableSlot = new AvailableSlot();
                    availableSlot.setSlotId(slot.getId());
                    availableSlot.setStylistName(slot.getShiftEmployee().getAccountForEmployee().getName());
                    availableSlot.setStylistLevel(slot.getShiftEmployee().getAccountForEmployee().getStylistLevel());
                    availableSlot.setStartHour(slot.getStartSlot());
                    availableSlotList.add(availableSlot);
                }
            }
        }
        return availableSlotList;
    }



/* => COMMENT TẠM THỜI

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
                // TÌM APPOINTMENT NẰM TRONG SLOT ĐÓ VÀ CHƯA HOÀN THÀNH - VD STYLIST ĐANG LÀM THỨ 2 MUỐN NGHỈ THỨ 3 MÀ THỨ 3 CÓ ĐƠN
                Appointment checkAppointment = appointmentRepository.findAppointmentBySlot_IdAndIsCompletedFalse(savedSlot.getId());
                if(checkAppointment != null){
                    if(!checkAppointment.isCompleted()){   // TÌM RA APPOINTMENT VÀ NÓ CHƯA COMPLETE
                        String appointmentID = String.valueOf(checkAppointment.getId());
                        String message = "FOUND " + appointmentID;
                        appointment.add(message);
                    }
                }
            }

            if (appointment.size() == 1){  // LIST CHỈ CÓ DÒNG DELETE COMPLETE
                appointment.add("NO APPOINTMENT FOUND");
            }
            // GENERATE RESPONSE
            *//*ShiftEmployeeResponse shiftEmployeeResponse = modelMapper.map(shiftEmployee, ShiftEmployeeResponse.class);
            shiftEmployeeResponse.setDayInWeek(shiftEmployee.getShiftInWeek().getDayOfWeek());
            shiftEmployeeResponse.setEmployeeId(shiftEmployee.getAccountForEmployee().getId());
            shiftEmployeeResponse.setName(shiftEmployee.getAccountForEmployee().getName());*//*

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
            throw new EntityNotFoundException("Can not complete shift!");
        }
    }


    //*//*/HÀM NÀY TRẢ VỀ DANH SÁCH CÁC THỨ TRONG TUẦN
    public List<String> getAllDaysInWeek(){
        List<String> days = new ArrayList<>();
        days.add("MONDAY");
        days.add("TUESDAY");
        days.add("WEDNESDAY");
        days.add("THURSDAY");
        days.add("FRIDAY");
        days.add("SATURDAY");
        return days;
    }

    // CHECK XEM DAY CỦA STYLIST LÀ Ỏ THỨ TỰ NÀO TRÊN LIST
    public int checkDay(String day){
        int number = 0; // STT
        List<String> days = getAllDaysInWeek();
        for(int i = 0; i < days.size(); i++){
            if(getAllDaysInWeek().get(i).equals(day)){
                number = i;
                break;
            }
        }
        return number;
    }*//*

    //HÀM NÀY LẤY RA TOÀN BỘ DANH SÁCH CA LÀM VIỆC CỦA STYLIST, TOÀN BỘ CA TRONG TUẦN MÀ KHÔNG QUAN TÂM HIỆN TẠI LÀ THỨ MẤY
    //KHÁCH HÀNG TỰ HIỂU QUY TẮC LÀ KHÔNG ĐƯỢC CHỌN TRONG NGÀY VÀ TRƯỚC NGÀY
    // THEO QUY TẮC THÌ SAU NGÀY THỨ 7 MANAGER SẼ RESET MỌI THỨ VỀ TRẠNG THÁI BAN ĐẦU NÊN NẾU CUSTOMER ĐẶT LỊCH TRƯỚC NGÀY HIỆN TẠI THÌ LỊCH ĐÓ SẼ BỊ XÓA
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
    */ //=> COMMENT TẠM THỜI
}
