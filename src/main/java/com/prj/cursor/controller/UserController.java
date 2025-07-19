package com.prj.cursor.controller;

import com.prj.cursor.dto.UserResponse;
import com.prj.cursor.dto.UserInfoResponse;
import com.prj.cursor.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 사용자 관리 REST API 컨트롤러
 * 
 * 사용자 정보 조회 및 계정 관리와 관련된 HTTP 요청을 처리하는 REST 컨트롤러입니다.
 * Spring MVC의 @RestController 어노테이션을 사용하여 JSON 응답을 자동으로 처리합니다.
 * 
 * 주요 기능:
 * - 활성 사용자 목록 조회
 * - 사용자명으로 사용자 조회
 * - 사용자 계정 활성화/비활성화 (이메일 기반)
 * 
 * API 엔드포인트:
 * - GET /api/users - 모든 활성 사용자 조회
 * - GET /api/users/{username} - 사용자명으로 사용자 조회
 * - PUT /api/users/{email}/deactivate - 사용자 계정 비활성화
 * - PUT /api/users/{email}/activate - 사용자 계정 활성화
 * 
 * @author Cursor Project
 * @version 1.0
 * @since 2024
 * @see UserService
 * @see UserResponse
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    /**
     * 사용자 비즈니스 로직 서비스
     * 
     * Spring의 의존성 주입을 통해 UserService 인스턴스를 주입받습니다.
     * final 키워드를 사용하여 불변성을 보장합니다.
     */
    private final UserService userService;
    
    /**
     * 모든 활성 사용자 조회 API
     * 
     * 시스템에 등록된 모든 활성화된 사용자 목록을 조회합니다.
     * 
     * HTTP 메서드: GET
     * URL: /api/users
     * 
     * @return ResponseEntity 객체 (활성 사용자 목록과 개수 포함)
     * @see UserResponse
     * @see ResponseEntity
     */
    @GetMapping
    public ResponseEntity<?> getAllActiveUsers() {
        try {
            // 활성 사용자 목록 조회
            List<UserResponse> users = userService.findAllActiveUsers();
            
            // 성공 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("users", users);
            response.put("count", users.size());
            
            // 200 OK 상태 코드와 함께 성공 응답 반환
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // 오류 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "사용자 목록을 조회하는 중 오류가 발생했습니다: " + e.getMessage());
            
            // 500 Internal Server Error 상태 코드와 함께 오류 응답 반환
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 사용자명으로 사용자 조회 API
     * 
     * 사용자명을 기준으로 특정 사용자의 정보를 조회합니다.
     * 
     * HTTP 메서드: GET
     * URL: /api/users/{username}
     * 
     * @param username 조회할 사용자명 (URL 경로 변수)
     * @return ResponseEntity 객체 (사용자 정보 포함)
     * @see UserResponse
     * @see ResponseEntity
     */
    @GetMapping("/{nickname}")
    public ResponseEntity<?> getUserByNickname(@PathVariable String nickname) {
        try {
            // 사용자명으로 사용자 조회
            UserResponse user = userService.findByNickname(nickname);
            
            // 성공 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", user);
            
            // 200 OK 상태 코드와 함께 성공 응답 반환
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            // 사용자를 찾을 수 없는 경우 오류 응답
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            // 404 Not Found 상태 코드와 함께 오류 응답 반환
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 사용자 계정 비활성화 API
     * 
     * 이메일을 기준으로 사용자 계정을 비활성화합니다.
     * 비활성화된 계정은 로그인이 불가능합니다.
     * 
     * HTTP 메서드: PUT
     * URL: /api/users/{email}/deactivate
     * 
     * @param email 비활성화할 사용자의 이메일 주소 (URL 경로 변수)
     * @return ResponseEntity 객체 (처리 결과 메시지 포함)
     * @see ResponseEntity
     */
    @PutMapping("/{email}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable String email) {
        try {
            // 사용자 계정 비활성화 처리
            userService.deactivateUser(email);
            
            // 성공 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "사용자가 비활성화되었습니다.");
            
            // 200 OK 상태 코드와 함께 성공 응답 반환
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            // 사용자를 찾을 수 없는 경우 오류 응답
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            // 404 Not Found 상태 코드와 함께 오류 응답 반환
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 사용자 계정 활성화 API
     * 
     * 이메일을 기준으로 비활성화된 사용자 계정을 다시 활성화합니다.
     * 활성화된 계정은 정상적으로 로그인이 가능합니다.
     * 
     * HTTP 메서드: PUT
     * URL: /api/users/{email}/activate
     * 
     * @param email 활성화할 사용자의 이메일 주소 (URL 경로 변수)
     * @return ResponseEntity 객체 (처리 결과 메시지 포함)
     * @see ResponseEntity
     */
    @PutMapping("/{email}/activate")
    public ResponseEntity<?> activateUser(@PathVariable String email) {
        try {
            // 사용자 계정 활성화 처리
            userService.activateUser(email);
            
            // 성공 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "사용자가 활성화되었습니다.");
            
            // 200 OK 상태 코드와 함께 성공 응답 반환
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            // 사용자를 찾을 수 없는 경우 오류 응답
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            // 404 Not Found 상태 코드와 함께 오류 응답 반환
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 사용자 정보 조회 API (userNo 제외)
     * 
     * 사용자명을 기준으로 특정 사용자의 정보를 조회합니다.
     * userNo와 password는 제외하고 안전한 정보만 반환합니다.
     * 
     * HTTP 메서드: GET
     * URL: /api/users/info/{nickname}
     * 
     * @param nickname 조회할 사용자명 (URL 경로 변수)
     * @return ResponseEntity 객체 (사용자 정보 포함, userNo 제외)
     * @see UserInfoResponse
     * @see ResponseEntity
     */
    @GetMapping("/info/{nickname}")
    public ResponseEntity<?> getUserInfoByNickname(@PathVariable String nickname) {
        try {
            // 사용자명으로 사용자 조회
            UserResponse user = userService.findByNickname(nickname);
            
            // UserInfoResponse로 변환
            UserInfoResponse userInfo = UserInfoResponse.builder()
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .userRole(user.getUserRole())
                    .isActive(user.isActive())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .build();
            
            // 성공 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userInfo", userInfo);
            
            // 200 OK 상태 코드와 함께 성공 응답 반환
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            // 사용자를 찾을 수 없는 경우 오류 응답
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            // 404 Not Found 상태 코드와 함께 오류 응답 반환
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 마스킹된 사용자 정보 조회 API
     * 
     * 사용자명을 기준으로 특정 사용자의 정보를 조회합니다.
     * 이메일과 닉네임이 마스킹되어 개인정보가 보호됩니다.
     * 
     * HTTP 메서드: GET
     * URL: /api/users/masked/{nickname}
     * 
     * @param nickname 조회할 사용자명 (URL 경로 변수)
     * @return ResponseEntity 객체 (마스킹된 사용자 정보 포함)
     * @see UserInfoResponse
     * @see ResponseEntity
     */
    @GetMapping("/masked/{nickname}")
    public ResponseEntity<?> getMaskedUserInfoByNickname(@PathVariable String nickname) {
        try {
            // 사용자명으로 사용자 조회
            UserResponse user = userService.findByNickname(nickname);
            
            // UserInfoResponse로 변환
            UserInfoResponse userInfo = UserInfoResponse.builder()
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .userRole(user.getUserRole())
                    .isActive(user.isActive())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .build();
            
            // 마스킹된 정보를 포함한 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userInfo", userInfo);
            response.put("maskedEmail", userInfo.getMaskedEmail());
            response.put("maskedNickname", userInfo.getMaskedNickname());
            
            // 200 OK 상태 코드와 함께 성공 응답 반환
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            // 사용자를 찾을 수 없는 경우 오류 응답
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            // 404 Not Found 상태 코드와 함께 오류 응답 반환
            return ResponseEntity.notFound().build();
        }
    }
} 