package com.scm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.scm.entities.Feedback;
import com.scm.entities.User;
import com.scm.repositories.FeedbackRepo;
import com.scm.services.FeedbackService;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    @Autowired
    private FeedbackRepo feedbackRepo;

    @Override
    public Feedback saveFeedback(Feedback feedback) {
        return feedbackRepo.save(feedback);
    }

    @Override
    public long countByUser(User user) {
        return feedbackRepo.countByUser(user);
    }
}
