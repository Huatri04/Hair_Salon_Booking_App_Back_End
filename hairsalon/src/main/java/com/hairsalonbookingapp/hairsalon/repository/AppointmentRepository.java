package com.hairsalonbookingapp.hairsalon.repository;

import com.hairsalonbookingapp.hairsalon.entity.AccountForCustomer;
import com.hairsalonbookingapp.hairsalon.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Appointment findAppointmentByIdAndStatusAndIsDeletedFalse(long id, String status);
    List<Appointment> findAppointmentsByStatusAndIsDeletedFalse(String status);
    Appointment findAppointmentByIdAndIsDeletedFalse(long id);
    Appointment findAppointmentBySlot_IdAndIsCompletedFalse(long slotID);
    Appointment findAppointmentByIdAndAccountForCustomerAndStatusAndIsDeletedFalse(long id, AccountForCustomer accountForCustomer, String status);
}
