package com.scm.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scm.entities.Feedback;
import com.scm.entities.User;
import com.scm.forms.FeedbackForm;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.ContactService;
import com.scm.services.FeedbackService;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ContactService contactService;
    
    @Autowired
    private FeedbackService feedbackService;

    // User dashboard page
    @GetMapping("/dashboard")
public String userDashboard(Model model, Authentication authentication) {
    try {
        String email = null;
        Object principal = authentication.getPrincipal();

        // Handle OAuth2 (Google) login
        if (principal instanceof OAuth2User) {
            email = ((OAuth2User) principal).getAttribute("email");
        } else {
            // Standard login
            email = authentication.getName();
        }

        logger.info("Logged-in user email: {}", email);

        User user = userService.getUserByEmail(email);
        if (user == null) {
            logger.error("User not found for email: {}", email);
            return "redirect:/login?error=user_not_found";
        }

        // Fetch statistics
        long totalContacts = contactService.countByUser(user);
        long favoriteContacts = contactService.countFavoriteByUser(user);
        long totalFeedbacks = feedbackService.countByUser(user);

        // Add to model
        model.addAttribute("loggedInUser", user);
        model.addAttribute("totalContacts", totalContacts);
        model.addAttribute("favoriteContacts", favoriteContacts);
        model.addAttribute("totalFeedbacks", totalFeedbacks);

        return "user/dashboard";
    } catch (Exception e) {
        logger.error("Error in dashboard controller:", e);
        throw e;
    }
}

    // User profile page
    @RequestMapping("/profile")
    public String userProfile(Model model, Authentication authentication) {
        // Add profile data if needed
        return "user/profile";
    }

    // Feedback Page - GET
    @GetMapping("/feedback")
    public String feedbackPage(Model model) {
        logger.info("Feedback page accessed");
        model.addAttribute("feedbackForm", new FeedbackForm());
        return "user/feedback";
    }

    // Feedback Submission - POST
    @PostMapping("/feedback")
    public String submitFeedback(
            @ModelAttribute FeedbackForm feedbackForm,
            Authentication authentication,
            HttpSession session) {

        // Get logged-in user
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        // Create and save feedback
        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setRating(feedbackForm.getRating());
        feedback.setMessage(feedbackForm.getMessage());
        feedbackService.saveFeedback(feedback);

        // Success message
        session.setAttribute("message", Message.builder()
                .content("Thank you for your feedback!")
                .type(MessageType.green)
                .build());

        return "redirect:/user/feedback";
    }
}
