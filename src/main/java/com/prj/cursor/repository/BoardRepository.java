package com.prj.cursor.repository;

import com.prj.cursor.entity.Board;
import com.prj.cursor.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 게시판 데이터 접근 계층
 * 
 * 게시글과 관련된 모든 데이터베이스 작업을 처리하는 Repository입니다.
 * Spring Data JPA의 JpaRepository를 상속받아 기본 CRUD 기능을 제공하며,
 * 게시판에 특화된 커스텀 쿼리 메서드들을 추가로 제공합니다.
 * 
 * 주요 기능:
 * - 기본 CRUD 작업 (생성, 조회, 수정, 삭제)
 * - 페이징 처리
 * - 검색 및 필터링
 * - 통계 정보 업데이트
 * 
 * @author Cursor Project
 * @version 1.0
 * @since 2024
 * @see Board
 * @see User
 */
@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    
    /**
     * 활성 상태의 모든 게시글을 페이징하여 조회
     * 
     * @param pageable 페이징 정보
     * @return 활성 게시글 페이지
     */
    Page<Board> findByIsActiveOrderByCreatedAtDesc(boolean isActive, Pageable pageable);
    
    /**
     * 상태와 활성화 상태로 게시글 조회
     * 
     * @param isActive 게시글 상태
     * @param pageable 페이징 정보
     * @return 게시글 페이지
     */
    Page<Board> findByStatusAndIsActiveOrderByCreatedAtDesc(Board.BoardStatus status, boolean isActive, Pageable pageable);
    
    /**
     * 카테고리별 활성 게시글 조회
     * 
     * @param category 게시글 카테고리
     * @param isActive 활성화 상태
     * @param pageable 페이징 정보
     * @return 카테고리별 게시글 페이지
     */
    Page<Board> findByCategoryAndIsActiveOrderByCreatedAtDesc(
        String category, 
        boolean isActive, 
        Pageable pageable
    );
    
    /**
     * 카테고리별 활성화된 게시글 조회
     * 
     * @param category 게시글 카테고리
     * @param isActive 활성화 상태
     * @param pageable 페이징 정보
     * @return 카테고리별 활성화된 게시글 페이지
     */
    Page<Board> findByCategoryAndStatusAndIsActiveOrderByCreatedAtDesc(
        String category, 
        Board.BoardStatus status,
        boolean isActive, 
        Pageable pageable
    );
    
    /**
     * 작성자별 게시글 조회
     * 
     * @param author 게시글 작성자
     * @param isActive 활성화 상태
     * @param pageable 페이징 정보
     * @return 작성자별 게시글 페이지
     */
    Page<Board> findByAuthorAndIsActiveOrderByCreatedAtDesc(
        User author, 
        boolean isActive, 
        Pageable pageable
    );
    
    /**
     * 제목 또는 내용에 검색어가 포함된 게시글 조회
     * 
     * @param title 검색할 제목 키워드
     * @param content 검색할 내용 키워드
     * @param isActive 활성화 상태
     * @param pageable 페이징 정보
     * @return 검색 결과 페이지
     */
    @Query("SELECT b FROM Board b WHERE b.isActive = :isActive AND " +
           "(LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')) OR " +
           "LOWER(b.content) LIKE LOWER(CONCAT('%', :content, '%'))) " +
           "ORDER BY b.createdAt DESC")
    Page<Board> findByTitleContainingOrContentContainingAndIsActive(
        @Param("title") String title,
        @Param("content") String content,
        @Param("isActive") boolean isActive,
        Pageable pageable
    );
    
    /**
     * 조회수 기준으로 인기 게시글 조회
     * 
     * @param isActive 활성화 상태
     * @param pageable 페이징 정보
     * @return 조회수 기준 인기 게시글 페이지
     */
    Page<Board> findByIsActiveOrderByViewCountDescCreatedAtDesc(
        boolean isActive, 
        Pageable pageable
    );
    
    /**
     * 좋아요 수 기준으로 인기 게시글 조회
     * 
     * @param isActive 활성화 상태
     * @param pageable 페이징 정보
     * @return 좋아요 수 기준 인기 게시글 페이지
     */
    Page<Board> findByIsActiveOrderByLikeCountDescCreatedAtDesc(
        boolean isActive, 
        Pageable pageable
    );
    
    /**
     * 댓글 수 기준으로 인기 게시글 조회
     * 
    * @param isActive 활성화 상태
     * @param pageable 페이징 정보
     * @return 댓글 수 기준 인기 게시글 페이지
     */
    Page<Board> findByIsActiveOrderByCommentCountDescCreatedAtDesc(
        boolean isActive, 
        Pageable pageable
    );
    
    /**
     * 게시글 조회수 증가
     * 
     * @param boardNo 게시글 번호
     */
    @Modifying
    @Query("UPDATE Board b SET b.viewCount = b.viewCount + 1 WHERE b.boardNo = :boardNo")
    void incrementViewCount(@Param("boardNo") Long boardNo);
    
    /**
     * 게시글 좋아요 수 증가
     * 
     * @param boardNo 게시글 번호
     */
    @Modifying
    @Query("UPDATE Board b SET b.likeCount = b.likeCount + 1 WHERE b.boardNo = :boardNo")
    void incrementLikeCount(@Param("boardNo") Long boardNo);
    
    /**
     * 게시글 좋아요 수 감소
     * 
     * @param boardNo 게시글 번호
     */
    @Modifying
    @Query("UPDATE Board b SET b.likeCount = b.likeCount - 1 WHERE b.boardNo = :boardNo AND b.likeCount > 0")
    void decrementLikeCount(@Param("boardNo") Long boardNo);
    
    /**
     * 게시글 댓글 수 증가
     * 
     * @param boardNo 게시글 번호
     */
    @Modifying
    @Query("UPDATE Board b SET b.commentCount = b.commentCount + 1 WHERE b.boardNo = :boardNo")
    void incrementCommentCount(@Param("boardNo") Long boardNo);
    
    /**
     * 게시글 댓글 수 감소
     * 
     * @param boardNo 게시글 번호
     */
    @Modifying
    @Query("UPDATE Board b SET b.commentCount = b.commentCount - 1 WHERE b.boardNo = :boardNo AND b.commentCount > 0")
    void decrementCommentCount(@Param("boardNo") Long boardNo);
    
    /**
     * 게시글 상태 변경 (삭제 처리)
     * 
     * @param boardNo 게시글 번호
    * @param isActive 변경할 활성화 상태
     */
    @Modifying
    @Query("UPDATE Board b SET b.isActive = :isActive WHERE b.boardNo = :boardNo")
    void updateIsActive(@Param("boardNo") Long boardNo, @Param("isActive") boolean isActive);
    
    /**
     * 게시글 비활성화
     * 
     * @param boardNo 게시글 번호
     */
    @Modifying
    @Query("UPDATE Board b SET b.isActive = false WHERE b.boardNo = :boardNo")
    void deactivateBoard(@Param("boardNo") Long boardNo);
    
    /**
     * 게시글 활성화
     * 
     * @param boardNo 게시글 번호
     */
    @Modifying
    @Query("UPDATE Board b SET b.isActive = true WHERE b.boardNo = :boardNo")
    void activateBoard(@Param("boardNo") Long boardNo);
    
    /**
     * 작성자의 게시글 수 조회
     * 
     * @param author 작성자
     * @param status 게시글 상태
     * @return 게시글 수
     */
    long countByAuthorAndIsActive(User author, boolean isActive);
    
    /**
     * 카테고리별 게시글 수 조회
     * 
     * @param category 카테고리
     * @param status 게시글 상태
     * @return 게시글 수
     */
    long countByCategoryAndIsActive(String category, boolean isActive);
    
    /**
     * 전체 활성 게시글 수 조회
     * 
     * @param isActive 게시글 상태
     * @return 게시글 수
     */
    long countByIsActive(boolean isActive);
    
    /**
     * 최근 게시글 조회 (메인 페이지용)
     * 
     * @param status 게시글 상태
     * @param limit 조회할 게시글 수
     * @return 최근 게시글 목록
     */
    @Query("SELECT b FROM Board b WHERE b.isActive = :isActive ORDER BY b.createdAt DESC")
    List<Board> findRecentBoards(@Param("isActive") boolean isActive, Pageable pageable);
    
    /**
     * 인기 게시글 조회 (메인 페이지용)
     * 
     * @param status 게시글 상태
     * @param limit 조회할 게시글 수
     * @return 인기 게시글 목록
     */
    @Query("SELECT b FROM Board b WHERE b.isActive = :isActive ORDER BY b.viewCount DESC, b.createdAt DESC")
    List<Board> findPopularBoards(@Param("isActive") boolean isActive, Pageable pageable);    
} 