package com.prj.cursor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/").permitAll()
                .requestMatchers("/index.html").permitAll()
                .requestMatchers("/signup.html").permitAll()
                .requestMatchers("/login.html").permitAll()
                .requestMatchers("/board.html").permitAll()
                .requestMatchers("/write.html").permitAll()
                .requestMatchers("/detail.html").permitAll()
                .requestMatchers("/css/**").permitAll()
                .requestMatchers("/js/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/boards/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/health").permitAll()
                .requestMatchers("/test").permitAll()
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
} 