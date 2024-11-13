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

    @Autowired
    EmployeeService employeeService;


    //ĐĂNG KÝ SHIFT EMPLOYEE (STYLIST) -> MANAGER LÀM
    public AccountResponseForEmployee registerShifts(StylistShiftRequest stylistShiftRequest) {
        // CẬP NHẬT VÀO ACCOUNT FOR EMPLOYEE
        String days = String.join(",", stylistShiftRequest.getWorkDays()); // LẤY CÁC GIÁ TRỊ TRONG LIST WORKDAYS VÀ BIẾN NÓ THÀNH 1 CHUỖI
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
        String[] daysOfWeek = days.split(","); // TÁCH CHUỖI CÁC NGÀY THÀNH 1 DANH SÁCH DỰA TRÊN DẤU ,
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
            slotRequest.setStartHour(timeService.setStartHour(day));
            slotRequest.setEndHour(timeService.setEndHour(day));
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
        //CHECK XEM MANAGER ĐÃ DÙNG CHỨC NĂNG NÀY CHƯA
        List<LocalDate> nextWeekDays = timeService.getNextWeekDays(timeService.today); // LIST CÁC NGÀY TUẦN SAU
        List<ShiftEmployee> shiftEmployeeList = shiftEmployeeRepository.findAll(); // LIST CÁC SHIFT EMPLOYEE TRONG DB
        for(LocalDate nextWeekDay : nextWeekDays){
            for(ShiftEmployee shiftEmployee : shiftEmployeeList){
                if(nextWeekDay.toString().equals(shiftEmployee.getDate())){
                    throw new DuplicateEntity("You can only use this function once per week!!!");
                }
            }
        }
        // => MANAGER CHƯA DÙNG CHỨC NĂNG NÀY
        List<String> foundStylists = employeeService.getStylistsThatWorkDaysNull(); // CHECK XEM CÓ STYLIST NÀO CHƯA SET WORKDAY
        if(foundStylists.isEmpty()){ //TÌM KHÔNG THẤY
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
        } else {
            throw new EntityNotFoundException("Can not process because some stylists did not set work days!!!");
        }
    }

    //TẠO THÊM NGÀY LÀM BÙ CHO STYLIST (VÍ DỤ TRƯỜNG HỢP STYLIST BẬN THỨ 3 VÀ MUỐN BÙ THỨ 4) -> MANAGER LÀM
    //LƯU Ý : CHỈ TẠO TRONG TUẦN
    public ShiftEmployeeResponse createTempShift(String stylistId, String date){
        AccountForEmployee accountForEmployee = employeeRepository.findAccountForEmployeeById(stylistId);
        if(accountForEmployee == null){
            throw new EntityNotFoundException("Invalid id!!!");
        }

        //TẠO SHIFT EMPLOYEE
        ShiftEmployee shiftEmployee = new ShiftEmployee();

        String day = timeService.getDay(date); // TÌM RA THỨ

        ShiftInWeek shiftInWeek = shiftWeekRepository
                .findShiftInWeekByDayOfWeekAndIsAvailableTrue(day);
        shiftEmployee.setShiftInWeek(shiftInWeek);
        shiftEmployee.setAccountForEmployee(accountForEmployee);
        shiftEmployee.setDate(date);

        //SAVE VÀO DB
        ShiftEmployee newShiftEmployee = shiftEmployeeRepository.save(shiftEmployee);

        // TẠO CÁC SLOT
        SlotRequest slotRequest = new SlotRequest();
        slotRequest.setDate(newShiftEmployee.getDate());
        slotRequest.setShiftEmployeeId(newShiftEmployee.getId());
        slotRequest.setStartHour(timeService.setStartHour(day));
        slotRequest.setEndHour(timeService.setEndHour(day));
        slotRequest.setDuration(timeService.duration);
        List<Slot> slotList = slotService.generateSlots(slotRequest);
        newShiftEmployee.setSlots(slotList);
        // SAVE LẠI VÀO DB
        ShiftEmployee savedShift = shiftEmployeeRepository.save(newShiftEmployee);

        // GENERATE RESPONSE
        ShiftEmployeeResponse shiftEmployeeResponse = new ShiftEmployeeResponse();
        shiftEmployeeResponse.setId(savedShift.getId());
        shiftEmployeeResponse.setAvailable(savedShift.isAvailable());
        shiftEmployeeResponse.setEmployeeId(savedShift.getAccountForEmployee().getId());
        shiftEmployeeResponse.setName(savedShift.getAccountForEmployee().getName());
        shiftEmployeeResponse.setDayInWeek(savedShift.getShiftInWeek().getDayOfWeek());
        shiftEmployeeResponse.setDate(savedShift.getDate());

        return shiftEmployeeResponse;
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
                if(slot.isAvailable()) {   // SLOT CÒN TRỐNG
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

    // HÀM LẤY DANH SÁCH STYLIST KHẢ DỤNG DỰA TRÊN GIỜ VÀ NGÀY
    public List<AvailableSlot> getAllAvailableSlotsByHour(String hour, String date){
        List<AvailableSlot> availableSlotListByHour = new ArrayList<>();
        for(AvailableSlot availableSlot : getAllAvailableSlots(date)){
            if(availableSlot.getStartHour().equals(hour)){
                availableSlotListByHour.add(availableSlot);
            }
        }
        return availableSlotListByHour;
    }

    //HÀM GIÚP PHÂN TRANG -> HỖ TRỢ HÀM DƯỚI
    public List<ShiftEmployeeResponse> paginate(List<ShiftEmployeeResponse> items, int page, int pageSize) {
        // Xác định vị trí bắt đầu và kết thúc của trang
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, items.size());

        // Kiểm tra xem trang có hợp lệ không
        if (fromIndex > items.size()) {
            throw new IllegalArgumentException("No result found!");
        }

        // Trả về sublist từ vị trí fromIndex đến toIndex
        return items.subList(fromIndex, toIndex);
    }

    //HÀM NÀY LẤY TOÀN BỘ SHIFT EMPLOYEE CỦA STYLIST TRONG TUẦN -> STYLIST LÀM
    public ShiftEmployeeResponsePage getAllShiftEmployeesInWeekByStylist(String startDate, int page, int pageSize){
        AccountForEmployee accountForEmployee = authenticationService.getCurrentAccountForEmployee();

        LocalDate startWeek = LocalDate.of(
                Integer.parseInt(startDate.substring(0,4)),
                Integer.parseInt(startDate.substring(5,7)),
                Integer.parseInt(startDate.substring(8))
        );

        List<ShiftEmployee> allShiftEmployeeList = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            LocalDate date = startWeek.plusDays(i);
            ShiftEmployee shiftEmployee = shiftEmployeeRepository
                    .findShiftEmployeeByAccountForEmployeeAndDateAndIsAvailableTrue(accountForEmployee, date.toString());
            if(shiftEmployee != null){
                allShiftEmployeeList.add(shiftEmployee);
            }
        }

        List<ShiftEmployeeResponse> shiftEmployeeResponseList = new ArrayList<>();
        for(ShiftEmployee shiftEmployee : allShiftEmployeeList){
            ShiftEmployeeResponse shiftEmployeeResponse = new ShiftEmployeeResponse();
            shiftEmployeeResponse.setId(shiftEmployee.getId());
            shiftEmployeeResponse.setAvailable(shiftEmployee.isAvailable());
            shiftEmployeeResponse.setEmployeeId(shiftEmployee.getAccountForEmployee().getId());
            shiftEmployeeResponse.setName(shiftEmployee.getAccountForEmployee().getName());
            shiftEmployeeResponse.setDayInWeek(shiftEmployee.getShiftInWeek().getDayOfWeek());
            shiftEmployeeResponse.setDate(shiftEmployee.getDate());
            shiftEmployeeResponse.setStartHour(shiftEmployee.getShiftInWeek().getStartHour());
            shiftEmployeeResponse.setEndHour(shiftEmployee.getShiftInWeek().getEndHour());


            shiftEmployeeResponseList.add(shiftEmployeeResponse);
        }

        ShiftEmployeeResponsePage shiftEmployeeResponsePage = new ShiftEmployeeResponsePage();
        shiftEmployeeResponsePage.setContent(paginate(shiftEmployeeResponseList, page, pageSize));
        shiftEmployeeResponsePage.setPageNumber(page);
        shiftEmployeeResponsePage.setTotalElements(shiftEmployeeResponseList.size());
        int totalPages = (shiftEmployeeResponseList.size() + pageSize - 1)/(pageSize);
        shiftEmployeeResponsePage.setTotalPages(totalPages);

        return shiftEmployeeResponsePage;
    }

    //HÀM NÀY LẤY TOÀN BỘ SHIFT EMPLOYEE CỦA STYLIST TRONG TUẦN -> STAFF LÀM
    public ShiftEmployeeResponsePage getAllShiftEmployeesInWeekByStaff(String stylistId, String startDate, int page, int pageSize){
        AccountForEmployee accountForEmployee = employeeRepository.findAccountForEmployeeById(stylistId);
        if(accountForEmployee == null){
            throw new EntityNotFoundException("Invalid id!");
        }

        LocalDate startWeek = LocalDate.of(
                Integer.parseInt(startDate.substring(0,4)),
                Integer.parseInt(startDate.substring(5,7)),
                Integer.parseInt(startDate.substring(8))
        );

        List<ShiftEmployee> allShiftEmployeeList = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            LocalDate date = startWeek.plusDays(i);
            ShiftEmployee shiftEmployee = shiftEmployeeRepository
                    .findShiftEmployeeByAccountForEmployeeAndDateAndIsAvailableTrue(accountForEmployee, date.toString());
            if(shiftEmployee != null){
                allShiftEmployeeList.add(shiftEmployee);
            }
        }

        List<ShiftEmployeeResponse> shiftEmployeeResponseList = new ArrayList<>();
        for(ShiftEmployee shiftEmployee : allShiftEmployeeList){
            ShiftEmployeeResponse shiftEmployeeResponse = new ShiftEmployeeResponse();
            shiftEmployeeResponse.setId(shiftEmployee.getId());
            shiftEmployeeResponse.setAvailable(shiftEmployee.isAvailable());
            shiftEmployeeResponse.setEmployeeId(shiftEmployee.getAccountForEmployee().getId());
            shiftEmployeeResponse.setName(shiftEmployee.getAccountForEmployee().getName());
            shiftEmployeeResponse.setDayInWeek(shiftEmployee.getShiftInWeek().getDayOfWeek());
            shiftEmployeeResponse.setDate(shiftEmployee.getDate());
            shiftEmployeeResponse.setStartHour(shiftEmployee.getShiftInWeek().getStartHour());
            shiftEmployeeResponse.setEndHour(shiftEmployee.getShiftInWeek().getEndHour());

            shiftEmployeeResponseList.add(shiftEmployeeResponse);
        }

        ShiftEmployeeResponsePage shiftEmployeeResponsePage = new ShiftEmployeeResponsePage();
        shiftEmployeeResponsePage.setContent(paginate(shiftEmployeeResponseList, page, pageSize));
        shiftEmployeeResponsePage.setPageNumber(page);
        shiftEmployeeResponsePage.setTotalElements(shiftEmployeeResponseList.size());
        int totalPages = (shiftEmployeeResponseList.size() + pageSize - 1)/(pageSize);
        shiftEmployeeResponsePage.setTotalPages(totalPages);

        return shiftEmployeeResponsePage;
    }

    //HÀM NÀY LẤY TOÀN BỘ SHIFT EMPLOYEE TRONG TUẦN -> STAFF/MANAGER LÀM
    public ShiftEmployeeResponsePage getAllShiftEmployeesInWeek(String startDate, int page, int pageSize){
        LocalDate startWeek = LocalDate.of(
                Integer.parseInt(startDate.substring(0,4)),
                Integer.parseInt(startDate.substring(5,7)),
                Integer.parseInt(startDate.substring(8))
        );

        List<ShiftEmployee> allShiftEmployeeList = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            LocalDate date = startWeek.plusDays(i);
            List<ShiftEmployee> shiftEmployeeList = shiftEmployeeRepository
                    .findShiftEmployeesByDateAndIsAvailableTrue(date.toString());
            if(!shiftEmployeeList.isEmpty()){
                allShiftEmployeeList.addAll(shiftEmployeeList);
            }
        }

        List<ShiftEmployeeResponse> shiftEmployeeResponseList = new ArrayList<>();
        for(ShiftEmployee shiftEmployee : allShiftEmployeeList){
            ShiftEmployeeResponse shiftEmployeeResponse = new ShiftEmployeeResponse();
            shiftEmployeeResponse.setId(shiftEmployee.getId());
            shiftEmployeeResponse.setAvailable(shiftEmployee.isAvailable());
            shiftEmployeeResponse.setEmployeeId(shiftEmployee.getAccountForEmployee().getId());
            shiftEmployeeResponse.setName(shiftEmployee.getAccountForEmployee().getName());
            shiftEmployeeResponse.setDayInWeek(shiftEmployee.getShiftInWeek().getDayOfWeek());
            shiftEmployeeResponse.setDate(shiftEmployee.getDate());
            shiftEmployeeResponse.setStartHour(shiftEmployee.getShiftInWeek().getStartHour());
            shiftEmployeeResponse.setEndHour(shiftEmployee.getShiftInWeek().getEndHour());

            shiftEmployeeResponseList.add(shiftEmployeeResponse);
        }

        ShiftEmployeeResponsePage shiftEmployeeResponsePage = new ShiftEmployeeResponsePage();
        shiftEmployeeResponsePage.setContent(paginate(shiftEmployeeResponseList, page, pageSize));
        shiftEmployeeResponsePage.setPageNumber(page);
        shiftEmployeeResponsePage.setTotalElements(shiftEmployeeResponseList.size());
        int totalPages = (shiftEmployeeResponseList.size() + pageSize - 1)/(pageSize);
        shiftEmployeeResponsePage.setTotalPages(totalPages);

        return shiftEmployeeResponsePage;
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





    //HÀM TẠO SHIFT EMPLOYEE: TẠO SHIFT EMPLOYEE CỦA 1 STYLIST TỪ NGÀY HIỆN TẠI ĐẾN CUỐI TUẦN
    // STARTDATE LÀ NGÀY BẮT ĐẦU: VÍ DỤ STARTDATE LÀ 13/11/2024 THÌ HỆ THỐNG SẼ TẠO CHO STYLIST CÁC CA NẰM TRONG KHOẢN TỪ 13/11/24 - 17/11/24
    public List<ShiftEmployeeResponse> generateShiftEmployeeByDate(String stylistId, String startDate){
        AccountForEmployee accountForEmployee = employeeRepository.findAccountForEmployeeById(stylistId);
        if(accountForEmployee == null){
            throw new EntityNotFoundException("Stylist not found!");
        }
        String days = accountForEmployee.getDays(); // LẤY CÁC NGÀY STYLIST CHỌN
        String[] daysOfWeek = days.split(","); // TÁCH CHUỖI CÁC NGÀY THÀNH 1 DANH SÁCH DỰA TRÊN DẤU ,
        List<LocalDate> daysUntilWeekDays = timeService.getDaysUntilWeekend(startDate);
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
            for(LocalDate date : daysUntilWeekDays){
                if(date.getDayOfWeek() == dayOfWeek){
                    shiftEmployee.setDate(date.toString());
                    break;
                }
            }
            if(shiftEmployee.getDate() != null){
                // SAVE VÀO DB
                ShiftEmployee newShiftEmployee = shiftEmployeeRepository.save(shiftEmployee);
                // TẠO CÁC SLOT
                SlotRequest slotRequest = new SlotRequest();
                slotRequest.setDate(newShiftEmployee.getDate());
                slotRequest.setShiftEmployeeId(newShiftEmployee.getId());
                slotRequest.setStartHour(timeService.setStartHour(day));
                slotRequest.setEndHour(timeService.setEndHour(day));
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

        }

        // LƯU LẠI ACCOUNT FOR EMPLOYEE
        accountForEmployee.setShiftEmployees(shiftEmployeeList);
        employeeRepository.save(accountForEmployee);

        return shiftEmployeeResponseList;
    }




    // TẠO ALL SHIFTS CHO ALL STYLISTS :  DÙNG HÀM TRÊN
    // KHUYẾN CÁO: NẾU 1 STYLIST MUỐN ĐỔI NGÀY LÀM VIỆC TRONG TUẦN, CÓ THỂ DÙNG RIÊNG HÀM TRÊN 1 CÁCH ĐỘC LẬP, NHƯNG PHẢI ĐẢM BAỎ RẰNG CHỨC NĂNG NÀY ĐÃ ĐƯỢC SỬ DỤNG TRONG TUẦN NÀY
    // VÌ NẾU DÙNG HÀM TRÊN TRƯỚC RỒI MỚI DÙNG HÀM DƯỚI, HÀM DƯỚI KHÔNG THỂ CHẠY ĐƯỢC VÌ HỆ THỐNG PHÁT HIỆN TUẦN ĐỊNH TẠO ĐÃ CÓ CA RỒI -> HỆ THỐNG MẶC ĐỊNH HIỂU HÀM DƯỚI ĐÃ DÙNG MẶC DÙ THỰC TẾ MÌNH CHƯA DÙNG
    public List<ShiftEmployeeResponse> generateAllShiftEmployeesByDate(String startDate){
        //CHECK XEM MANAGER ĐÃ DÙNG CHỨC NĂNG NÀY CHƯA
        List<LocalDate> daysUntilWeekend = timeService.getDaysUntilWeekend(startDate);
        List<ShiftEmployee> shiftEmployeeList = shiftEmployeeRepository.findAll(); // LIST CÁC SHIFT EMPLOYEE TRONG DB
        for(LocalDate date : daysUntilWeekend){
            for(ShiftEmployee shiftEmployee : shiftEmployeeList){
                if(date.toString().equals(shiftEmployee.getDate())){
                    throw new DuplicateEntity("You can only use this function once per week!!!");
                }
            }
        }
        // => MANAGER CHƯA DÙNG CHỨC NĂNG NÀY
        List<String> foundStylists = employeeService.getStylistsThatWorkDaysNull(); // CHECK XEM CÓ STYLIST NÀO CHƯA SET WORKDAY
        if(foundStylists.isEmpty()){ //TÌM KHÔNG THẤY
            String role = "Stylist";
            String status = "Workday";
            List<ShiftEmployeeResponse> allShiftEmployeeResponseList = new ArrayList<>();
            List<AccountForEmployee> accountForEmployeeList = employeeRepository
                    .findAccountForEmployeesByRoleAndStatusAndIsDeletedFalse(role, status);
            if(!accountForEmployeeList.isEmpty()) {
                for(AccountForEmployee accountForEmployee : accountForEmployeeList){
                    List<ShiftEmployeeResponse> shiftEmployeeResponseList = generateShiftEmployeeByDate(accountForEmployee.getId(), startDate);
                    allShiftEmployeeResponseList.addAll(shiftEmployeeResponseList);
                }
                return allShiftEmployeeResponseList;
            } else {
                throw new EntityNotFoundException("Can not execute!");
            }
        } else {
            throw new EntityNotFoundException("Can not process because some stylists did not set work days!!!");
        }
    }



}
