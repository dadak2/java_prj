package com.prj.cursor.dto;

import com.prj.cursor.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 정보 응답 DTO
 * 
 * 클라이언트에게 전달할 사용자 정보를 담는 DTO입니다.
 * 보안상 민감한 정보(password 등)는 제외하고
 * 필요한 정보만 포함합니다.
 * 
 * @author Cursor Project
 * @version 1.0
 * @since 2024
 * @see User
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    
    /**
     * 사용자 번호
     */
    private Long userNo;
    
    /**
     * 사용자 닉네임
     */
    private String nickname;
    
    /**
     * 이메일 주소
     */
    private String email;
    
    /**
     * 사용자 역할
     */
    private User.UserRole userRole;
    
    /**
     * 계정 활성화 상태
     */
    private boolean isActive;
    
    /**
     * 생성일시
     */
    private LocalDateTime createdAt;
    
    /**
     * 수정일시
     */
    private LocalDateTime updatedAt;
    
    /**
     * User 엔티티로부터 UserInfoResponse를 생성하는 정적 팩토리 메서드
     * 
     * @param user User 엔티티
     * @return UserInfoResponse 객체
     */
    public static UserInfoResponse from(User user) {
        return UserInfoResponse.builder()
                .userNo(user.getUserNo())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .userRole(user.getUserRole())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
    
    /**
     * 마스킹된 이메일을 반환
     * 
     * 이메일의 일부를 '*'로 마스킹하여 개인정보를 보호합니다.
     * 예: test@example.com -> t***@example.com
     * 
     * @return 마스킹된 이메일
     */
    public String getMaskedEmail() {
        if (email == null || email.isEmpty()) {
            return "";
        }
        
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email; // @가 없거나 첫 번째 문자 뒤에 @가 있는 경우
        }
        
        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex);
        
        if (localPart.length() <= 1) {
            return email;
        }
        
        String maskedLocalPart = localPart.charAt(0) + 
                "*".repeat(localPart.length() - 1);
        
        return maskedLocalPart + domainPart;
    }
    
    /**
     * 마스킹된 닉네임을 반환
     * 
     * 닉네임의 일부를 '*'로 마스킹하여 개인정보를 보호합니다.
     * 예: "홍길동" -> "홍*동"
     * 
     * @return 마스킹된 닉네임
     */
    public String getMaskedNickname() {
        if (nickname == null || nickname.isEmpty()) {
            return "";
        }
        
        if (nickname.length() <= 2) {
            return nickname;
        }
        
        return nickname.charAt(0) + 
               "*".repeat(nickname.length() - 2) + 
               nickname.charAt(nickname.length() - 1);
    }
} 