package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.*;
import com.hairsalonbookingapp.hairsalon.exception.EntityNotFoundException;
import com.hairsalonbookingapp.hairsalon.model.AppointmentRequest;
import com.hairsalonbookingapp.hairsalon.model.AppointmentUpdate;
import com.hairsalonbookingapp.hairsalon.model.StylistInfo;
import com.hairsalonbookingapp.hairsalon.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public List<HairSalonService> getServiceList() {
        List<HairSalonService> list = hairSalonBookingAppService.getAllService();
        if (list != null) {
            return list;
        } else {
            throw new EntityNotFoundException("Service not found!");
        }
    }

    public HairSalonService getService(long serviceId) {
        return serviceRepository.findHairSalonServiceByIdAndStatusTrue(serviceId);
    }

    //CUSTOMER XEM VÀ CHỌN STYLIST
    public List<StylistInfo> getAllStylistInFo() {
        List<StylistInfo> list = authenticationService.getAllStylist();
        return list;
    }

    public AccountForEmployee getStylist(String stylistId) {
        return employeeRepository.findAccountForEmployeeById(stylistId);
    }

    //CUSTOMER CHỌN CA LÀM VIỆC (THỨ 2, 3,...) VÀ SLOT PHÙ HỢP
    public List<ShiftEmployee> getShiftEmployees(String stylistId) {
        return shiftEmployeeService.getShiftsOfEmployee(stylistId);
    }

    public List<Slot> viewAvailableSlots(long shiftEmployeeId) {
        return slotService.getSlots(shiftEmployeeId);
    }

    public Slot getAvailableSlot(long slotId) {
        return slotRepository.findSlotById(slotId);
    }

    //CUSTOMER NHẬP MÃ GIẢM GIÁ (TÙY CHỌN)
    public DiscountCode getDiscountCode(String DiscountCodeId) {
        return discountCodeRepository.findDiscountCodeById(DiscountCodeId);
    }

    //HỆ THỐNG CHỐT
    public Appointment createNewAppointment(AppointmentRequest appointmentRequest) {
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
            return newAppointment;
        } catch (Exception e) {
            throw new EntityNotFoundException("Can not create appointment!");
        }

    }
    //CẬP NHẬT APPOINTMENT -> CUSTOMER LÀM
    // CẬP NHẬT APPOINTMENT CÓ 2 TRƯỜNG HỢP:
    //1. CUSTOMER CHƯA GỬI ĐƠN : TRƯỜNG HỢP NÀY KHÔNG VẤN ĐỀ GÌ, CUSTOMER CÓ THỂ THAO TÁC LẠI CÁC HÀM Ở TRÊN ĐỂ LỰA CHỌN LẠI
    // SAU ĐÓ CHỈNH SỬA ĐẦU VÀO CỦA createNewAppointment LÀ XONG
    //2. CUSTOMER ĐÃ GỬI ĐƠN VÀ MUỐN LẤY LẠI ĐƠN ĐỂ SỬA: CÓ 2 CÁCH GIẢI QUYẾT:
    // A. CUSTOMER HỦY ĐƠN VÀ LÀM LẠI ĐƠN KHÁC
    // B. CUSTOMER LẤY LẠI ĐƠN VÀ UPDATE LẠI THÔNG TIN

    public Appointment updateAppointment(AppointmentUpdate appointmentUpdate, long id) {
        Appointment oldAppointment = appointmentRepository.findAppointmentByIdAndStatusTrue(id);  //TÌM LẠI APPOINTMENT CŨ
        if (oldAppointment != null) {   // TÌM THẤY
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

            Appointment newAppontment = appointmentRepository.save(oldAppointment);     // LƯU LẠI LÊN DB
            return newAppontment;
        } else {
            throw new EntityNotFoundException("Appointment not found!");
        }
    }

    // XÓA APPOINTMENT  -> CUSTOMER LÀM
    public Appointment deleteAppointment(long id){
        Appointment oldAppointment = appointmentRepository.findAppointmentByIdAndStatusTrue(id);  //TÌM LẠI APPOINTMENT CŨ
        if(oldAppointment != null){
            oldAppointment.setStatus(false);
            Appointment newAppontment = appointmentRepository.save(oldAppointment);     // LƯU LẠI LÊN DB
            return newAppontment;
        } else {
            throw new EntityNotFoundException("Appointment not found!");
        }
    }





    


}
