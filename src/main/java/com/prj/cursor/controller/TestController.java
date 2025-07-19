package com.prj.cursor.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 테스트용 컨트롤러
 * 애플리케이션 연결 상태를 확인하기 위한 엔드포인트 제공
 */
@RestController
public class TestController {
    
    /**
     * 애플리케이션 상태 확인
     * @return 상태 메시지
     */
    @GetMapping("/test")
    public String test() {
        return "Cursor 애플리케이션이 정상적으로 실행 중입니다!";
    }
    
    /**
     * 헬스체크 엔드포인트
     * @return 헬스 상태
     */
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
} 