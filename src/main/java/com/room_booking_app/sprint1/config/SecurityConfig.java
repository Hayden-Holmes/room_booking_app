package com.room_booking_app.sprint1.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

   @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // allow static assets
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                // allow login/logout endpoints
                .requestMatchers("/login", "/logout").permitAll()

                // PUBLIC search endpoints 
                .requestMatchers("/", "/search").permitAll()


                // EVERYTHING else requires login
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .defaultSuccessUrl("/search", true)
                .permitAll()

                )
            .logout(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

  

}