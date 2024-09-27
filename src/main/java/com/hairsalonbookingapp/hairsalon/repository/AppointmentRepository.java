package com.hairsalonbookingapp.hairsalon.repository;

import com.hairsalonbookingapp.hairsalon.entity.Appointment;
import com.hairsalonbookingapp.hairsalon.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, String> {
    Optional<Appointment> findTopByOrderByAppointmentIdDesc();
    Appointment findFeedbackByAppointmentId(String feedbackId);
    List<Appointment> findAppointmentsByIsDeletedFalse();
}
