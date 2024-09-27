package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.AccountForCustomer;
import com.hairsalonbookingapp.hairsalon.entity.AccountForEmployee;
import com.hairsalonbookingapp.hairsalon.entity.Feedback;
import com.hairsalonbookingapp.hairsalon.exception.Duplicate;
import com.hairsalonbookingapp.hairsalon.model.FeedbackResponse;
import com.hairsalonbookingapp.hairsalon.model.RequestFeedback;
import com.hairsalonbookingapp.hairsalon.repository.FeedbackRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {
    @Autowired
    FeedbackRepository feedbackRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    ModelMapper modelMapper;
    // create feedback
    public FeedbackResponse createFeedback(RequestFeedback requestFeedback){
        Feedback feedback = modelMapper.map(requestFeedback, Feedback.class);
        try{
            String newId = generateId();
            feedback.setFeedbackId(newId);
            AccountForCustomer accountForCustomer = authenticationService.getCurrentAccountForCustomer();
            if(accountForCustomer == null){
                throw new Duplicate("No current customer found.");
            }
            feedback.setCustomer(accountForCustomer);
            Feedback newFeedback = feedbackRepository.save(feedback);
            return modelMapper.map(newFeedback, FeedbackResponse.class);
        } catch (Exception e) {
            if(e.getMessage().contains(feedback.getStar() + "")){
                throw new Duplicate("duplicate start! ");
            } else if (e.getMessage().contains(feedback.getFeedbackId())) {
                throw new Duplicate("duplicate feedback id! ");
            } else if (e.getMessage().contains(feedback.getCustomer() + "")) {
                throw new Duplicate("duplicate Customer! ");
            }
        }
        return null;
    }

    public String generateId() {
        // Tìm ID cuối cùng theo vai trò
        Optional<Feedback> lastFeedback = feedbackRepository.findTopByOrderByFeedbackIdDesc();
        int newIdNumber = 1; // Mặc định bắt đầu từ 1

        // Nếu có tài khoản cuối cùng, lấy ID
        if (lastFeedback.isPresent()) {
            String lastId = lastFeedback.get().getFeedbackId();
            newIdNumber = Integer.parseInt(lastId.replaceAll("\\D+", "")) + 1; // Tăng số lên 1
        }


        String prefix = "FB";

        return String.format("%s%06d", prefix, newIdNumber); // Tạo ID mới với format
    }


    //delete feedback
    public FeedbackResponse deleteFeedback(String feedbackId){
        // tim toi id ma FE cung cap
        Feedback feedbackNeedDelete = feedbackRepository.findFeedbackByFeedbackId(feedbackId);
        if(feedbackNeedDelete == null){
            throw new Duplicate("Feedback not found!"); // dung tai day
        }

        feedbackNeedDelete.setDeleted(true);
        Feedback deletedFeedback = feedbackRepository.save(feedbackNeedDelete);
        return modelMapper.map(deletedFeedback, FeedbackResponse.class);
    }

    // show list of feedback
    public List<Feedback> getAllFeedback(){
        List<Feedback> feedbacks = feedbackRepository.findFeedbacksByIsDeletedFalse();
        return feedbacks;
    }

}
