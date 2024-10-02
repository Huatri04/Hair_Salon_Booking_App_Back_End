package com.hairsalonbookingapp.hairsalon.repository;

import com.hairsalonbookingapp.hairsalon.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Appointment findAppointmentByIdAndStatusTrue(long id);
}
