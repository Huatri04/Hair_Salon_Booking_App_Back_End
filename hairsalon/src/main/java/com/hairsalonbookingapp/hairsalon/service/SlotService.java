package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.ShiftEmployee;
import com.hairsalonbookingapp.hairsalon.entity.ShiftInWeek;
import com.hairsalonbookingapp.hairsalon.entity.Slot;
import com.hairsalonbookingapp.hairsalon.exception.DuplicateEntity;
import com.hairsalonbookingapp.hairsalon.exception.EntityNotFoundException;
import com.hairsalonbookingapp.hairsalon.model.SlotRequest;
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
    public List<Slot> createSlots(SlotRequest slotRequest){
        //   MỖI CA(SHIFT) CỦA 1 STYLIST NHẤT ĐỊNH SẼ CÓ SỐ SLOT NHẤT ĐỊNH
        List<Slot> list = new ArrayList<>();
        for(LocalTime time : shiftWeekService.getSLots(slotRequest.getStartHour(), slotRequest.getEndHour(), slotRequest.getDuration())){
            Slot slot = new Slot();
            slot.setStartSlot(time.toString());
            slot.setStatus(true);
            slot.setShiftEmployee(shiftEmployeeRepository.findShiftEmployeeById(slotRequest.getShiftEmployeeId()));
            Slot newSlot = slotRepository.save(slot);  // TRƯỚC KHI KẾT THÚC VÒNG LẶP SẼ LƯU XUỐNG DB, SAU ĐÓ THÊM VÀO LIST
            list.add(newSlot);
        }
        return list;
    }

    // xem slot trong ngày dựa trên shiftEmployeeId -> STYLIST LÀM
    public List<Slot> getAllSlots(long shiftEmployeeId){
        List<Slot> slots = slotRepository.findSlotsByShiftEmployeeId(shiftEmployeeId);
        if(slots != null){
            return slots;
        } else {
            throw new EntityNotFoundException("Slots not found!");
        }
    }

    // xem slot trong ngày dựa trên shiftEmployeeId -> CUSTOMER LÀM
    public List<Slot> getSlots(long shiftEmployeeId){
        List<Slot> slots = slotRepository.findSlotsByShiftEmployeeIdAndStatusTrue(shiftEmployeeId);
        if(slots != null){
            return slots;
        } else {
            throw new EntityNotFoundException("Slots not found!");
        }
    }

    //xóa slot -> STYLIST LÀM
    public Slot deleteSLot(long slotId){
        Slot slot = slotRepository.findSlotById(slotId);
        if(slot != null){
            slot.setStatus(false);
            return slotRepository.save(slot);
        } else {
            throw new EntityNotFoundException("Slot not found!");
        }
    }

    //cập nhật status slot về true - đã hoàn thành -> STYLIST LÀM
    public Slot updateSlot(long slotId){
        Slot slot = slotRepository.findSlotById(slotId);
        if(slot != null){
            slot.setStatus(true);
            return slotRepository.save(slot);
        } else {
            throw new EntityNotFoundException("Slot not found!");
        }
    }


}
