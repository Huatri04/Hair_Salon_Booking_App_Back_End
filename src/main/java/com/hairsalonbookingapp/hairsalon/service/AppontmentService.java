package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.Appointment;
import com.hairsalonbookingapp.hairsalon.entity.Feedback;
import com.hairsalonbookingapp.hairsalon.exception.Duplicate;
import com.hairsalonbookingapp.hairsalon.model.AppointmentResponse;
import com.hairsalonbookingapp.hairsalon.model.FeedbackResponse;
import com.hairsalonbookingapp.hairsalon.model.RequestAppointment;
import com.hairsalonbookingapp.hairsalon.model.RequestFeedback;
import com.hairsalonbookingapp.hairsalon.repository.AppointmentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppontmentService {
    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    @Lazy
    ModelMapper modelMapper;
    // create feedback
    public AppointmentResponse createAppointment(RequestAppointment requestAppointment){
        Appointment appointment = modelMapper.map(requestAppointment, Appointment.class);
        try{
            String newId = generateId();
            appointment.setAppointmentId(newId);
            Appointment newAppointment = appointmentRepository.save(appointment);
            return modelMapper.map(newAppointment, AppointmentResponse.class);
        } catch (Exception e) {
            if(e.getMessage().contains(appointment.getAppointmentId())){
                throw new Duplicate("duplicate Appointment id! ");
            }
        }
        return null;
    }

    public String generateId() {
        // Tìm ID cuối cùng theo vai trò
        Optional<Appointment> lastAppointment = appointmentRepository.findTopByOrderByAppointmentIdDesc();
        int newIdNumber = 1; // Mặc định bắt đầu từ 1

        // Nếu có tài khoản cuối cùng, lấy ID
        if (lastAppointment.isPresent()) {
            String lastId = lastAppointment.get().getAppointmentId();
            newIdNumber = Integer.parseInt(lastId.replaceAll("\\D+", "")) + 1; // Tăng số lên 1
        }


        String prefix = "AP";

        return String.format("%s%06d", prefix, newIdNumber); // Tạo ID mới với format
    }


    //delete feedback
    public AppointmentResponse deleteAppointment(String appointmentId){
        // tim toi id ma FE cung cap
        Appointment appointmentNeedDelete = appointmentRepository.findFeedbackByAppointmentId(appointmentId);
        if(appointmentNeedDelete == null){
            throw new Duplicate("Appointment not found!"); // dung tai day
        }

        appointmentNeedDelete.setDeleted(true);
        Appointment deletedAppointment = appointmentRepository.save(appointmentNeedDelete);
        return modelMapper.map(deletedAppointment, AppointmentResponse.class);
    }

    // show list of feedback
    public List<Appointment> getAllAppointment(){
        List<Appointment> appointments = appointmentRepository.findAppointmentsByIsDeletedFalse();
        return appointments;
    }
}
