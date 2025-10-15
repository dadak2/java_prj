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
                // 모든 정적 리소스 허용
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico", "/error").permitAll()
                // 모든 HTML 페이지 허용 (클라이언트에서 로그인 체크)
                .requestMatchers("/*.html").permitAll()
                .requestMatchers("/").permitAll()
                // API 엔드포인트들
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/boards/**").permitAll()
                .requestMatchers("/api/comments/**").permitAll()
                .requestMatchers("/api/game/**").permitAll()
                // WebSocket 엔드포인트
                .requestMatchers("/ws/**").permitAll()
                // 기타
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/health", "/test").permitAll()
                .anyRequest().permitAll()
            );
        
        return http.build();
    }
} 