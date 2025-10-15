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

@Entity
@Table(name = "boards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Board {
    
    /**
     * 게시글 고유 ID
     * 자동 증가하는 기본키
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardNo;
    
    /**
     * 게시글 제목
     * 필수 입력 항목, 최대 200자
     */
    @Column(nullable = false, length = 200)
    private String title;
    
    /**
     * 게시글 내용
     * 필수 입력 항목, 텍스트 타입 (긴 텍스트)
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    /**
     * 게시글 카테고리
     * 일반, 기술, 질문, 자유 등
     */
    @Column(length = 50)
    private String category;
    
    /**
     * 작성자 ID
     * User 엔티티와의 관계 설정
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    @JsonIgnore
    private User author;
    
    /**
     * 조회수
     * 기본값 0
     */
    @Column(columnDefinition = "BIGINT DEFAULT 0")
    private Long viewCount = 0L;
    
    /**
     * 좋아요 수
     * 기본값 0
     */
    @Column(columnDefinition = "BIGINT DEFAULT 0")
    private Long likeCount = 0L;
    
    /**
     * 댓글 수
     * 기본값 0
     */
    @Column(columnDefinition = "BIGINT DEFAULT 0")
    private Long commentCount = 0L;
    
    /**
     * 게시글 상태
     * ACTIVE: 활성, DELETED: 삭제됨
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private BoardStatus status = BoardStatus.ACTIVE;
    
    /**
     * 게시글 활성화 상태
     * true: 활성화된 게시글 (표시됨)
     * false: 비활성화된 게시글 (숨김)
     */
    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;
    
    /**
     * 생성일시
     * 자동으로 현재 시간 설정
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 수정일시
     * 자동으로 현재 시간 업데이트
     */
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * 게시글 정보 업데이트
     * 
     * 제목, 내용, 카테고리를 업데이트하고 수정일시를 현재 시간으로 설정합니다.
     * 
     * @param title 수정할 제목
     * @param content 수정할 내용
     * @param category 수정할 카테고리
     */
    public void updateBoard(String title, String content, String category) {
        this.title = title;
        this.content = content;
        this.category = category;
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
        return author != null ? author.getNickname() : null;
    }
    
    /**
     * 작성자 이메일 반환 (마스킹)
     * 
     * 작성자의 이메일을 마스킹하여 반환합니다.
     * 
     * @return 마스킹된 작성자 이메일
     */
    public String getAuthorMaskedEmail() {
        if (author == null || author.getEmail() == null) {
            return null;
        }
        
        String email = author.getEmail();
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
    
    /**
     * 게시글 비활성화
     * 
     * 게시글을 비활성화하여 화면에 표시되지 않도록 합니다.
     */
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 게시글 활성화
     * 
     * 게시글을 활성화하여 화면에 표시되도록 합니다.
     */
    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 게시글 상태 열거형
     */
    public enum BoardStatus {
        ACTIVE,    // 활성
        DELETED    // 삭제됨
    }
}
