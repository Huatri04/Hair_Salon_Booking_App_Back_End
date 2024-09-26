package com.hairsalonbookingapp.hairsalon.repository;

import com.hairsalonbookingapp.hairsalon.entity.AccountForEmployee;
import com.hairsalonbookingapp.hairsalon.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, String> {
    Optional<Feedback> findTopByOrderByFeedbackIdDesc();
    Feedback findFeedbackByFeedbackId(String feedbackId);
    List<Feedback> findFeedbacksByIsDeletedFalse();
}
