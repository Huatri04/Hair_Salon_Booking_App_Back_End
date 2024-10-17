package com.hairsalonbookingapp.hairsalon.repository;

import com.hairsalonbookingapp.hairsalon.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    /*List<Slot> findSlotsByShiftEmployee_Id(long shiftEmployeeId);
    Slot findSlotById(long id);

    List<Slot> findSlotsByShiftEmployee_IdAndIsAvailableTrue(long shiftEmployeeId);
    List<Slot> findSlotsByShiftEmployee_AccountForEmployee_Id(String stylistId);
    Slot findSlotByStartSlotAndShiftEmployee_AccountForEmployee_IdAndShiftEmployee_ShiftInWeek_DayOfWeek(String startSlot, String stylistID, String dayOfWeek);
    List<Slot> findSlotsByShiftEmployee_AccountForEmployee_IdAndShiftEmployee_Id(String stylistId, long shiftId);
    Slot findSlotByShiftEmployee_AccountForEmployee_IdAndId(String stylistId, long slotId);*/
    List<Slot> findSlotsByDateAndStartSlotAndIsAvailableTrue(String date, String startSlot);
    Slot findSlotByIdAndIsAvailableTrue(long id);
    Slot findSlotByIdAndIsAvailableFalse(long id);
    Slot findSlotByStartSlotAndShiftEmployee_AccountForEmployee_IdAndDate(String startSlot, String stylistID, String date);
    Slot findSlotById(long id);
    List<Slot> findSlotsByShiftEmployee_AccountForEmployee_IdAndDate(String stylistId, String date);
    Slot findSlotByStartSlotAndDateAndShiftEmployee_AccountForEmployee_IdAndIsAvailableTrue(String startSlot, String date, String stylistId);
}
