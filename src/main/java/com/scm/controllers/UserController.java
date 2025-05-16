package com.scm.controllers;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scm.services.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    
    // user dashboard page
    @RequestMapping(value = "/dashboard")
    public String userDashboard() {
        logger.info("User dashboard accessed");
        return "user/dashboard";
    }

    // user profile page
    @RequestMapping(value = "/profile")
    public String userProfile(Model model,Authentication authentication) {
        
        return "user/profile";
    }

    // Feedback page
    @GetMapping("/feedback")
    public String feedbackPage() {
        logger.info("Feedback page accessed");
        return "user/feedback"; // Ensure this maps to templates/user/feedback.html
    }
}


