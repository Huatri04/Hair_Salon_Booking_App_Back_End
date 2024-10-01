package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.ShiftInWeek;
import com.hairsalonbookingapp.hairsalon.exception.DuplicateEntity;
import com.hairsalonbookingapp.hairsalon.exception.EntityNotFoundException;
import com.hairsalonbookingapp.hairsalon.repository.ShiftWeekRepository;
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

    //tạo mới shift
    public ShiftInWeek createWeekShift(ShiftInWeek shiftInWeek){
        try{
            ShiftInWeek newShift = shiftWeekRepository.save(shiftInWeek);
            return newShift;
        } catch (Exception e) {
            throw new DuplicateEntity("Duplicate day!");
        }
    }

    //update shift
    public ShiftInWeek updateShift(ShiftInWeek shiftInWeek, String dayOfWeek){
        ShiftInWeek shift = shiftWeekRepository.findShiftInWeekByDayOfWeekAndStatusTrue(dayOfWeek);
        if(shift != null){
            shift.setStartHour(shiftInWeek.getStartHour());
            shift.setEndHour(shiftInWeek.getEndHour());

            ShiftInWeek newShift = shiftWeekRepository.save(shift);
            return newShift;
        } else {
            throw new EntityNotFoundException("Shift not found!");
        }
    }

    //delete shift
    public ShiftInWeek deleteShift(String dayOfWeek){
        ShiftInWeek shift = shiftWeekRepository.findShiftInWeekByDayOfWeekAndStatusTrue(dayOfWeek);
        if(shift != null){
            shift.setStatus(false);
            ShiftInWeek newShift = shiftWeekRepository.save(shift);
            return newShift;
        } else {
            throw new EntityNotFoundException("Shift not found!");
        }
    }

    //get all shift
    public List<ShiftInWeek> getAllShift(){
        List<ShiftInWeek> list = shiftWeekRepository.findShiftInWeeksByStatusTrue();
        if(list != null){
            return list;
        } else {
            throw new EntityNotFoundException("List not found!");
        }
    }

    //get shift by day
    public ShiftInWeek getShift(String dayOfWeek){
        ShiftInWeek shift = shiftWeekRepository.findShiftInWeekByDayOfWeekAndStatusTrue(dayOfWeek);
        return shift;
    }

    //chia các slot
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

    // chia slot dựa vào giờ bắt đầu và kết thúc
    public List<LocalTime> getSLots(int startHour, int endHour, long duration){
        LocalTime startTime = LocalTime.of(startHour, 0);
        LocalTime endTime = LocalTime.of(endHour, 0);

        Duration interval = Duration.ofMinutes(duration);

        List<LocalTime> intervals = getTimeIntervals(startTime, endTime, interval);
        return intervals;
    }

    }
