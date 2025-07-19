package com.prj.cursor.dto;

import com.prj.cursor.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 게시글 응답 DTO
 * 
 * 클라이언트에게 전달할 게시글 정보를 담는 DTO입니다.
 * 보안상 민감한 정보는 제외하고 필요한 정보만 포함합니다.
 * 
 * @author Cursor Project
 * @version 1.0
 * @since 2024
 * @see Board
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponse {
    
    /**
     * 게시글 번호
     */
    private Long boardNo;
    
    /**
     * 게시글 제목
     */
    private String title;
    
    /**
     * 게시글 내용
     */
    private String content;
    
    /**
     * 게시글 카테고리
     */
    private String category;
    
    /**
     * 작성자 닉네임
     */
    private String authorNickname;
    
    /**
     * 작성자 마스킹된 이메일
     */
    private String authorMaskedEmail;
    
    /**
     * 조회수
     */
    private Long viewCount;
    
    /**
     * 좋아요 수
     */
    private Long likeCount;
    
    /**
     * 댓글 수
     */
    private Long commentCount;
    
    /**
     * 게시글 상태
     */
    private Board.BoardStatus status;
    
    /**
     * 생성일시
     */
    private LocalDateTime createdAt;
    
    /**
     * 수정일시
     */
    private LocalDateTime updatedAt;
    
    /**
     * Board 엔티티로부터 BoardResponse를 생성하는 정적 팩토리 메서드
     * 
     * @param board Board 엔티티
     * @return BoardResponse 객체
     */
    public static BoardResponse from(Board board) {
        return BoardResponse.builder()
                .boardNo(board.getBoardNo())
                .title(board.getTitle())
                .content(board.getContent())
                .category(board.getCategory())
                .authorNickname(board.getAuthorNickname())
                .authorMaskedEmail(board.getAuthorMaskedEmail())
                .viewCount(board.getViewCount())
                .likeCount(board.getLikeCount())
                .commentCount(board.getCommentCount())
                .status(board.getStatus())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
    }
    
    /**
     * 내용 미리보기 반환
     * 
     * 게시글 내용의 일부만 반환하여 목록에서 사용할 수 있도록 합니다.
     * 
     * @param maxLength 최대 길이 (기본값: 100)
     * @return 미리보기 내용
     */
    public String getContentPreview(int maxLength) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        
        if (content.length() <= maxLength) {
            return content;
        }
        
        return content.substring(0, maxLength) + "...";
    }
    
    /**
     * 내용 미리보기 반환 (기본 길이: 100자)
     * 
     * @return 미리보기 내용
     */
    public String getContentPreview() {
        return getContentPreview(100);
    }
    
    /**
     * 게시글 상태가 활성인지 확인
     * 
     * @return 활성 상태 여부
     */
    public boolean isActive() {
        return status == Board.BoardStatus.ACTIVE;
    }
    
    /**
     * 게시글이 삭제되었는지 확인
     * 
     * @return 삭제 상태 여부
     */
    public boolean isDeleted() {
        return status == Board.BoardStatus.DELETED;
    }
} 