package com.scm.config;

import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AuthFailureHandler implements AuthenticationFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(AuthFailureHandler.class);

    @Override
    public void onAuthenticationFailure(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException exception
    ) throws IOException, ServletException {
        HttpSession session = request.getSession();
        String errorMessage = "Invalid credentials";

        // Check if the exception is due to a disabled (unverified) account
        if (exception instanceof DisabledException) {
            errorMessage = "Account not verified. Please check your email for the verification link.";
            logger.warn("Login attempt for unverified account: {}", request.getParameter("email"));
        } else {
            logger.error("Authentication failed: {}", exception.getMessage());
        }

        session.setAttribute("message", 
            Message.builder()
                .type(MessageType.red)
                .content(errorMessage)
                .build());
        
        response.sendRedirect("/login?error");
    }
}
