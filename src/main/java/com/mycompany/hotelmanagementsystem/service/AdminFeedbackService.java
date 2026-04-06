package com.mycompany.hotelmanagementsystem.service;

<<<<<<< HEAD
import com.mycompany.hotelmanagementsystem.entity.Feedback;
import com.mycompany.hotelmanagementsystem.dal.FeedbackRepository;
=======
import com.mycompany.hotelmanagementsystem.model.Feedback;
import com.mycompany.hotelmanagementsystem.dao.FeedbackRepository;
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
import java.util.List;

public class AdminFeedbackService {
    private final FeedbackRepository feedbackRepository;

    public AdminFeedbackService() {
        this.feedbackRepository = new FeedbackRepository();
    }

    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAllWithDetails();
    }

<<<<<<< HEAD
=======
    public List<Feedback> getVisibleFeedback(int limit) {
        return feedbackRepository.findVisibleWithDetails(limit);
    }

>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
    public boolean toggleVisibility(int feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId);
        if (feedback == null) return false;
        return feedbackRepository.updateIsHidden(feedbackId, !feedback.isHidden()) > 0;
    }

    public boolean replyToFeedback(int feedbackId, int adminId, String reply) {
        return feedbackRepository.upsertReply(feedbackId, adminId, reply) > 0;
    }
}
