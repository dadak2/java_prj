package com.prj.cursor.repository;

import com.prj.cursor.entity.Board;
import com.prj.cursor.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 댓글 데이터 접근 레포지토리
 * 
 * JPA를 사용하여 댓글 데이터베이스 작업을 수행하는 레포지토리 인터페이스입니다.
 * Spring Data JPA의 기본 CRUD 기능과 커스텀 쿼리를 제공합니다.
 * 
 * 주요 기능:
 * - 댓글 CRUD 작업
 * - 게시글별 댓글 조회
 * - 활성화 상태별 댓글 조회
 * - 댓글 활성화/비활성화 작업
 * 
 * @author Cursor Project
 * @version 1.0
 * @since 2024
 * @see Comment
 * @see Board
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    /**
     * 게시글별 활성 댓글 조회
     * 
     * 특정 게시글의 활성화된 댓글들을 생성일시 오름차순으로 조회합니다.
     * 
     * @param board 게시글 엔티티
     * @param isActive 활성화 상태
     * @param pageable 페이징 정보
     * @return 댓글 페이지
     */
    Page<Comment> findByBoardAndIsActiveOrderByCreatedAtAsc(Board board, boolean isActive, Pageable pageable);
    
    /**
     * 게시글별 모든 댓글 조회 (페이징 없음)
     * 
     * 특정 게시글의 모든 댓글을 생성일시 오름차순으로 조회합니다.
     * 
     * @param board 게시글 엔티티
     * @return 댓글 목록
     */
    List<Comment> findByBoardOrderByCreatedAtAsc(Board board);
    
    /**
     * 게시글별 활성 댓글 수 조회
     * 
     * 특정 게시글의 활성화된 댓글 수를 조회합니다.
     * 
     * @param board 게시글 엔티티
     * @param isActive 활성화 상태
     * @return 댓글 수
     */
    long countByBoardAndIsActive(Board board, boolean isActive);
    
    /**
     * 게시글의 모든 댓글 비활성화
     * 
     * 특정 게시글의 모든 댓글을 비활성화합니다.
     * 
     * @param boardNo 게시글 번호
     */
    @Modifying
    @Query("UPDATE Comment c SET c.isActive = false WHERE c.board.boardNo = :boardNo")
    void deactivateAllByBoardNo(@Param("boardNo") Long boardNo);
    
    /**
     * 게시글의 모든 댓글 활성화
     * 
     * 특정 게시글의 모든 댓글을 활성화합니다.
     * 
     * @param boardNo 게시글 번호
     */
    @Modifying
    @Query("UPDATE Comment c SET c.isActive = true WHERE c.board.boardNo = :boardNo")
    void activateAllByBoardNo(@Param("boardNo") Long boardNo);
    
    /**
     * 댓글 비활성화
     * 
     * 특정 댓글을 비활성화합니다.
     * 
     * @param commentNo 댓글 번호
     */
    @Modifying
    @Query("UPDATE Comment c SET c.isActive = false WHERE c.commentNo = :commentNo")
    void deactivateByCommentNo(@Param("commentNo") Long commentNo);
    
    /**
     * 댓글 활성화
     * 
     * 특정 댓글을 활성화합니다.
     * 
     * @param commentNo 댓글 번호
     */
    @Modifying
    @Query("UPDATE Comment c SET c.isActive = true WHERE c.commentNo = :commentNo")
    void activateByCommentNo(@Param("commentNo") Long commentNo);
}
