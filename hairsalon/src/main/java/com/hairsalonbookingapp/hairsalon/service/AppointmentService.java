package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.*;
import com.hairsalonbookingapp.hairsalon.exception.EntityNotFoundException;
import com.hairsalonbookingapp.hairsalon.model.*;
import com.hairsalonbookingapp.hairsalon.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    SlotService slotService;

    @Autowired
    ShiftEmployeeService shiftEmployeeService;

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    HairSalonBookingAppService hairSalonBookingAppService;

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    SlotRepository slotRepository;

    @Autowired
    DiscountCodeRepository discountCodeRepository;

    //CUSTOMER XEM VÀ CHỌN DỊCH VỤ
    /*public List<HairSalonServiceResponse> getServiceList() {            // LẤY RA DANH SÁCH DỊCH VỤ KHẢ DỤNG
        List<HairSalonServiceResponse> list = hairSalonBookingAppService.getAllAvailableService();
        return list;
    }*/

    // CHỨC NĂNG getAllAvailableService(); BÊN HAIR SALON BOOKING APP SERVICE : CUSTOMER XEM CÁC DỊCH VỤ KHẢ DỤNG


    public HairSalonService getService(long serviceId) {        // HÀM LẤY SERVICE
        HairSalonService service = serviceRepository.findHairSalonServiceByIdAndIsAvailableTrue(serviceId);
        if(service != null){
            return service;
        } else {
            throw new EntityNotFoundException("Service not found!");
        }
    }

    //CUSTOMER XEM VÀ CHỌN STYLIST
    /*public List<StylistInfo> getAllStylistInFo() {
        List<StylistInfo> list = authenticationService.getAllStylist();
        return list;
    }*/

    //  CHỨC NĂNG getAllAvailableStylist(); BÊN EMPLOYEE SERVICE : CUSTOMER CHỌN CÁC STYLIST KHẢ DỤNG

    public AccountForEmployee getStylist(String stylistId) {// HÀM LẤY STYLIST
        String status = "Workday";
        AccountForEmployee account = employeeRepository.findAccountForEmployeeByIdAndStatusAndIsDeletedFalse(stylistId, status);
        if(account != null){
            return account;
        } else {
            throw new EntityNotFoundException("Stylist not found!");
        }
    }

    //CUSTOMER CHỌN CA LÀM VIỆC (THỨ 2, 3,...) VÀ SLOT PHÙ HỢP
    //HÀM NÀY LẤY RA TOÀN BỘ DANH SÁCH CA LÀM VIỆC CỦA STYLIST, TOÀN BỘ CA TRONG TUẦN MÀ KHÔNG QUAN TÂM HIỆN TẠI LÀ THỨ MẤY
    //KHÁCH HÀNG TỰ HIỂU QUY TẮC LÀ KHÔNG ĐƯỢC CHỌN TRONG NGÀY VÀ TRƯỚC NGÀY
    public List<ShiftEmployeeResponse> getAvailableShiftEmployees(String stylistId) {     //CUSTOMER TÌM CÁC CA LÀM VIỆC KHẢ DỤNG CỦA STYLIST
        List<ShiftEmployee> shiftEmployeeList = shiftEmployeeService.getShiftsOfEmployee(stylistId);
        List<ShiftEmployeeResponse> shiftEmployeeResponseList = new ArrayList<>();
        for(ShiftEmployee shiftEmployee : shiftEmployeeList){
            ShiftEmployeeResponse shiftEmployeeResponse = modelMapper.map(shiftEmployee, ShiftEmployeeResponse.class);
            shiftEmployeeResponse.setEmployeeId(stylistId);
            shiftEmployeeResponse.setDayInWeek(shiftEmployee.getShiftInWeek().getDayOfWeek());
            shiftEmployeeResponseList.add(shiftEmployeeResponse);
        }
        return shiftEmployeeResponseList;
    }

    //HÀM DƯỚI LÀ TÍNH CHÍNH XÁC THEO THỜI GIAN THỰC ĐỂ XEM KHÁCH CÒN NHỮNG NGÀY KHẢ DỤNG NÀO, NHƯNG SẼ KHÓ TEST NÊN TẠM THỜI KHÔNG LÀM
    /*public List<String> getAvailableShiftEmployee(String stylistId){
        List<ShiftEmployee> shiftEmployeeList = shiftEmployeeService.getShiftsOfEmployee(stylistId);
        List<String> daysOfWeek = new ArrayList<>();
        for(ShiftEmployee shiftEmployee : shiftEmployeeList){
            String dayOfWeek = shiftEmployee.getShiftInWeek().getDayOfWeek();
            daysOfWeek.add(dayOfWeek);
        }
        LocalDate currentDate = LocalDate.now();
        DayOfWeek dayInWeek = currentDate.getDayOfWeek();

    }*/



    public List<SlotResponse> viewAvailableSlots(long shiftEmployeeId) {     // XEM CÁC SLOT KHẢ DỤNG CỦA CA
        List<Slot> slotList = slotService.getSlots(shiftEmployeeId);
        List<SlotResponse> slotResponseList = new ArrayList<>();
        for(Slot slot : slotList){
            SlotResponse slotResponse = modelMapper.map(slot, SlotResponse.class);
            slotResponse.setShiftEmployeeId(shiftEmployeeId);
            slotResponseList.add(slotResponse);
        }
        return slotResponseList;
    }

    public Slot getAvailableSlot(long slotId) {   // HÀM LẤY SLOT
        Slot slot = slotRepository.findSlotByIdAndIsAvailableTrue(slotId);
        if(slot != null){
            return slot;
        } else {
            throw new EntityNotFoundException("Slot not found!");
        }
    }

    //CUSTOMER NHẬP MÃ GIẢM GIÁ (TÙY CHỌN)
    public DiscountCode getDiscountCode(String DiscountCodeId) {    // HÀM LẤY MÃ GIẢM GIÁ -> CẦN COI LẠI
        DiscountCode discountCode = discountCodeRepository.findDiscountCodeById(DiscountCodeId);
        if(discountCode != null){
            return discountCode;
        } else {
            throw new EntityNotFoundException("Invalid code");
        }
    }

    //HỆ THỐNG CHỐT
    public AppointmentResponse createNewAppointment(AppointmentRequest appointmentRequest) {
        try {
            double bonusDiscountCode = 0;    // PHÍ GIẢM GIÁ CỦA MÃ (NẾU CÓ)
            double bonusEmployee = 0;   // PHÍ TRẢ THÊM CHO STYLIST DỰA TRÊN CẤP ĐỘ
            double serviceFee = serviceRepository.findHairSalonServiceByIdAndIsAvailableTrue(appointmentRequest.getServiceId()).getCost();  // PHÍ GỐC CỦA SERVICE
            Appointment appointment = new Appointment();
            appointment.setSlot(getAvailableSlot(appointmentRequest.getSlotId()));
            appointment.setAccountForCustomer(authenticationService.getCurrentAccountForCustomer());
            appointment.setHairSalonService(getService(appointmentRequest.getServiceId()));

            if (appointmentRequest.getDiscountCodeId().isEmpty()) {
                appointment.setDiscountCode(null);
            } else {
                DiscountCode discountCode = getDiscountCode(appointmentRequest.getDiscountCodeId());
                appointment.setDiscountCode(discountCode);
                bonusDiscountCode += (discountCode.getDiscountProgram().getPercentage()) / 100;
            }

            AccountForEmployee accountForEmployee = getStylist(appointmentRequest.getStylistId());
            if (accountForEmployee.getExpertStylistBonus() != 0) {
                bonusEmployee += (accountForEmployee.getExpertStylistBonus()) / 100;
            }

            double cost = serviceFee - (bonusDiscountCode * serviceFee) + (bonusEmployee * serviceFee);
            appointment.setCost(cost);

            Appointment newAppointment = appointmentRepository.save(appointment);

            AppointmentResponse appointmentResponse = modelMapper.map(newAppointment, AppointmentResponse.class);
            /*appointmentResponse.setServiceId(newAppointment.getHairSalonService().getId());
            appointmentResponse.setCustomerId(newAppointment.getAccountForCustomer().getPhoneNumber());
            appointmentResponse.setSlotId(newAppointment.getSlot().getId());
            appointmentResponse.setDiscountCodeId(newAppointment.getDiscountCode().getId());*/

            appointmentResponse.setDay(newAppointment.getSlot().getShiftEmployee().getShiftInWeek().getDayOfWeek());
            appointmentResponse.setStartHour(newAppointment.getSlot().getStartSlot());
            appointmentResponse.setCustomer(newAppointment.getAccountForCustomer().getUsername());
            appointmentResponse.setService(newAppointment.getHairSalonService().getName());
            appointmentResponse.setStylist(newAppointment.getSlot().getShiftEmployee().getName());

            return appointmentResponse;
        } catch (Exception e) {
            throw new EntityNotFoundException("Can not create appointment: " + e.getMessage());
        }
    }
    //CẬP NHẬT APPOINTMENT -> CUSTOMER LÀM
    // CẬP NHẬT APPOINTMENT CÓ 2 TRƯỜNG HỢP:
    //1. CUSTOMER CHƯA GỬI ĐƠN : TRƯỜNG HỢP NÀY KHÔNG VẤN ĐỀ GÌ, CUSTOMER CÓ THỂ THAO TÁC LẠI CÁC HÀM Ở TRÊN ĐỂ LỰA CHỌN LẠI
    // SAU ĐÓ CHỈNH SỬA ĐẦU VÀO CỦA createNewAppointment LÀ XONG
    //2. CUSTOMER ĐÃ GỬI ĐƠN VÀ MUỐN LẤY LẠI ĐƠN ĐỂ SỬA: CÓ 2 CÁCH GIẢI QUYẾT:
    // A. CUSTOMER HỦY ĐƠN VÀ LÀM LẠI ĐƠN KHÁC
    // B. CUSTOMER LẤY LẠI ĐƠN VÀ UPDATE LẠI THÔNG TIN

    public AppointmentResponse updateAppointment(AppointmentUpdate appointmentUpdate, long id) {
        String status = "Appointment sent!";
        Appointment oldAppointment = appointmentRepository.findAppointmentByIdAndStatus(id, status);  //TÌM LẠI APPOINTMENT CŨ
        if (oldAppointment != null) {   // TÌM THẤY
            try{
                double cost = 0;          // TÍNH TOÁN COST TỪ ĐẦU
                double newBonusEmployee = 0;
                double newBonusCode = 0;
                if (appointmentUpdate.getServiceId() != 0) {      // NẾU CUSTOMER CÓ NHẬP ID SERVICE
                    oldAppointment.setHairSalonService(getService(appointmentUpdate.getServiceId()));       // CẬP NHẬT
                    //cost += serviceRepository.findHairSalonServiceByIdAndIsAvailableTrue(appointmentUpdate.getServiceId()).getCost(); // CẬP NHẬT LẠI VÀO COST
                    cost += oldAppointment.getHairSalonService().getCost();
                } else {                                        // NẾU CUSTOMER KHÔNG NHẬP ID SERVICE
                    cost += oldAppointment.getHairSalonService().getCost();  //  COST SERVICE NHƯ CŨ
                }

                if (appointmentUpdate.getSlotId() != 0) {         // NẾU CUSTOMER CÓ NHẬP SLOT ID
                    oldAppointment.setSlot(getAvailableSlot(appointmentUpdate.getSlotId()));  // CẬP NHẬT
                }

                if (!appointmentUpdate.getStylistId().isEmpty()) {       // NẾU CUSTOMER NHẬP STYLIST ID -> STYLIST MỚI

                    if (getStylist(appointmentUpdate.getStylistId()).getExpertStylistBonus() != 0) {    // NẾU STYLIST MỚI LÀ DÂN CHUYÊN
                        AccountForEmployee accountForEmployee = getStylist(appointmentUpdate.getStylistId());
                        newBonusEmployee += (accountForEmployee.getExpertStylistBonus()) / 100;   // CẬP NHẬT BONUS CHO NÓ
                    }

                } else {                                                                        // NẾU CUSTOMER KO NHẬP -> STYLIST CŨ
                    String oldStylistId = oldAppointment.getSlot().getShiftEmployee().getAccountForEmployee().getId();
                    AccountForEmployee account = getStylist(oldStylistId);

                    if (account.getExpertStylistBonus() != 0) {      // STYLIST CŨ VẪN LÀ DÂN CHUYÊN
                        newBonusEmployee += (account.getExpertStylistBonus()) / 100;
                    }

                }

                if (!appointmentUpdate.getDiscountCodeId().isEmpty()) {        //  CUSTOMER NHẬP MÃ MỚI

                    DiscountCode newCode = getDiscountCode(appointmentUpdate.getDiscountCodeId());
                    DiscountProgram discountProgram = newCode.getDiscountProgram();
                    newBonusCode += (discountProgram.getPercentage()) / 100;

                } else {      // MÃ CŨ

                    DiscountCode oldCode = oldAppointment.getDiscountCode();
                    DiscountProgram oldProgram = oldCode.getDiscountProgram();
                    newBonusCode += (oldProgram.getPercentage()) / 100;

                }

                double newCost = cost + (newBonusEmployee * cost) + (newBonusCode * cost);
                oldAppointment.setCost(newCost);

                Appointment newAppointment = appointmentRepository.save(oldAppointment);     // LƯU LẠI LÊN DB

                AppointmentResponse appointmentResponse = modelMapper.map(newAppointment, AppointmentResponse.class);
                appointmentResponse.setServiceId(newAppointment.getHairSalonService().getId());
                appointmentResponse.setCustomerId(newAppointment.getAccountForCustomer().getPhoneNumber());
                appointmentResponse.setSlotId(newAppointment.getSlot().getId());
                appointmentResponse.setDiscountCodeId(newAppointment.getDiscountCode().getId());

                return appointmentResponse;
            } catch (Exception e) {
                throw new EntityNotFoundException("Can not update appointment: " + e.getMessage());
            }

        } else {
            throw new EntityNotFoundException("Appointment not found!");
        }
    }

    // XÓA APPOINTMENT  -> CUSTOMER LÀM
    public AppointmentResponse deleteAppointment(long id){
        Appointment oldAppointment = appointmentRepository.findAppointmentByIdAndStatusTrue(id);  //TÌM LẠI APPOINTMENT CŨ
        if(oldAppointment != null){
            oldAppointment.setStatus(false);
            Appointment newAppointment = appointmentRepository.save(oldAppointment);     // LƯU LẠI LÊN DB

            AppointmentResponse appointmentResponse = modelMapper.map(newAppointment, AppointmentResponse.class);
            appointmentResponse.setServiceId(newAppointment.getHairSalonService().getId());
            appointmentResponse.setCustomerId(newAppointment.getAccountForCustomer().getPhoneNumber());
            appointmentResponse.setSlotId(newAppointment.getSlot().getId());
            appointmentResponse.setDiscountCodeId(newAppointment.getDiscountCode().getId());

            return appointmentResponse;
        } else {
            throw new EntityNotFoundException("Appointment not found!");
        }
    }

}
