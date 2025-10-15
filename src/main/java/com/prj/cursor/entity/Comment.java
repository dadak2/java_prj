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
 * 댓글 엔티티 클래스
 * 
 * JPA를 사용하여 데이터베이스의 'comments' 테이블과 매핑되는 엔티티입니다.
 * Spring Data JPA의 Auditing 기능을 사용하여 생성일시와 수정일시를 자동으로 관리합니다.
 * 
 * 주요 기능:
 * - 댓글 기본 정보 관리 (내용, 작성자)
 * - 게시글과의 연관관계 설정
 * - 댓글 활성화 상태 관리
 * - 생성일시 및 수정일시 자동 기록
 * 
 * @author Cursor Project
 * @version 1.0
 * @since 2024
 */
@Entity
@Table(name = "comments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Comment {
    
    /**
     * 댓글 고유 ID
     * 자동 증가하는 기본키
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentNo;
    
    /**
     * 댓글 내용
     * 필수 입력 항목, 최대 1000자
     */
    @Column(nullable = false, length = 1000)
    private String content;
    
    /**
     * 작성자 ID
     * User 엔티티와의 관계 설정
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    @JsonIgnore
    private User nickname;
    
    /**
     * 게시글 ID
     * Board 엔티티와의 관계 설정
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_no", nullable = false)
    @JsonIgnore
    private Board board;
    
    /**
     * 댓글 활성화 상태
     * true: 활성화된 댓글 (표시됨)
     * false: 비활성화된 댓글 (숨김)
     */
    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;
    
    /**
     * 생성일시
     * 자동으로 현재 시간 설정
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 수정일시
     * 자동으로 현재 시간 업데이트
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * 댓글 내용 업데이트
     * 
     * 댓글 내용을 업데이트하고 수정일시를 현재 시간으로 설정합니다.
     * 
     * @param content 수정할 댓글 내용
     */
    public void updateContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 댓글 비활성화
     * 
     * 댓글을 비활성화하여 화면에 표시되지 않도록 합니다.
     */
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 댓글 활성화
     * 
     * 댓글을 활성화하여 화면에 표시되도록 합니다.
     */
    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 작성자 닉네임 반환
     * 
     * 작성자의 닉네임만 반환하여 개인정보를 보호합니다.
     * 
     * @return 작성자 닉네임
     */
    public String getAuthorNickname() {
        return nickname != null ? nickname.getNickname() : null;
    }
    
    /**
     * 작성자 이메일 반환 (마스킹)
     * 
     * 작성자의 이메일을 마스킹하여 반환합니다.
     * 
     * @return 마스킹된 작성자 이메일
     */
    public String getAuthorMaskedEmail() {
        if (nickname == null || nickname.getEmail() == null) {
            return null;
        }
        
        String email = nickname.getEmail();
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email;
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
}
