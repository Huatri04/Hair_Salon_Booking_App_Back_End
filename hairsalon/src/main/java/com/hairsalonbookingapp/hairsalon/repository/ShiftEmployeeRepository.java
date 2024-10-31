package com.hairsalonbookingapp.hairsalon.repository;

import com.hairsalonbookingapp.hairsalon.entity.AccountForEmployee;
import com.hairsalonbookingapp.hairsalon.entity.ShiftEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShiftEmployeeRepository extends JpaRepository<ShiftEmployee, Long> {
    ShiftEmployee findShiftEmployeeById(long id);
    /*List<ShiftEmployee> findShiftEmployeesByAccountForEmployee_IdAndIsAvailableTrue(String employeeId);
    List<ShiftEmployee> findShiftEmployeesByAccountForEmployee_Id(String employeeId);
    List<ShiftEmployee> findShiftEmployeesByShiftInWeek_DayOfWeekAndIsAvailableTrue(String dayOfWeek);
    ShiftEmployee findShiftEmployeeByShiftInWeek_DayOfWeekAndNameAndIsAvailableTrue(String dayOfWeek, String name);
    ShiftEmployee findShiftEmployeeByShiftInWeek_DayOfWeekAndName(String dayOfWeek, String name);*/
    List<ShiftEmployee> findShiftEmployeesByDateAndIsAvailableTrue(String date);
    ShiftEmployee findShiftEmployeeByAccountForEmployeeAndDateAndIsAvailableTrue(AccountForEmployee accountForEmployee, String date);
}
