package com.scm.services;

import com.scm.entities.Feedback;
import com.scm.entities.User;

public interface FeedbackService {
    Feedback saveFeedback(Feedback feedback);
    long countByUser(User user);
}
