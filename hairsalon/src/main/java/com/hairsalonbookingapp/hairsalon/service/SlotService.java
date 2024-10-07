package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.*;
import com.hairsalonbookingapp.hairsalon.exception.DuplicateEntity;
import com.hairsalonbookingapp.hairsalon.exception.EntityNotFoundException;
import com.hairsalonbookingapp.hairsalon.model.SlotRequest;
import com.hairsalonbookingapp.hairsalon.model.SlotResponse;
import com.hairsalonbookingapp.hairsalon.repository.AppointmentRepository;
import com.hairsalonbookingapp.hairsalon.repository.EmployeeRepository;
import com.hairsalonbookingapp.hairsalon.repository.ShiftEmployeeRepository;
import com.hairsalonbookingapp.hairsalon.repository.SlotRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SlotService {

    @Autowired
    SlotRepository slotRepository;

    @Autowired
    ShiftEmployeeRepository shiftEmployeeRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ShiftWeekService shiftWeekService;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    AppointmentRepository appointmentRepository;

    /*try{
                //Chia khung thời gian thành các khoảng nhỏ
                ShiftEmployee shiftEmployee = shiftEmployeeRepository.findShiftEmployeeById(shiftEmployee_Id); // lấy shift employee
                ShiftInWeek shiftInWeek = shiftEmployee.getShiftInWeek(); // lấy shift week
                String startHour = shiftInWeek.getStartHour(); // lấy start hour   // 08:00
                String endHour = shiftInWeek.getEndHour(); // lấy end hour    // 12:00
                String partOfStartHour = startHour.substring(0,2); // lấy 08
                String partOfEndHour = endHour.substring(0,2); // lấy 12
                int startTime = Integer.parseInt(partOfStartHour); // chuyển sang int
                int endTime = Integer.parseInt(partOfEndHour); // chuyển sang int
                int duration = 60; // thời gian 1 slot
                List<LocalTime> intervals = shiftWeekService.getTimes(startTime, endTime, duration);

                for(long i = 1; i <= intervals.size(); i++){
                    long slot_id = i;
                    boolean status = true;
                    Slot slot = modelMapper.map(status, Slot.class);
                    slot.setShiftEmployee(shiftEmployee);
                }
                return slotRepository.save(slot);
            } catch (Exception e) {
                throw new DuplicateEntity("");
            }*/



    //tạo slot -> STYLIST LÀM
    public List<SlotResponse> createSlots(SlotRequest slotRequest){
        //   MỖI CA(SHIFT) CỦA 1 STYLIST NHẤT ĐỊNH SẼ CÓ SỐ SLOT NHẤT ĐỊNH
        List<Slot> list = new ArrayList<>();
        List<LocalTime> localTimeList = shiftWeekService.getSLots(slotRequest.getStartHour(), slotRequest.getEndHour(), slotRequest.getDuration());
        for(LocalTime time : localTimeList){
            if(time.equals(localTimeList.get(localTimeList.size() - 1))){
                break;        // DỪNG NẾU TIME = ENDHOUR
            } else {
                Slot slot = new Slot();
                slot.setStartSlot(time.toString());
                slot.setShiftEmployee(shiftEmployeeRepository.findShiftEmployeeById(slotRequest.getShiftEmployeeId()));
                Slot newSlot = slotRepository.save(slot);  // TRƯỚC KHI KẾT THÚC VÒNG LẶP SẼ LƯU XUỐNG DB, SAU ĐÓ THÊM VÀO LIST
                list.add(newSlot);
            }
        }
        //GENERATE LIST RESPONSE
        List<SlotResponse> responseList = new ArrayList<>();
        for(Slot slot : list){
            SlotResponse slotResponse = modelMapper.map(slot, SlotResponse.class);
            slotResponse.setShiftEmployeeId(slot.getShiftEmployee().getId());
            responseList.add(slotResponse);
        }
        return responseList;
    }

    // xem slot trong ngày dựa trên shiftEmployeeId -> STYLIST LÀM
    public List<SlotResponse> getAllSlotsInDay(long shiftEmployeeId){
        List<Slot> slots = slotRepository.findSlotsByShiftEmployee_Id(shiftEmployeeId);
        if(slots != null){

            //GENERATE LIST RESPONSE
            List<SlotResponse> responseList = new ArrayList<>();
            for(Slot slot : slots){
                SlotResponse slotResponse = modelMapper.map(slot, SlotResponse.class);
                slotResponse.setShiftEmployeeId(slot.getShiftEmployee().getId());
                responseList.add(slotResponse);
            }
            return responseList;
        } else {
            throw new EntityNotFoundException("Slots not found!");
        }
    }

    // GET ALL SLOT -> STYLIST LÀM
    public List<SlotResponse> getAllSlots(){
        List<Slot> slots = slotRepository.findSlotsByShiftEmployee_AccountForEmployee_Id(authenticationService.getCurrentAccountForEmployee().getId());
        if(slots != null){

            //GENERATE LIST RESPONSE
            List<SlotResponse> responseList = new ArrayList<>();
            for(Slot slot : slots){
                SlotResponse slotResponse = modelMapper.map(slot, SlotResponse.class);
                slotResponse.setShiftEmployeeId(slot.getShiftEmployee().getId());
                responseList.add(slotResponse);
            }
            return responseList;
        } else {
            throw new EntityNotFoundException("Slots not found!");
        }
    }

    // xem slot trong ngày dựa trên shiftEmployeeId -> CUSTOMER LÀM -> DÙNG CHO APPOINTMENT SERVICE
    public List<Slot> getSlots(long shiftEmployeeId){
        List<Slot> slots = slotRepository.findSlotsByShiftEmployee_IdAndIsAvailableTrue(shiftEmployeeId);
        if(slots != null){
            return slots;
        } else {
            throw new EntityNotFoundException("Slots not found!");
        }
    }

    //xóa slot
    //CÓ 2 TRƯỜNG HỢP XẢY RA:
    //1. TRƯỚC: STYLIST BẬN, NÓ THÔNG BÁO MANAGER VÀ ĐC CHẤP THUẬN, STYLIST TẠM THỜI GỠ SLOT ĐÓ RA ĐỂ KHÁCH KHÔNG CHỌN
    //2. SAU: KHÁCH ĐẶT LỊCH HẸN TẠI SLOT NÀY, APPOINTMENT SERVICE TỰ FALSE SLOT ĐÓ
    public SlotResponse deleteSLot(long slotId){
        Slot slot = slotRepository.findSlotById(slotId);
        if(slot != null){
            slot.setAvailable(false);
            Slot newSlot = slotRepository.save(slot);
            SlotResponse slotResponse = modelMapper.map(newSlot, SlotResponse.class);
            slotResponse.setShiftEmployeeId(newSlot.getShiftEmployee().getId());
            return slotResponse;
        } else {
            throw new EntityNotFoundException("Slot not found!");
        }
    }


    //COMPLETE SLOT -> STAFF LÀM SAU KHI XONG 1 SLOT
    public SlotResponse completeSlot(long slotId){
        Slot slot = slotRepository.findSlotById(slotId);
        if(slot != null){
            slot.setCompleted(true);
            AccountForEmployee account = slot.getShiftEmployee().getAccountForEmployee();
            account.setCompletedSlot(account.getCompletedSlot() + 1);     // CẬP NHẬT VÀO SLOT ĐỂ TÍNH KPI
            AccountForEmployee updatedAccount = employeeRepository.save(account);

            slot.setAvailable(true);
            Slot newSlot = slotRepository.save(slot);
            SlotResponse slotResponse = modelMapper.map(newSlot, SlotResponse.class);
            slotResponse.setShiftEmployeeId(newSlot.getShiftEmployee().getId());
            return slotResponse;
        } else {
            throw new EntityNotFoundException("Slot not found!");
        }
    }

    // CHUYỂN TẤT CẢ COMPLETE VỀ FALSE -> MANAGER LÀM VÀO THỨ 7
    public String resetAllSlots(){
        List<Slot> slots = slotRepository.findAll();
        for(Slot slot : slots){
            slot.setAvailable(true);
            slot.setCompleted(false);
            Slot updatedSlot = slotRepository.save(slot);
        }
        String message = "Reset complete!";
        return message;
    }

    // STAFF XEM SLOT CỦA STYLIST ĐỂ XÁC NHẬN COMPLETE
    /*public SlotResponse viewSlotInfo(String stylistID, String day, String startSlot){
        Slot slot = slotRepository.findSlotByStartSlotAndShiftEmployee_AccountForEmployee_IdAndShiftEmployee_ShiftInWeek_DayOfWeek(startSlot, stylistID, day);
        if(slot != null){
            SlotResponse slotResponse = modelMapper.map(slot, SlotResponse.class);
            slotResponse.setShiftEmployeeId(slot.getShiftEmployee().getId());
            return slotResponse;
        } else {
            throw new EntityNotFoundException("Slot not found!");
        }
    }*/

    public SlotResponse viewSlotInfo(long appointmentID) {
        Appointment appointment = appointmentRepository.findAppointmentByIdAndIsDeletedFalse(appointmentID);
        if (appointment != null) {
            Slot slot = appointment.getSlot();
            SlotResponse slotResponse = modelMapper.map(slot, SlotResponse.class);
            slotResponse.setShiftEmployeeId(slot.getShiftEmployee().getId());
            return slotResponse;
        } else {
            throw new EntityNotFoundException("Appointment not found!");
        }
    }

    // CUSTOMER XEM CÁC SLOT PHÙ HỢP
    public List<SlotResponse> viewAvailableSlots(long shiftEmployeeId) {     // XEM CÁC SLOT KHẢ DỤNG CỦA CA
        List<Slot> slotList = getSlots(shiftEmployeeId);
        List<SlotResponse> slotResponseList = new ArrayList<>();
        for(Slot slot : slotList){
            SlotResponse slotResponse = modelMapper.map(slot, SlotResponse.class);
            slotResponse.setShiftEmployeeId(shiftEmployeeId);
            slotResponseList.add(slotResponse);
        }
        return slotResponseList;
    }


}
