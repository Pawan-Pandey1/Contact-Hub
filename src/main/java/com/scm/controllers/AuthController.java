package com.scm.controllers;

import com.scm.entities.User;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.repositories.UserRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepo userRepo;

    @GetMapping("/verify-email")
    public String verifyEmail(
            @RequestParam("token") String token,
            HttpSession session
    ) {
        // Find user by email token
        Optional<User> userOptional = userRepo.findByEmailToken(token);

        if (userOptional.isEmpty()) {
            // Invalid token
            session.setAttribute("message", 
                Message.builder()
                    .type(MessageType.red)
                    .content("Invalid verification token")
                    .build());
            return "redirect:/error";
        }

        User user = userOptional.get();

        if (user.isEmailVerified()) {
            // Already verified
            session.setAttribute("message", 
                Message.builder()
                    .type(MessageType.blue)
                    .content("Email already verified")
                    .build());
            return "redirect:/login";
        }

        // Verify user
        user.setEmailVerified(true);
        user.setEnabled(true);  // Enable account
        user.setEmailToken(null);  // Clear verification token
        userRepo.save(user);

        session.setAttribute("message", 
            Message.builder()
                .type(MessageType.green)
                .content("Email verified successfully! You can now login")
                .build());

        return "redirect:/login";
    }
}
