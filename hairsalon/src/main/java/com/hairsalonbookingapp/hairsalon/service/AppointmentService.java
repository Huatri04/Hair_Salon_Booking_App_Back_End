package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.*;
import com.hairsalonbookingapp.hairsalon.exception.EntityNotFoundException;
import com.hairsalonbookingapp.hairsalon.model.StylistInfo;
import com.hairsalonbookingapp.hairsalon.repository.AppointmentRepository;
import com.hairsalonbookingapp.hairsalon.repository.EmployeeRepository;
import com.hairsalonbookingapp.hairsalon.repository.ServiceRepository;
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

    //CUSTOMER XEM VÀ CHỌN DỊCH VỤ
    public List<HairSalonService> getServiceList(){
        List<HairSalonService> list = hairSalonBookingAppService.getAllService();
        if(list != null) {
            return list;
        } else {
            throw new EntityNotFoundException("Service not found!");
        }
    }

    public HairSalonService getService(long serviceId){
        return serviceRepository.findHairSalonServiceByIdAndStatusTrue(serviceId);
    }

    //CUSTOMER XEM VÀ CHỌN STYLIST
    public List<StylistInfo> getAllStylistInFo(){
        List<StylistInfo> list = authenticationService.getAllStylist();
        return list;
    }

    public AccountForEmployee getStylist(String stylistId){
        return employeeRepository.findAccountForEmployeeById(stylistId);
    }
    //CUSTOMER CHỌN CA LÀM VIỆC (THỨ 2, 3,...) VÀ SLOT PHÙ HỢP
    public List<ShiftEmployee> getShiftEmployees(String stylistId){
        return shiftEmployeeService.getShiftsOfEmployee(stylistId);
    }

    public List<Slot> getAvailableSlots(long shiftEmployeeId){
        return slotService.getSlots(shiftEmployeeId);
    }
    //CUSTOMER NHẬP MÃ GIẢM GIÁ (TÙY CHỌN)
    public String getDiscountCode(String code){
        return code;
    }
    //HỆ THỐNG CHỐT
    


}
