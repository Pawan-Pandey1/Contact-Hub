package com.scm.config;

import com.scm.services.impl.SecurityCustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
public class SecurityConfig {

    @Autowired
    private SecurityCustomUserDetailService userDetailService;

    @Autowired
    private OAuthAuthenticationSuccessHandler  handler;

    @Autowired
    private AuthenticationFailureHandler authFailureHandler;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailService);
        provider.setPasswordEncoder(passwordEncoder());
        
        // Block unverified users from logging in
        provider.setPreAuthenticationChecks(user -> {
            if (!((UserDetails) user).isEnabled()) {
                throw new DisabledException("Account not verified");
            }
        });
        
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> {
            authorize.requestMatchers(
                "/user/**"       // Private endpoints
            ).authenticated();

            authorize.requestMatchers(
                "/", 
                "/register", 
                "/login", 
                "/auth/verify-email",  // Email verification endpoint
                "/css/**", 
                "/js/**"               // Static resources
            ).permitAll();

            authorize.anyRequest().permitAll();
        });

        // Form login configuration
        http.formLogin(formLogin -> {
            formLogin.loginPage("/login")
                    .loginProcessingUrl("/authenticate")
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .successForwardUrl("/user/profile")
                    .failureHandler(authFailureHandler);
        });

        // OAuth2 login configuration
        http.oauth2Login(oauth -> {
            oauth.loginPage("/login")
                 .successHandler(handler);
        });

        // Logout configuration
        http.logout(logout -> {
            logout.logoutUrl("/do-logout")
                  .logoutSuccessUrl("/login?logout=true");
        });

        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
