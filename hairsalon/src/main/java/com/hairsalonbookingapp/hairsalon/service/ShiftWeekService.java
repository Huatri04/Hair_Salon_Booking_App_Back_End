package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.ShiftInWeek;
import com.hairsalonbookingapp.hairsalon.exception.DuplicateEntity;
import com.hairsalonbookingapp.hairsalon.exception.EntityNotFoundException;
import com.hairsalonbookingapp.hairsalon.model.ShiftWeekRequest;
import com.hairsalonbookingapp.hairsalon.model.ShiftWeekResponse;
import com.hairsalonbookingapp.hairsalon.model.ShiftWeekUpdate;
import com.hairsalonbookingapp.hairsalon.repository.ShiftWeekRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShiftWeekService {

    @Autowired
    ShiftWeekRepository shiftWeekRepository;

    @Autowired
    ModelMapper modelMapper;

    //tạo mới shift -> MANAGER LÀM
    public ShiftWeekResponse createWeekShift(ShiftWeekRequest shiftWeekRequest){
        try{
            ShiftInWeek checkExistedShift = shiftWeekRepository.findShiftInWeekByDayOfWeekAndIsAvailableTrue(shiftWeekRequest.getDayOfWeek());
            if(checkExistedShift != null){
                throw new DuplicateEntity("Duplicate day!");
            }
            ShiftInWeek newShift = modelMapper.map(shiftWeekRequest, ShiftInWeek.class);
            ShiftInWeek savedShift = shiftWeekRepository.save(newShift);
            return modelMapper.map(savedShift, ShiftWeekResponse.class);
        } catch (Exception e) {
            throw new DuplicateEntity("Duplicate day!");
        }
    }

    //update shift -> MANAGER LÀM
    public ShiftWeekResponse updateShift(ShiftWeekUpdate shiftWeekUpdate, String dayOfWeek){
        ShiftInWeek shift = shiftWeekRepository.findShiftInWeekByDayOfWeekAndIsAvailableTrue(dayOfWeek);
        if(shift != null){
            shift.setStartHour(shiftWeekUpdate.getStartHour());
            shift.setEndHour(shiftWeekUpdate.getEndHour());

            ShiftInWeek newShift = shiftWeekRepository.save(shift);
            return modelMapper.map(newShift, ShiftWeekResponse.class);
        } else {
            throw new EntityNotFoundException("Shift not found!");
        }
    }

    //delete shift -> MANAGER LÀM
    public ShiftWeekResponse deleteShift(String dayOfWeek){
        ShiftInWeek shift = shiftWeekRepository.findShiftInWeekByDayOfWeekAndIsAvailableTrue(dayOfWeek);
        if(shift != null){
            shift.setAvailable(false);
            ShiftInWeek newShift = shiftWeekRepository.save(shift);
            return modelMapper.map(newShift, ShiftWeekResponse.class);
        } else {
            throw new EntityNotFoundException("Shift not found!");
        }
    }

    //get all shift -> MANAGER LÀM
    public List<ShiftWeekResponse> getAllShift(){
        List<ShiftInWeek> list = shiftWeekRepository.findAll();
        if(list != null){
            List<ShiftWeekResponse> shiftWeekResponseList = new ArrayList<>();
            for(ShiftInWeek shiftInWeek : list){
                ShiftWeekResponse shiftWeekResponse = modelMapper.map(shiftInWeek, ShiftWeekResponse.class);
                shiftWeekResponseList.add(shiftWeekResponse);
            }
            return shiftWeekResponseList;
        } else {
            throw new EntityNotFoundException("Shift not found!");
        }
    }

    /*//get shift by day
    public ShiftInWeek getShift(String dayOfWeek){
        ShiftInWeek shift = shiftWeekRepository.findShiftInWeekByDayOfWeekAndStatusTrue(dayOfWeek);
        return shift;
    }*/

    //chia các slot -> HỖ TRỢ HÀM DƯỚI
    public List<LocalTime> getTimeIntervals(LocalTime startTime, LocalTime endTime, Duration interval) {
        List<LocalTime> timeIntervals = new ArrayList<>();

        // Bắt đầu từ startTime và thêm vào danh sách các khoảng thời gian đều nhau
        LocalTime currentTime = startTime;
        while (!currentTime.isAfter(endTime)) {
            timeIntervals.add(currentTime);
            currentTime = currentTime.plus(interval); // Tăng thời gian lên theo khoảng thời gian interval
        }

        return timeIntervals;
    }

    // chia slot dựa vào giờ bắt đầu và kết thúc -> HÀM NÀY GIÚP SLOT SERVICE CHIA SLOT
    public List<LocalTime> getSLots(int startHour, int endHour, long duration){
        LocalTime startTime = LocalTime.of(startHour, 0);
        LocalTime endTime = LocalTime.of(endHour, 0);

        Duration interval = Duration.ofMinutes(duration);

        List<LocalTime> intervals = getTimeIntervals(startTime, endTime, interval);
        return intervals;
    }

    //RESTART SHIFT -> MANAGER LÀM
    public ShiftWeekResponse restartShift(String dayOfWeek){
        ShiftInWeek shift = shiftWeekRepository.findShiftInWeekByDayOfWeekAndIsAvailableFalse(dayOfWeek);
        if(shift != null){
            shift.setAvailable(true);
            ShiftInWeek newShift = shiftWeekRepository.save(shift);
            return modelMapper.map(newShift, ShiftWeekResponse.class);
        } else {
            throw new EntityNotFoundException("Shift not found!");
        }
    }

}
