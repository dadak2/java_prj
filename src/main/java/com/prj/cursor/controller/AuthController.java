package com.prj.cursor.controller;

import com.prj.cursor.dto.UserResponse;
import com.prj.cursor.dto.UserSignupRequest;
import com.prj.cursor.dto.UserLoginRequest;
import com.prj.cursor.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 인증 관련 REST API 컨트롤러
 * 
 * 사용자 인증과 관련된 HTTP 요청을 처리하는 REST 컨트롤러입니다.
 * Spring MVC의 @RestController 어노테이션을 사용하여 JSON 응답을 자동으로 처리합니다.
 * 
 * 주요 기능:
 * - 회원가입 API
 * - 로그인 API
 * - 사용자명 중복 확인 API
 * - 이메일 중복 확인 API
 * 
 * API 엔드포인트:
 * - POST /api/auth/signup - 회원가입
 * - POST /api/auth/login - 로그인
 * - GET /api/auth/check-nickname/{nickname} - 사용자명 중복 확인
 * - GET /api/auth/check-email/{email} - 이메일 중복 확인
 * 
 * @author Cursor Project
 * @version 1.0
 * @since 2024
 * @see UserService
 * @see UserSignupRequest
 * @see UserResponse
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    /**
     * 사용자 비즈니스 로직 서비스
     * 
     * Spring의 의존성 주입을 통해 UserService 인스턴스를 주입받습니다.
     * final 키워드를 사용하여 불변성을 보장합니다.
     */
    private final UserService userService;
    
    /**
     * 회원가입 API
     * 
     * 새로운 사용자 계정을 생성하는 REST API 엔드포인트입니다.
     * 
     * 처리 과정:
     * 1. 요청 데이터 유효성 검사 (@Valid 어노테이션)
     * 2. UserService를 통한 회원가입 처리
     * 3. 성공 시 201 Created 상태 코드와 함께 응답
     * 4. 실패 시 400 Bad Request 상태 코드와 함께 오류 메시지 반환
     * 
     * HTTP 메서드: POST
     * URL: /api/auth/signup
     * Content-Type: application/json
     * 
     * @param request 회원가입 요청 데이터 (JSON 형태)
     * @return ResponseEntity 객체 (성공/실패 여부와 메시지 포함)
     * @throws IllegalArgumentException 검증 실패 시 발생
     * @see UserSignupRequest
     * @see UserResponse
     * @see ResponseEntity
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserSignupRequest request) {
        try {
            // UserService를 통해 회원가입 처리
            UserResponse userResponse = userService.signup(request);
            
            // 성공 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "회원가입이 완료되었습니다.");
            response.put("user", userResponse);
            
            // 201 Created 상태 코드와 함께 성공 응답 반환
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            // 검증 실패 시 오류 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            // 400 Bad Request 상태 코드와 함께 오류 응답 반환
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 사용자명 중복 확인 API
     * 
     * 회원가입 시 사용자명의 중복 여부를 확인하는 REST API 엔드포인트입니다.
     * 
     * 처리 과정:
     * 1. 사용자명으로 사용자 조회 시도
     * 2. 사용자가 존재하면 중복으로 판단
     * 3. 사용자가 존재하지 않으면 사용 가능으로 판단
     * 4. 결과를 JSON 형태로 반환
     * 
     * HTTP 메서드: GET
     * URL: /api/auth/check-nickname/{nickname}
     * 
     * @param nickname 확인할 사용자명 (URL 경로 변수)
     * @return ResponseEntity 객체 (사용 가능 여부와 메시지 포함)
     * @see ResponseEntity
     */
    @GetMapping("/check-nickname/{nickname}")
    public ResponseEntity<?> checkNickname(@PathVariable String nickname) {
        // 응답 데이터를 담을 Map 객체 생성
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 사용자명으로 사용자 조회 시도
            userService.findByNickname(nickname);
            
            // 사용자가 존재하는 경우 - 중복으로 판단
            response.put("available", false);
            response.put("message", "이미 사용 중인 사용자별명입니다.");
            
        } catch (IllegalArgumentException e) {
            // 사용자가 존재하지 않는 경우 - 사용 가능으로 판단
            response.put("available", true);
            response.put("message", "사용 가능한 사용자별명입니다.");
        }
        
        // 200 OK 상태 코드와 함께 응답 반환
        return ResponseEntity.ok(response);
    }
    
    /**
     * 로그인 API
     * 
     * 사용자 인증을 처리하는 REST API 엔드포인트입니다.
     * 
     * 처리 과정:
     * 1. 요청 데이터 유효성 검사 (@Valid 어노테이션)
     * 2. 사용자명 또는 이메일로 사용자 조회
     * 3. 비밀번호 검증
     * 4. 성공 시 사용자 정보와 함께 응답
     * 5. 실패 시 적절한 오류 메시지 반환
     * 
     * HTTP 메서드: POST
     * URL: /api/auth/login
     * Content-Type: application/json
     * 
     * @param request 로그인 요청 데이터 (JSON 형태)
     * @return ResponseEntity 객체 (성공/실패 여부와 사용자 정보 포함)
     * @throws IllegalArgumentException 인증 실패 시 발생
     * @see UserLoginRequest
     * @see UserResponse
     * @see ResponseEntity
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest request) {
        try {
            // UserService를 통해 로그인 처리
            UserResponse userResponse = userService.login(request);
            
            // 성공 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "로그인이 완료되었습니다.");
            response.put("user", userResponse);
            
            // 200 OK 상태 코드와 함께 성공 응답 반환
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            // 인증 실패 시 오류 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            // 401 Unauthorized 상태 코드와 함께 오류 응답 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    /**
     * 이메일 중복 확인 API
     * 
     * 회원가입 시 이메일 주소의 중복 여부를 확인하는 REST API 엔드포인트입니다.
     * 
     * 처리 과정:
     * 1. 이메일로 사용자 조회 시도
     * 2. 사용자가 존재하면 중복으로 판단
     * 3. 사용자가 존재하지 않으면 사용 가능으로 판단
     * 4. 결과를 JSON 형태로 반환
     * 
     * HTTP 메서드: GET
     * URL: /api/auth/check-email/{email}
     * 
     * @param email 확인할 이메일 주소 (URL 경로 변수)
     * @return ResponseEntity 객체 (사용 가능 여부와 메시지 포함)
     * @see ResponseEntity
     */
    @GetMapping("/check-email/{email}")
    public ResponseEntity<?> checkEmail(@PathVariable String email) {
        // 응답 데이터를 담을 Map 객체 생성
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 이메일로 사용자 조회 시도
            userService.findByEmail(email);
            
            // 사용자가 존재하는 경우 - 중복으로 판단
            response.put("available", false);
            response.put("message", "이미 사용 중인 이메일입니다.");
            
        } catch (IllegalArgumentException e) {
            // 사용자가 존재하지 않는 경우 - 사용 가능으로 판단
            response.put("available", true);
            response.put("message", "사용 가능한 이메일입니다.");
        }
        
        // 200 OK 상태 코드와 함께 응답 반환
        return ResponseEntity.ok(response);
    }
} 