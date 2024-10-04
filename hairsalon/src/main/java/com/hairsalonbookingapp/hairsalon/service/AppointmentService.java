package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.*;
import com.hairsalonbookingapp.hairsalon.exception.EntityNotFoundException;
import com.hairsalonbookingapp.hairsalon.model.*;
import com.hairsalonbookingapp.hairsalon.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public List<HairSalonServiceResponse> getServiceList() {            // LẤY RA DANH SÁCH DỊCH VỤ KHẢ DỤNG
        List<HairSalonServiceResponse> list = hairSalonBookingAppService.getAllService();
        if (list != null) {
            return list;
        } else {
            throw new EntityNotFoundException("Service not found!");
        }
    }

    public HairSalonService getService(long serviceId) {        // HÀM LẤY SERVICE
        HairSalonService service = serviceRepository.findHairSalonServiceByIdAndStatusTrue(serviceId);
        if(service != null){
            return service;
        } else {
            throw new EntityNotFoundException("Service not found!");
        }
    }

    //CUSTOMER XEM VÀ CHỌN STYLIST
    public List<StylistInfo> getAllStylistInFo() {              //  LẤY DANH SÁCH STYLIST KHẢ DỤNG
        List<StylistInfo> list = authenticationService.getAllStylist();
        return list;
    }

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
    public List<ShiftEmployeeResponse> getShiftEmployees(String stylistId) {     //CUSTOMER TÌM CÁC CA LÀM VIỆC KHẢ DỤNG CỦA STYLIST
        List<ShiftEmployee> shiftEmployeeList = shiftEmployeeService.getShiftsOfEmployee(stylistId);
        List<ShiftEmployeeResponse> shiftEmployeeResponseList = new ArrayList<>();
        for(ShiftEmployee shiftEmployee : shiftEmployeeList){
            ShiftEmployeeResponse shiftEmployeeResponse = modelMapper.map(shiftEmployee, ShiftEmployeeResponse.class);
            shiftEmployeeResponseList.add(shiftEmployeeResponse);
        }
        return shiftEmployeeResponseList;
    }

    public List<SlotResponse> viewAvailableSlots(long shiftEmployeeId) {     // XEM CÁC SLOT KHẢ DỤNG CỦA CA
        List<Slot> slotList = slotService.getSlots(shiftEmployeeId);
        List<SlotResponse> slotResponseList = new ArrayList<>();
        for(Slot slot : slotList){
            SlotResponse slotResponse = modelMapper.map(slot, SlotResponse.class);
            slotResponseList.add(slotResponse);
        }
        return slotResponseList;
    }

    public Slot getAvailableSlot(long slotId) {   // HÀM LẤY SLOT
        Slot slot = slotRepository.findSlotByIdAndStatusTrue(slotId);
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
            double bonusDiscountCode = 0;
            double bonusEmployee = 0;
            double serviceFee = serviceRepository.findHairSalonServiceByIdAndStatusTrue(appointmentRequest.getServiceId()).getCost();
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
            appointment.setStatus(true);

            Appointment newAppointment = appointmentRepository.save(appointment);

            AppointmentResponse appointmentResponse = modelMapper.map(newAppointment, AppointmentResponse.class);
            appointmentResponse.setServiceId(newAppointment.getHairSalonService().getId());
            appointmentResponse.setCustomerId(newAppointment.getAccountForCustomer().getPhoneNumber());
            appointmentResponse.setSlotId(newAppointment.getSlot().getId());
            appointmentResponse.setDiscountCodeId(newAppointment.getDiscountCode().getId());

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
        Appointment oldAppointment = appointmentRepository.findAppointmentByIdAndStatusTrue(id);  //TÌM LẠI APPOINTMENT CŨ
        if (oldAppointment != null) {   // TÌM THẤY
            try{
                double cost = 0;          // TÍNH TOÁN COST TỪ ĐẦU
                double newBonusEmployee = 0;
                double newBonusCode = 0;
                if (appointmentUpdate.getServiceId() != 0) {      // NẾU CUSTOMER CÓ NHẬP ID SERVICE
                    oldAppointment.setHairSalonService(getService(appointmentUpdate.getServiceId()));       // CẬP NHẬT
                    cost += serviceRepository.findHairSalonServiceByIdAndStatusTrue(appointmentUpdate.getServiceId()).getCost(); // CẬP NHẬT LẠI VÀO COST
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
