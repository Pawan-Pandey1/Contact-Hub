package com.scm.repositories;

import com.scm.entities.Feedback;
import com.scm.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepo extends JpaRepository<Feedback, String> {
    long countByUser(User user);
}
