package com.prj.cursor.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 사용자 엔티티 클래스
 * 
 * JPA를 사용하여 데이터베이스의 'users' 테이블과 매핑되는 엔티티입니다.
 * Spring Data JPA의 Auditing 기능을 사용하여 생성일시와 수정일시를 자동으로 관리합니다.
 * 
 * 주요 기능:
 * - 사용자 기본 정보 관리 (사용자명, 이메일, 비밀번호)
 * - 사용자 역할 관리 (USER, ADMIN)
 * - 계정 활성화 상태 관리
 * - 생성일시 및 수정일시 자동 기록
 * 
 * @author Cursor Project
 * @version 1.0
 * @since 2024
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {
    
    /**
     * 사용자 고유 번호 (Primary Key)
     * 
     * 데이터베이스에서 자동으로 생성되는 고유 식별자입니다.
     * IDENTITY 전략을 사용하여 데이터베이스의 AUTO_INCREMENT 기능을 활용합니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long userNo;
    
    /**
     * 사용자 닉네임
     * 
     * 최대 50자까지 입력 가능하며, 중복을 허용하지 않습니다.
     * 영문, 숫자, 언더스코어만 사용 가능합니다.
     */
    @Column(unique = true, nullable = false, length = 50)
    private String nickname;
    
    /**
     * 이메일 주소
     * 
     * 사용자의 이메일 주소로, 로그인 및 비밀번호 재설정에 사용됩니다.
     * 최대 100자까지 입력 가능하며, 중복을 허용하지 않습니다.
     * 유효한 이메일 형식이어야 합니다.
     */
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    /**
     * 비밀번호
     * 
     * 사용자 인증을 위한 암호화된 비밀번호입니다.
     * Spring Security의 BCryptPasswordEncoder를 사용하여 암호화됩니다.
     * 최소 8자 이상이어야 합니다.
     */
    @Column(nullable = false)
    @JsonIgnore
    private String password;
    
    /**
     * 사용자 역할
     * 
     * 시스템 내에서 사용자의 권한을 정의하는 역할입니다.
     * USER: 일반 사용자 권한
     * ADMIN: 관리자 권한
     * 
     * @see UserRole
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    @Builder.Default
    private UserRole userRole = UserRole.USER;
    
    /**
     * 계정 활성화 상태
     * 
     * 사용자 계정의 활성화 여부를 나타냅니다.
     * true: 활성화된 계정 (로그인 가능)
     * false: 비활성화된 계정 (로그인 불가)
     */
    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;
    
    /**
     * 생성일시
     * 
     * 사용자 계정이 생성된 날짜와 시간입니다.
     * Spring Data JPA Auditing을 통해 자동으로 설정되며, 수정할 수 없습니다.
     */
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 수정일시
     * 
     * 사용자 정보가 마지막으로 수정된 날짜와 시간입니다.
     * Spring Data JPA Auditing을 통해 자동으로 업데이트됩니다.
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * 사용자 역할 열거형
     * 
     * 시스템에서 사용할 수 있는 사용자 역할을 정의합니다.
     */
    public enum UserRole {
        /** 일반 사용자 */
        USER,
        /** 관리자 */
        ADMIN
    }
} 