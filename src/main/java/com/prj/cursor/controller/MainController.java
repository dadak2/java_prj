package com.prj.cursor.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 메인 페이지 컨트롤러
 * 
 * 루트 경로 접속 시 로그인 페이지로 리다이렉트하는 컨트롤러입니다.
 * 
 * @author Cursor Project
 * @version 1.0
 * @since 2024
 */
@Slf4j
@Controller
public class MainController {
    
    /**
     * 루트 경로 접속 시 로그인 페이지로 리다이렉트
     * 
     * 사용자가 http://localhost:9090/ 에 접속하면
     * 자동으로 로그인 페이지로 이동합니다.
     * 
     * @return 로그인 페이지로 리다이렉트
     */
    @GetMapping("/")
    public String redirectToLogin() {
        log.info("루트 경로 접속 - 로그인 페이지로 리다이렉트");
        return "redirect:/login.html";
    }
    
    /**
     * 메인 페이지 접속 시 로그인 페이지로 리다이렉트
     * 
     * 사용자가 http://localhost:9090/main 에 접속하면
     * 자동으로 로그인 페이지로 이동합니다.
     * 
     * @return 로그인 페이지로 리다이렉트
     */
    @GetMapping("/main")
    public String redirectToLoginFromMain() {
        log.info("메인 페이지 접속 - 로그인 페이지로 리다이렉트");
        return "redirect:/login.html";
    }
    
    /**
     * 홈 페이지 접속 시 로그인 페이지로 리다이렉트
     * 
     * 사용자가 http://localhost:9090/home 에 접속하면
     * 자동으로 로그인 페이지로 이동합니다.
     * 
     * @return 로그인 페이지로 리다이렉트
     */
    @GetMapping("/home")
    public String redirectToLoginFromHome() {
        log.info("홈 페이지 접속 - 로그인 페이지로 리다이렉트");
        return "redirect:/login.html";
    }
} 