package com.prj.cursor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사용자 로그인 요청 DTO
 * 
 * 로그인 API에서 사용되는 요청 데이터 전송 객체입니다.
 * 사용자명 또는 이메일과 비밀번호를 포함하며, 로그인 상태 유지 옵션도 제공합니다.
 * 
 * 주요 필드:
 * - loginInfo: 사용자명 또는 이메일 (고유 식별자)
 * - password: 사용자 비밀번호
 * - rememberMe: 로그인 상태 유지 여부
 * 
 * @author Cursor Project
 * @version 1.0
 * @since 2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequest {
    
    /**
     * 사용자별명 또는 이메일
     * 
     * 로그인 시 사용자별명 또는 이메일 중 하나를 입력받습니다.
     * 빈 값이 아니어야 하며, 최소 3자 이상이어야 합니다.
     */
    @NotBlank(message = "사용자별명 또는 이메일을 입력해주세요.")
    @Size(min = 3, message = "사용자별명 또는 이메일은 최소 3자 이상이어야 합니다.")
    private String loginInfo;
    
    /**
     * 사용자 비밀번호
     * 
     * 로그인 시 사용할 비밀번호입니다.
     * 빈 값이 아니어야 하며, 최소 6자 이상이어야 합니다.
     */
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    private String password;
    
    /**
     * 로그인 상태 유지 여부
     * 
     * true인 경우 로그인 세션을 더 오래 유지합니다.
     * 기본값은 false입니다.
     */
    private boolean rememberMe = false;
} 