package com.scm.config;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.scm.entities.Providers;
import com.scm.entities.User;
import com.scm.helpers.AppConstants;
import com.scm.repositories.UserRepo;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final Logger logger = LoggerFactory.getLogger(OAuthAuthenticationSuccessHandler.class);

    @Autowired
    private UserRepo userRepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        logger.info("OAuthAuthenticationSuccessHandler triggered");

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String provider = oauthToken.getAuthorizedClientRegistrationId();
        logger.info("Provider: {}", provider);

        DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
        logger.debug("OAuth2 User Attributes:");
        oauthUser.getAttributes().forEach((key, value) ->
                logger.debug("{} : {}", key, value)
        );

        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setRoleList(List.of(AppConstants.ROLE_USER));
        user.setEmailVerified(true);
        user.setEnabled(true);
        user.setPassword("oauth-dummy-password"); // Security: Consider random password

        if ("google".equalsIgnoreCase(provider)) {
            // Google attributes
            user.setEmail(oauthUser.getAttribute("email").toString());
            user.setProfilePic(oauthUser.getAttribute("picture").toString());
            user.setName(oauthUser.getAttribute("name").toString());
            user.setProviderUserId(oauthUser.getName());
            user.setProvider(Providers.GOOGLE);
            user.setAbout("This account was created using Google");
        } else if ("github".equalsIgnoreCase(provider)) {
            // Github attributes
            String email = oauthUser.getAttribute("email") != null
                    ? oauthUser.getAttribute("email").toString()
                    : oauthUser.getAttribute("login").toString() + "@github.com";
            user.setEmail(email);
            user.setProfilePic(oauthUser.getAttribute("avatar_url").toString());
            user.setName(oauthUser.getAttribute("login").toString());
            user.setProviderUserId(oauthUser.getName());
            user.setProvider(Providers.GITHUB);
            user.setAbout("This account was created using GitHub");
        } else if ("linkedin".equalsIgnoreCase(provider)) {
            // Implement LinkedIn handling if needed
        } else {
            logger.warn("Unsupported OAuth provider: {}", provider);
        }

        // Save the user if not already present
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            User existingUser = userRepo.findByEmail(user.getEmail()).orElse(null);
            if (existingUser == null) {
                userRepo.save(user);
                logger.info("New OAuth user saved: {}", user.getEmail());
            } else {
                logger.info("User already exists: {}", existingUser.getEmail());
            }
        }

        new DefaultRedirectStrategy().sendRedirect(request, response, "/user/profile");
    }
}
